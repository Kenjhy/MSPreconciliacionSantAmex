package mx.com.nmp.mspreconciliacion.services.impl;

import static mx.com.nmp.mspreconciliacion.util.XLSXUtil.buildCellStyle;
import static mx.com.nmp.mspreconciliacion.util.XLSXUtil.createCellWithCurrenyFormat;
import static mx.com.nmp.mspreconciliacion.util.XLSXUtil.setCellStyleRow;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mx.com.nmp.mspreconciliacion.helper.ComisionesBancariasHelper;
import mx.com.nmp.mspreconciliacion.model.config.ColumnaArchivo;
import mx.com.nmp.mspreconciliacion.model.dto.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.google.gson.Gson;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.consumer.rest.ComisionesMIDAsService;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.CuotaDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.ListaCuotaDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.RequestComisionesMIDAsDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.ResponseComisionesMIDAsDTO;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.exceptions.SistemaException;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.model.enums.EstadoPagosEnum;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoEPA;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoEPARepository;
import mx.com.nmp.mspreconciliacion.services.ComsionBancariaService;
import mx.com.nmp.mspreconciliacion.util.CatalogoComision;
import mx.com.nmp.mspreconciliacion.util.FechaUtil;
import mx.com.nmp.mspreconciliacion.util.Utils;

@Service
public class ComisionBancariaAMEXServiceImpl extends CatalogoComision implements ComsionBancariaService {

	@Autowired
	private ElasticsearchOperations elasticTemplate;

	@Autowired
	private IPagoConciliadoEPARepository pagoConciliadoEPARepository;

	@Autowired
	private ComisionesBancariasHelper comisionesBancariasHelper;


	@Autowired
	private ComisionesMIDAsService comisionesMIDAsService;

	private static final Logger LOG = LoggerFactory.getLogger(ComisionBancariaAMEXServiceImpl.class);


	@Override
	public ComisionesPaginadoDTO consultarComisiones(RequestComisionesDTO comisionesDTO) throws SistemaException, PagoException {
		LOG.info("Inicia ejecucion consulta comisiones AMEX");
		ComisionesPaginadoDTO resultado = null;
		List<ComisionesDTO> comisionesAMEXList = new ArrayList<>();
		Page<PagoConciliadoEPA> resultadoEPA= null;

		try {
			if (comisionesDTO != null && comisionesDTO.getCorresponsal() != null && comisionesDTO.getCorresponsal().trim().length() > 0 && comisionesDTO.getCorresponsal().trim().equals(CorresponsalEnum.AMEX.getNombre())) {

				if (comisionesDTO.getRequierePaginado() != null) {
					List<ComisionesDTO>  resultadoTotal = null;

					resultadoEPA= buscarComisiones(comisionesDTO, comisionesDTO.getRequierePaginado());
					comisionesAMEXList=  mapearComisiones(resultadoEPA.getContent());

					if (!comisionesAMEXList.isEmpty()) {
						resultado = asignarResultadoConsulta( comisionesDTO, resultadoEPA, comisionesAMEXList);

						if (Boolean.TRUE.equals(comisionesDTO.getRequierePaginado())){
							Page<PagoConciliadoEPA> result = buscarComisiones(comisionesDTO,Boolean.FALSE);
							resultadoTotal = mapearComisiones(result.getContent());

							comisionesBancariasHelper.sumarTotalesComisiones(resultado, resultadoTotal, comisionesDTO.getRequierePaginado());
						}
					}
				}else {
					LOG.info(PagoException.ERROR_DEVOLUCION_PAGINADO.getDescripcion());
					throw PagoException.ERROR_DEVOLUCION_PAGINADO;
				}

			}

		}catch (PagoException |SistemaException ex ) {
			LOG.info("Error Comisiones (PagoException |SistemaException): {}", ex.getMessage());
			throw ex;
		}

		catch(Exception ex) {
			LOG.info("Error Comisiones (Exception): {}", ex.getMessage());
		}

		LOG.info("Termina ejecucion consulta comisiones AMEX");
		return resultado;
	}



	private ComisionesPaginadoDTO asignarResultadoConsulta(RequestComisionesDTO comisionesDTO, Page<PagoConciliadoEPA> resultadoEPA, List<ComisionesDTO> comisionesAMEXList) throws SistemaException, PagoException {
		ComisionesPaginadoDTO resultado = new ComisionesPaginadoDTO();
		List<ComisionesDTO>  comisionesAplicadas= aplicarComisionesMidas(comisionesAMEXList, comisionesDTO);
		resultado.setComisionesList(comisionesAplicadas);
		resultado.setNumeroPagina(resultadoEPA.getNumber());
		resultado.setNumeroRegistros(resultadoEPA.getNumberOfElements());
		resultado.setTotalRegistros((int) resultadoEPA.getTotalElements());
		resultado.setTieneMasPaginas(resultadoEPA.hasNext()) ;
		return resultado;
	}

	private List<ComisionesDTO> mapearComisiones(List<PagoConciliadoEPA> pagosEPA)  {
		List<ComisionesDTO> resultado = new ArrayList<>();
		if (!pagosEPA.isEmpty()) {
			//mapear resultado
			pagosEPA.forEach(p-> {
				ComisionesDTO comisionEPA= new ComisionesDTO();
				if (p.getCorresponsal()!= null ) {
					comisionEPA.setBanco("AMEX"); //Se asigna según DR 3B pagina17
					comisionEPA.setSucursal(p.getCorresponsal().getSucursal());
					comisionEPA.setFechaLiquidacion(p.getCorresponsal().getFechaLiquidacion());
					comisionEPA.setFechaVenta(p.getCorresponsal().getFechaOperacion());

					//Para que el correcto cálculo de Comision transaccional, el campo importe = importe Bruto
					// Ya que EPA manda la información calculada
					comisionEPA.setImporteNeto(revisarImporte(p.getCorresponsal().getImporteBruto()));

					//comisión reportada por EPA
					comisionEPA.setComisionTransaccionalEPA(revisarImporte(p.getCorresponsal().getComisionTransaccionalEPA()));
					comisionEPA.setComisionTransaccional(revisarImporte(p.getCorresponsal().getComisionTransaccional()));
					comisionEPA.setIvaTransaccional(BigDecimal.valueOf(p.getCorresponsal().getIvaTransaccional()));
					comisionEPA.setSobretasa(BigDecimal.valueOf(p.getCorresponsal().getSobretasa()));
					comisionEPA.setIvaSobretasa(BigDecimal.valueOf(p.getCorresponsal().getIvaSobretasa()));
					comisionEPA.setTipoTarjeta(p.getCorresponsal().getTipoTarjeta());
					comisionEPA.setPlazo(p.getCorresponsal().getNumeroMesesPromocion());
					comisionEPA.setTipoPago(p.getCorresponsal().getTipoPago());
					comisionEPA.setIdPago(p.getIdPago());
					//Se cálcula monto Total
					comisionEPA.setMontoTotal(Utils.calcularMontoTotal( comisionEPA.getImporteNeto(), comisionEPA.getComisionTransaccional(), comisionEPA.getIvaTransaccional(),comisionEPA.getSobretasa(),comisionEPA.getIvaSobretasa() ));
					resultado.add(comisionEPA);
				}
			});
		}

		return resultado;
	}

	private BigDecimal revisarImporte(BigDecimal valor) {
		BigDecimal contenido= BigDecimal.ZERO;

		if (valor != null) {
			contenido= valor;
		}
		return contenido;
	}


	private BigDecimal obtenerComisionTC(ListaCuotaDTO detalle) {
		BigDecimal comisionTCredito= BigDecimal.ZERO;
		if (detalle != null && detalle.getCuota()!= null) {
			Optional<CuotaDTO> obtenerCuota= detalle.getCuota().stream().filter(cuo-> cuo.getTarjeta().contains("INTERNACIONAL")).findFirst();
			//F-tipo fijo
			//P-tipo porcentaje
			if (obtenerCuota.isPresent()) {
				if (obtenerCuota.get().getTipo().equals("F")){
					LOG.info("comisionTCAMEX- tipo F");
					comisionTCredito= obtenerCuota.get().getComision();
				}else if(obtenerCuota.get().getTipo().equals("P")){
					LOG.info("comisionTCAMEX- tipo P");
					comisionTCredito= obtenerCuota.get().getComision().divide(BigDecimal.valueOf(100));
				}
			}
		}
		return comisionTCredito;
	}

	private List<ComisionesDTO>  aplicarComisionesMidas(List<ComisionesDTO> comisionesIndices, RequestComisionesDTO comisionesDTO) throws SistemaException, PagoException{
		String filtroMidasJ=null; 
		BigDecimal comisionTCredito= BigDecimal.ZERO;
		ResponseComisionesMIDAsDTO comisionesMIDAS2= null;
		try {
			RequestComisionesMIDAsDTO filtroMidas = new RequestComisionesMIDAsDTO();
			filtroMidas.setCorresponsal(comisionesDTO.getCorresponsal());
			filtroMidas.setFecha(FechaUtil.convierteFechaaCadena(comisionesDTO.getFecha(), FechaUtil.FORMATO_WSCOMISIONES, FechaUtil.ZONE_AMERICA));
			filtroMidasJ= new Gson().toJson(filtroMidas);
			
			LOG.info("filtroWSMIDAS, {}", filtroMidasJ);
			comisionesMIDAS2 = comisionesMIDAsService.consultarComisionesMIDAS(filtroMidas);

			//Recorrer lista de midas, para asociar a comisiones de Indices.
			//Obtener comisionTransaccionalMIDAS
			if (comisionesMIDAS2 != null && comisionesMIDAS2.getCuotas() != null) {
				//Sobretasa- Se obtiene de Crédito, es la unica tarjeta que se maneja-AMEX
				comisionTCredito= obtenerComisionTC(comisionesMIDAS2.getCuotas());
				LOG.info("comisionTarjetaCreditoAMEX: {}", comisionTCredito);
			}

			LOG.info("start Asignar sobretasaMIDAS");
			//Asignar sobretasaMIDAS
			if (comisionesMIDAS2 != null && comisionesMIDAS2.getCampanias() != null &&  !comisionesMIDAS2.getCampanias().getCampania().isEmpty()) {

				//Recorrer lista de comisiones
				for(ComisionesDTO com: comisionesIndices) {
					//Se filtra la comisión por sucursal y plazo
					comisionesMIDAS2.getCampanias().getCampania().stream().filter(campa-> campa.getSucursal().equals(com.getSucursal())  && com.getImporteNeto().doubleValue() >= campa.getMontoMinimo().doubleValue()  && campa.getPlazo().equals(com.getPlazo())).forEach(banc-> {
						BigDecimal comisionf=  banc.getBancos().getBanco().stream().filter(amex-> amex.getBanco().equals("AMERICAN EXPRESS")).findFirst().orElse(null).getSobretasa();
						com.setSobretasaMIDAS(comisionf);
					});
					com.setComisionTransaccionalMIDAS(comisionTCredito.setScale(2, RoundingMode.DOWN));
				}
			}
			LOG.info("end Asignar sobretasaMIDAS");



		}catch(Exception ex) {
			LOG.info("catch aplicarComisionesMidas");
			LOG.error(PagoException.ERROR_WS_OAG_MIDAS_COMISIONES.getDescripcion(), ex.getMessage());
			throw PagoException.ERROR_WS_OAG_MIDAS_COMISIONES;
		}

		return comisionesIndices;
	}

	private boolean validaFiltroSucursal(RequestComisionesDTO comisionesDTO) {
		boolean tieneFiltro= false;
		if (comisionesDTO.getSucursal() != null && comisionesDTO.getSucursal().trim().length() > 0)
			tieneFiltro = true;

		return tieneFiltro;
	}

	private BoolQueryBuilder aplicarFiltros(RequestComisionesDTO comisionesDTO) throws PagoException{
		BoolQueryBuilder builder= QueryBuilders.boolQuery();
		//Construir filtros dinámicos
		try {
			//Filtro default- solo ventas
			builder.must( QueryBuilders.matchPhraseQuery("corresponsal.tipoOperacion", Constants.TIPO_OPERACION_VENTA));
	        builder.must( QueryBuilders.matchPhraseQuery("estado", EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos()));
			
			//Armado Query
			if (validaFiltroSucursal(comisionesDTO)) {
				builder.must( QueryBuilders.matchPhraseQuery("corresponsal.sucursal", comisionesDTO.getSucursal()));
			}

			if (comisionesDTO.getTipoPago() != null && comisionesDTO.getTipoPago().trim().length() > 0) {

				if (!comisionesDTO.getTipoPago().equals("CONTADO")) {
					builder.must( QueryBuilders.matchPhraseQuery("corresponsal.numeroMesesPromocion", comisionesDTO.getTipoPago().split(" ")[0]));
				}else {
					builder.must( QueryBuilders.matchPhraseQuery("corresponsal.tipoPago", removeAccent(comisionesDTO.getTipoPago())));
				}
			}

			if (comisionesDTO.getTipoTarjeta() != null && comisionesDTO.getTipoTarjeta().trim().length() > 0) {
				builder.must( QueryBuilders.matchBoolPrefixQuery("corresponsal.tipoTarjeta", removeAccent(comisionesDTO.getTipoTarjeta())));
			}

			//Filtro Banco, siempre sera AMEX
			if (comisionesDTO.getBanco() != null && comisionesDTO.getBanco().trim().length() > 0 && !comisionesDTO.getBanco().equals("AMEX")) {
				LOG.info(PagoException.ERROR_AMEX_BANCO.getDescripcion());
				throw PagoException.ERROR_AMEX_BANCO;
			}
			if (comisionesDTO.getFecha() != null) {
				Calendar ini = Calendar.getInstance();
				Calendar fin = Calendar.getInstance();
				ini.setTime( comisionesDTO.getFecha());
				fin.setTime( comisionesDTO.getFecha());

				ini.set(Calendar.HOUR_OF_DAY, 0);
				ini.set(Calendar.MINUTE, 0);
				ini.set(Calendar.SECOND, 0);
				ini.set(Calendar.MILLISECOND, 0);

				fin.set(Calendar.HOUR_OF_DAY, 23);
				fin.set(Calendar.MINUTE, 59);
				fin.set(Calendar.SECOND, 59);
				fin.set(Calendar.MILLISECOND, 59);

				builder.filter(QueryBuilders.rangeQuery("corresponsal.fechaOperacion")
						.gte(ini.getTimeInMillis())
						.lte(fin.getTimeInMillis()));
			}
			else {
				LOG.info(PagoException.ERROR_CONCILIACION_FECHA.getDescripcion());
				throw PagoException.ERROR_CONCILIACION_FECHA;
			}


		}catch(PagoException exp) {
			LOG.info("Error Comisiones-aplicarFiltros (PagoException): {}", exp.getMessage());
			throw exp;
		}
		catch(Exception ex) {
			LOG.info("Error Comisiones-aplicarFiltros (Exception): {}", ex.getMessage());
		}

		return builder;
	}

	private Page<PagoConciliadoEPA> buscarComisiones(RequestComisionesDTO comisionesDTO, boolean paginado) throws PagoException{
		BoolQueryBuilder query= null;
		Pageable pageable = null;

		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		query= aplicarFiltros(comisionesDTO);
		nativeSearchQueryBuilder.withQuery(query);

		if (paginado) {
			if (comisionesDTO.getNumeroRegistros() > 0) {
				pageable = PageRequest.of(comisionesDTO.getNumeroPagina(), comisionesDTO.getNumeroRegistros());
				nativeSearchQueryBuilder.withPageable(pageable);
			}else {
				LOG.info(PagoException.ERROR_DEVOLUCION_PAGINADO.getDescripcion());
				throw PagoException.ERROR_DEVOLUCION_PAGINADO;
			}
		}
		NativeSearchQuery n = nativeSearchQueryBuilder.build();
		SearchHits<PagoConciliadoEPA> list = elasticTemplate.search(n, PagoConciliadoEPA.class);
		return new PageImpl<>(list.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList()), paginado ? pageable : Pageable.unpaged(), list.getTotalHits());
	}

	@Override
	public String reporteComisiones(RequestComisionesDTO comisionesDTO) throws SistemaException, PagoException{
		LOG.info("Inicia ejecucion reporteComisiones AMEX");
		String arreglo64= null;
		ByteArrayOutputStream reporte;
		List<ComisionesDTO> lista = null;
		List<ComisionesDTO> listaComisiones = null;
		List<ComisionesDetalleDTO> listaComisionesDetalle = new ArrayList<>();

		Page<PagoConciliadoEPA> resultado= buscarComisiones(comisionesDTO, false);
		if (resultado.getTotalElements()> 0) {
			lista= mapearComisiones(resultado.getContent());
			LOG.info("start aplicarComisionesMidas");
			listaComisiones= aplicarComisionesMidas(lista, comisionesDTO);
			LOG.info("end aplicarComisionesMidas");

			for (ComisionesDTO detalle : listaComisiones) {
				ComisionesDetalleDTO comisionesDetalleDTO = new ComisionesDetalleDTO();
				comisionesDetalleDTO.setBanco(detalle.getBanco());
				comisionesDetalleDTO.setSucursal(detalle.getSucursal());
				comisionesDetalleDTO.setFechaVenta(detalle.getFechaVenta());
				comisionesDetalleDTO.setImporteNeto(detalle.getImporteNeto());
				comisionesDetalleDTO.setComisionTransaccional(detalle.getComisionTransaccional());
				comisionesDetalleDTO.setIvaTransaccional(detalle.getIvaTransaccional());
				comisionesDetalleDTO.setSobretasa(detalle.getSobretasa());
				comisionesDetalleDTO.setIvaSobretasa(detalle.getIvaSobretasa());
				comisionesDetalleDTO.setSobretasaMIDAS(detalle.getSobretasaMIDAS()!=null?detalle.getSobretasaMIDAS(): BigDecimal.valueOf(0.0));
				comisionesDetalleDTO.setComisionTransaccionalMIDAS(detalle.getComisionTransaccionalMIDAS()!=null?detalle.getComisionTransaccionalMIDAS(): BigDecimal.valueOf(0.0));
				comisionesDetalleDTO.setComisionTransaccionalEPA(detalle.getComisionTransaccionalEPA());
				comisionesDetalleDTO.setMontoTotal(detalle.getMontoTotal().setScale(2,RoundingMode.HALF_UP));
				listaComisionesDetalle.add(comisionesDetalleDTO);
			}
			LOG.info("end listaComisionesDetalle");

			try {
				//reporte = reporteExcelComisiones(listaComisiones);

				LOG.info("start reporteExcelComisionesFile");
				reporte = reporteExcelComisionesFile(listaComisionesDetalle);
				LOG.info("end reporteExcelComisionesFile");

				/*
				byte[] byteArray = reporte.toByteArray();
				String filePath = "C:\\pack\\example_amex.csv";
				FileOutputStream fileOutputStream = new FileOutputStream(filePath);
				fileOutputStream.write(byteArray);
				fileOutputStream.close();
*/

			} catch (IOException e) {
				throw PagoException.ERROR_REPORTE_COMISIONES;
			}
			arreglo64 = Base64Utils.encodeToString(reporte.toByteArray());
		}
		LOG.info("Finaliza ejecucion reporteComisiones AMEX- arreglo64");
		LOG.info("arreglo64: {}", arreglo64);
		return arreglo64;
	}


	public ByteArrayOutputStream reporteExcelComisiones(List<ComisionesDTO> comisionesBancarias) throws IOException {
		XSSFWorkbook reporte = new XSSFWorkbook();
		Sheet sheet = reporte.createSheet("Comisiones AMEX");
		int numRow = 0;
		Row headerData = sheet.createRow(numRow++);
		final CellStyle headerStyle = buildCellStyle(reporte, IndexedColors.WHITE, IndexedColors.RED, true);
		int indice= 0;
		CellUtil.createCell(headerData,indice++,"Banco", headerStyle);
		CellUtil.createCell(headerData,indice++,"Sucursal", headerStyle);
		CellUtil.createCell(headerData,indice++,"Fecha", headerStyle);
		CellUtil.createCell(headerData,indice++,"Importe", headerStyle);
		CellUtil.createCell(headerData,indice++,"Comisión transaccionalidad", headerStyle);
		CellUtil.createCell(headerData,indice++,"IVA Com Tran", headerStyle);
		CellUtil.createCell(headerData,indice++,"Sobre Tasa", headerStyle);
		CellUtil.createCell(headerData,indice++,"IVA SobreTasa", headerStyle);
		CellUtil.createCell(headerData,indice++,"Comisión MIDAS Sobretasa", headerStyle);
		CellUtil.createCell(headerData,indice++,"Comisión MIDAS Transaccional", headerStyle);
		CellUtil.createCell(headerData,indice++,"Comisión EPA Transaccional", headerStyle);
		CellUtil.createCell(headerData,indice++,"Monto Total", headerStyle);

		///datos
		numRow= procesarDatosReporte(reporte, sheet, numRow, comisionesBancarias);
		final int initRowData = numRow - comisionesBancarias.size();
		final int finalRowData = numRow;

		final CellStyle totalStyle = buildCellStyle(reporte, IndexedColors.BLACK, IndexedColors.GREY_25_PERCENT, true);
		Row total = sheet.createRow(numRow);
		CellUtil.createCell(total,6,"Total:", totalStyle);
		createCellWithCurrenyFormat(reporte, total, 7, MessageFormat.format("SUM(H{0}:H{1})", initRowData, finalRowData), true, totalStyle);
		createCellWithCurrenyFormat(reporte, total, 8, MessageFormat.format("SUM(I{0}:I{1})", initRowData, finalRowData), true, totalStyle);
		createCellWithCurrenyFormat(reporte, total, 9, MessageFormat.format("SUM(J{0}:J{1})", initRowData, finalRowData), true, totalStyle);
		createCellWithCurrenyFormat(reporte, total, 10, MessageFormat.format("SUM(K{0}:K{1})", initRowData, finalRowData), true, totalStyle);
		createCellWithCurrenyFormat(reporte, total, 11, MessageFormat.format("SUM(L{0}:L{1})", initRowData, finalRowData), true, totalStyle);
		setCellStyleRow(total,totalStyle,0,5);

		// Ajustamos las columnas al contenido
		for(int i=0; i< indice; i++) {
			sheet.autoSizeColumn(i);
		}
		//generamos el archivo a bytes
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		reporte.write(outputStream);
		reporte.close();
		return outputStream;
	}

	public ByteArrayOutputStream reporteExcelComisionesFile
			(List<ComisionesDetalleDTO> comisionesBancarias) throws IOException {
		LOG.info("[ComisionBancariaAMEXServiceImpl] Starting method reporteExcelComisionesFile");
		Date fechaProceso = new Date();
		String nameDoc = "Comisiones AMEX_" + String.valueOf(fechaProceso.getTime()) + ".csv";
		File file = new File(nameDoc);
			try {

				LOG.info("[ComisionBancariaAMEXServiceImpl] Starting create file");
				Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
				BufferedWriter writer = new BufferedWriter(fileWriter);
				String nameColumnas = getNombreColumnas();
				writer.write(nameColumnas);

				BigDecimal totalImporteNeto = BigDecimal.ZERO;
				BigDecimal totalComisionTransaccional = BigDecimal.ZERO;
				BigDecimal totalIvaComisionTransaccional = BigDecimal.ZERO;
				BigDecimal totalSobretasa = BigDecimal.ZERO;
				BigDecimal totalIvaSobretasa = BigDecimal.ZERO;
				BigDecimal totalMontoTotal = BigDecimal.ZERO;
				for (ComisionesDetalleDTO detalle : comisionesBancarias) {
					//escribe datos
					writer.write("\n" + detalle.toStringSCV());
					//calculo totales
					totalImporteNeto = totalImporteNeto.add(detalle.getImporteNeto());
					totalComisionTransaccional = totalComisionTransaccional.add(detalle.getComisionTransaccional());
					totalIvaComisionTransaccional= totalIvaComisionTransaccional.add(detalle.getIvaTransaccional());
					totalSobretasa= totalSobretasa.add(detalle.getSobretasa());
					totalIvaSobretasa = totalIvaSobretasa.add(detalle.getIvaSobretasa());
					totalMontoTotal = totalMontoTotal.add(detalle.getMontoTotal() );
				}
				String separador = ",";
				//escribe lista de totales
				writer.write("\n" +
								"" + separador +
								"" + separador +
								"Total:" + separador +
								totalImporteNeto.toString()+ separador +
								totalComisionTransaccional.toString()+ separador +
								totalIvaComisionTransaccional.toString()+ separador +
								totalSobretasa.toString()+ separador +
								totalIvaSobretasa.toString()+ separador +
								"" + separador +
								"" + separador +
								"" + separador +
								totalMontoTotal.toString()
						);


				writer.close();
				LOG.info("[ComisionBancariaAMEXServiceImpl] Ending create file");

				LOG.info("[ComisionBancariaAMEXServiceImpl] Starting create byteArrayOutputStream");
				// Create a FileInputStream to read the file
				FileInputStream fileInputStream = new FileInputStream(file);
				// Create a ByteArrayOutputStream to store the file content
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int bytesRead;
				// Read from the file and write to the ByteArrayOutputStream
				while ((bytesRead = fileInputStream.read(buffer)) != -1) {
					byteArrayOutputStream.write(buffer, 0, bytesRead);
				}
				// Close the FileInputStream
				fileInputStream.close();
				byteArrayOutputStream.close();
				LOG.info("[ComisionBancariaAMEXServiceImpl] Ending create byteArrayOutputStream");

				LOG.info("[ComisionBancariaAMEXServiceImpl] Ending method reporteExcelComisionesFile");
				return byteArrayOutputStream;
			} catch (Exception e) {
				LOG.error("FileManagerServiceImpl. Error al generar documento", e);
				throw e;
			} finally {
				file.delete();
			}
	}

	private String getNombreColumnas() {
		Class<ComisionesDetalleDTO> columnClass = ComisionesDetalleDTO.class;
		StringBuilder columnas = new StringBuilder();
		String separador = "";
		Field[] campos = columnClass.getDeclaredFields();
		for (Field campo : campos) {
			if (campo.isAnnotationPresent(ColumnaArchivo.class)){
				ColumnaArchivo columnaArchivo = campo.getAnnotation(ColumnaArchivo.class);
				columnas.append(separador.trim()).append(columnaArchivo.nombreColumna());
				separador = ",";
			}
		}
		return columnas.toString();
	}


	@Override
	public CatalogoComisionesDTO consultarCatalogoComisiones(Date fechaOperacion) {
		LOG.info("Fecha en valor Long {}",fechaOperacion.getTime());
		List<PagoConciliadoEPA> responseElastic= pagoConciliadoEPARepository.findByFechaOperacion(FechaUtil.obtenerFechaIni(fechaOperacion).getTime(), FechaUtil.obtenerFechaFin(fechaOperacion).getTime());
		return buildCatalogoComisiones(responseElastic);
	}

	private CatalogoComisionesDTO buildCatalogoComisiones(List<PagoConciliadoEPA> pagoConciliadoEPA) {
		return contruyeCatalogoComision(CorresponsalEnum.AMEX, null, pagoConciliadoEPA);
	}


	private int procesarDatosReporte(XSSFWorkbook reporte, Sheet sheet, int numRow, List<ComisionesDTO> comisionesBancarias) {
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		for (ComisionesDTO comisionesBancariasDTO: comisionesBancarias) {
			int indice= 0;
			final Row rowData = sheet.createRow(numRow++);

			rowData.createCell(indice++).setCellValue(obtenerTexto(comisionesBancariasDTO.getBanco()));
			rowData.createCell(indice++).setCellValue(obtenerTexto(comisionesBancariasDTO.getSucursal()));
			rowData.createCell(indice++).setCellValue(comisionesBancariasDTO.getFechaVenta()!= null?formatoFecha.format(comisionesBancariasDTO.getFechaVenta()):"");
			createCellWithCurrenyFormat(reporte, rowData, indice++, obtenerMonto(comisionesBancariasDTO.getImporteNeto()), false, null);
			createCellWithCurrenyFormat(reporte, rowData, indice++, obtenerMonto(comisionesBancariasDTO.getComisionTransaccional()), false, null);
			createCellWithCurrenyFormat(reporte, rowData, indice++, obtenerMonto(comisionesBancariasDTO.getIvaTransaccional()), false, null);
			createCellWithCurrenyFormat(reporte, rowData, indice++, obtenerMonto(comisionesBancariasDTO.getSobretasa()), false, null);
			createCellWithCurrenyFormat(reporte, rowData, indice++, obtenerMonto(comisionesBancariasDTO.getIvaSobretasa()), false, null);
			createCellWithCurrenyFormat(reporte, rowData, indice++, obtenerMonto(comisionesBancariasDTO.getSobretasaMIDAS()), false, null);
			createCellWithCurrenyFormat(reporte, rowData, indice++, obtenerMonto(comisionesBancariasDTO.getComisionTransaccionalMIDAS()), false, null);
			createCellWithCurrenyFormat(reporte, rowData, indice++, obtenerMonto(comisionesBancariasDTO.getComisionTransaccionalEPA()), false, null);
			createCellWithCurrenyFormat(reporte, rowData, indice,  obtenerMonto(comisionesBancariasDTO.getMontoTotal()), false, null);
		}
		return numRow;
	}

	private String obtenerTexto(String valor) {
		String texto= "";
		if (valor != null) {
			texto = valor;
		}
		return texto;
	}

	private String obtenerMonto (BigDecimal valor) {
		String monto= "0";
		if (valor != null) {
			monto = valor.toString();
		}
		return monto;
	}
}
