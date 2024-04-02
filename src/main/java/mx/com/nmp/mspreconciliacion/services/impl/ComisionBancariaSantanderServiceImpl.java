package mx.com.nmp.mspreconciliacion.services.impl;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.SistemaException;
import mx.com.nmp.mspreconciliacion.helper.ComisionesBancariasHelper;
import mx.com.nmp.mspreconciliacion.model.config.ColumnaArchivo;
import mx.com.nmp.mspreconciliacion.model.dto.*;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.model.enums.EstadoPagosEnum;
import mx.com.nmp.mspreconciliacion.model.enums.TipoTarjetaEnum;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoMIT;
import mx.com.nmp.mspreconciliacion.services.ComsionBancariaService;
import mx.com.nmp.mspreconciliacion.util.CatalogoComision;
import mx.com.nmp.mspreconciliacion.util.Utils;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static mx.com.nmp.mspreconciliacion.exceptions.SistemaException.INTERNAL_SERVER_ERROR;
import static mx.com.nmp.mspreconciliacion.util.XLSXUtil.buildCellStyle;
import static mx.com.nmp.mspreconciliacion.util.XLSXUtil.createCellWithCurrenyFormat;
import static mx.com.nmp.mspreconciliacion.util.XLSXUtil.setCellStyleRow;


@Service
public class ComisionBancariaSantanderServiceImpl extends CatalogoComision implements ComsionBancariaService  {

    private static final Logger LOG = LoggerFactory.getLogger(ComisionBancariaSantanderServiceImpl.class);

    private static final BigDecimal IVA = BigDecimal.valueOf(0.16f);

    private static final String NOMBRE_SHEET = "Reporte de Comisiones Bancarias SANTANDER";

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    /**
     * Zona horaria
     */
    @Value("${TZ}")
    private String timeZoneId;


    /**
     * Referencia al template {@link ElasticsearchRestTemplate}
     */
    @Autowired
    private ElasticsearchRestTemplate elasticTemplate;

    @Autowired
    private ComisionesBancariasHelper comisionesBancariasHelper;

    private BigDecimal obtenerMontoTotal(BigDecimal importe, BigDecimal comisionTransaccional,
                                         BigDecimal ivaTransaccionalidad, BigDecimal sobreTasa,
                                         BigDecimal ivaSobreTasa){
        BigDecimal montoTotal = null;
        montoTotal = importe
                .subtract(comisionTransaccional)
                .subtract(ivaTransaccionalidad)
                .subtract(sobreTasa)
                .subtract(ivaSobreTasa);
        return  montoTotal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String reporteComisiones(RequestComisionesDTO comisionesDTO) throws SistemaException {

        comisionesDTO.setRequierePaginado(Boolean.FALSE);

        //obtenemos los pagos del proveedor Santander
        Page<PagoConciliadoMIT> pagosConciliados = findComisiones(comisionesDTO);
        List<ComisionesBancariasDTO> comisionesBancarias = new ArrayList<>();
        List<ComisionesBancariasDetalleDTO> comisionesBancariasDetalle = new ArrayList<>();
        if (pagosConciliados.hasContent()){
            //mapeamos los pagos para el reporte
            comisionesBancarias = pagosConciliados.stream().map(
                    pagoConciliadoMIT -> mapToComisionBancaria(pagoConciliadoMIT)).collect(Collectors.toList());

            for (ComisionesBancariasDTO detalle : comisionesBancarias) {
                ComisionesBancariasDetalleDTO comisionesBancariasDetalleDTO = new ComisionesBancariasDetalleDTO();
                comisionesBancariasDetalleDTO.setSucursal(detalle.getSucursal());
                comisionesBancariasDetalleDTO.setFecha(detalle.getFecha());
                comisionesBancariasDetalleDTO.setImporte(detalle.getImporte());
                comisionesBancariasDetalleDTO.setEmisor(detalle.getEmisor());
                comisionesBancariasDetalleDTO.setBanco(detalle.getBanco());
                comisionesBancariasDetalleDTO.setTipoTarjeta(detalle.getTipoTarjeta());
                comisionesBancariasDetalleDTO.setTipoPago(detalle.getTipoPago());
                comisionesBancariasDetalleDTO.setComisionTransaccional(detalle.getComisionTransaccional());
                comisionesBancariasDetalleDTO.setIvaTransaccionalidad(detalle.getIvaTransaccionalidad());
                comisionesBancariasDetalleDTO.setSobreTasa(detalle.getSobreTasa());
                comisionesBancariasDetalleDTO.setIvaSobreTasa(detalle.getIvaSobreTasa());
                comisionesBancariasDetalleDTO.setMontoTotal(
                        obtenerMontoTotal(
                                detalle.getImporte(),
                                detalle.getComisionTransaccional(),
                                detalle.getIvaTransaccionalidad(),
                                detalle.getSobreTasa(),
                                detalle.getIvaSobreTasa()
                                ).setScale(2,RoundingMode.HALF_UP)
                );
                comisionesBancariasDetalle.add(comisionesBancariasDetalleDTO);
            }
        }

        //generamos el reporte
        ByteArrayOutputStream reporte = null;
        try {
            //reporte = generarReporte(comisionesBancarias, comisionesDTO.getSucursal());


            reporte = generarReporteFile(comisionesBancariasDetalle, comisionesDTO.getSucursal());

/*
            byte[] byteArray2 = reporte.toByteArray();
            String filePath2 = "C:\\pack\\example_2.csv";
            FileOutputStream fileOutputStream2 = new FileOutputStream(filePath2);
            fileOutputStream2.write(byteArray2);
            fileOutputStream2.close();
*/

        } catch (IOException e) {
            LOG.error("Ocurrio un error al generar el reporte en xlsx",e);
            throw INTERNAL_SERVER_ERROR;
        }
        return Base64Utils.encodeToString(reporte.toByteArray());
    }

    /**
     * Genera el archivo xlsx del reporte comisiones bancarias Santander
     * @param comisionesBancarias Lista con los detalles del reporte
     * @return Stream con los bites del archivo xlsx
     */
    private ByteArrayOutputStream generarReporte(List<ComisionesBancariasDTO> comisionesBancarias, String sucursal) throws IOException {

        XSSFWorkbook reporte = new XSSFWorkbook();
        Sheet sheet = reporte.createSheet(NOMBRE_SHEET);
        int numRow = 1;

        //Cabecera principal

        Row headerSucursal = sheet.createRow(numRow++);

        headerSucursal.createCell(0).setCellValue("Sucursal:");
        headerSucursal.createCell(1).setCellValue(StringUtils.hasText(sucursal) ? sucursal : "TODAS");

        numRow+=2;

        //Cabecera datos

        Row headerData = sheet.createRow(numRow++);

        final CellStyle headerStyle = buildCellStyle(reporte, IndexedColors.WHITE, IndexedColors.GREEN, true);

        CellUtil.createCell(headerData,0,"Sucursal", headerStyle);
        CellUtil.createCell(headerData,1,"Fecha", headerStyle);
        CellUtil.createCell(headerData,2,"Importe", headerStyle);
        CellUtil.createCell(headerData,3,"Emisor", headerStyle);
        CellUtil.createCell(headerData,4,"Banco", headerStyle);
        CellUtil.createCell(headerData,5,"Tipo Tarjeta", headerStyle);
        CellUtil.createCell(headerData,6,"Tipo de Pago", headerStyle);
        CellUtil.createCell(headerData,7,"Comisión transaccionalidad", headerStyle);
        CellUtil.createCell(headerData,8,"IVA transaccionalidad", headerStyle);
        CellUtil.createCell(headerData,9,"Sobre Tasa", headerStyle);
        CellUtil.createCell(headerData,10,"IVA Sobre Tasa", headerStyle);
        CellUtil.createCell(headerData,11,"Monto Total", headerStyle);

        ///datos
        for (ComisionesBancariasDTO comisionesBancariasDTO: comisionesBancarias) {

            final Row rowData = sheet.createRow(numRow);

            rowData.createCell(0).setCellValue(comisionesBancariasDTO.getSucursal());
            rowData.createCell(1).setCellValue(comisionesBancariasDTO.getFecha());
            createCellWithCurrenyFormat(reporte, rowData, 2, comisionesBancariasDTO.getImporte().toString(), false, null);
            rowData.createCell(3).setCellValue(comisionesBancariasDTO.getEmisor());
            rowData.createCell(4).setCellValue(comisionesBancariasDTO.getBanco());
            rowData.createCell(5).setCellValue(comisionesBancariasDTO.getTipoTarjeta().getNombre());
            rowData.createCell(6).setCellValue(comisionesBancariasDTO.getTipoPago());
            createCellWithCurrenyFormat(reporte, rowData, 7, comisionesBancariasDTO.getComisionTransaccional().toString(), false, null);
            createCellWithCurrenyFormat(reporte, rowData, 8, comisionesBancariasDTO.getIvaTransaccionalidad().toString(), false, null);
            createCellWithCurrenyFormat(reporte, rowData, 9, comisionesBancariasDTO.getSobreTasa().toString(), false, null);
            createCellWithCurrenyFormat(reporte, rowData, 10, comisionesBancariasDTO.getIvaSobreTasa().toString(), false, null);
            createCellWithCurrenyFormat(reporte, rowData, 11, MessageFormat.format("C{0}-H{0}-I{0}-J{0}-K{0}", ++numRow), true, null);

        }
        ///totales

        //total tarjeta debito
        Row totalDebito = sheet.createRow(numRow++);

        final int initRowData = numRow - comisionesBancarias.size();
        final int finalRowData = numRow - 1;

        final CellStyle totalStyle = buildCellStyle(reporte, IndexedColors.BLACK, IndexedColors.GREY_25_PERCENT, true);

        setCellStyleRow(totalDebito,totalStyle,1,4);
        CellUtil.createCell(totalDebito,0,"Total V/MC", totalStyle);
        createCellWithCurrenyFormat(reporte, totalDebito, 3, MessageFormat.format("(SUMIF(D{0}:D{1},\"visa\",C{0}:C{1})+SUMIF(D{0}:D{1},\"mastercard\",C{0}:C{1}))", initRowData, finalRowData), true, totalStyle);
        CellUtil.createCell(totalDebito,5,"Comisiones", totalStyle);

        CellUtil.createCell(totalDebito,6,"Total Débito:", totalStyle);
        buildTotalesByTarjeta(reporte, totalDebito, initRowData, finalRowData, TipoTarjetaEnum.DEBITO, totalStyle);


        //total tarjeta credito
        Row totalCredito = sheet.createRow(numRow++);
        setCellStyleRow(totalCredito,totalStyle,1,5);
        CellUtil.createCell(totalCredito,0,"Total AMEX:", totalStyle);
        createCellWithCurrenyFormat(reporte, totalCredito, 3, MessageFormat.format("SUMIF(D{0}:D{1},\"amex\",C{0}:C{1})", initRowData, finalRowData), true, totalStyle);
        CellUtil.createCell(totalCredito,6,"Total Crédito:", totalStyle);
        buildTotalesByTarjeta(reporte, totalCredito, initRowData, finalRowData, TipoTarjetaEnum.CREDITO, totalStyle);

        //total internacional
        Row totalInternacional = sheet.createRow(numRow++);
        CellUtil.createCell(totalInternacional,6,"Total Internacional:", totalStyle);
        buildTotalesByTarjeta(reporte, totalInternacional, initRowData, finalRowData, TipoTarjetaEnum.INTERNACIONAL, totalStyle);
        setCellStyleRow(totalInternacional,totalStyle,0,5);

        //total
        Row total = sheet.createRow(numRow);
        CellUtil.createCell(total,6,"Total:", totalStyle);
        createCellWithCurrenyFormat(reporte, total, 7, MessageFormat.format("SUM(H{0}:H{1})", initRowData, finalRowData), true, totalStyle);
        createCellWithCurrenyFormat(reporte, total, 8, MessageFormat.format("SUM(I{0}:I{1})", initRowData, finalRowData), true, totalStyle);
        createCellWithCurrenyFormat(reporte, total, 9, MessageFormat.format("SUM(J{0}:J{1})", initRowData, finalRowData), true, totalStyle);
        createCellWithCurrenyFormat(reporte, total, 10, MessageFormat.format("SUM(K{0}:K{1})", initRowData, finalRowData), true, totalStyle);
        createCellWithCurrenyFormat(reporte, total, 11, MessageFormat.format("SUM(L{0}:L{1})", initRowData, finalRowData), true, totalStyle);
        setCellStyleRow(total,totalStyle,0,5);

        //Filtros en la cabecera de los datos
        sheet.setAutoFilter(CellRangeAddress.valueOf("A"+(initRowData-1)+":K"+finalRowData));

        // Ajustamos las columnas al contenido
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.autoSizeColumn(11);

        //generamos el archivo a bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporte.write(outputStream);
        reporte.close();
        return outputStream;
    }


    private ByteArrayOutputStream generarReporteFile(
            List<ComisionesBancariasDetalleDTO> comisionesBancarias,
            String sucursal) throws IOException {
        LOG.info("FileManagerServiceImpl.generateDocumento: {}", sucursal);
        Date fechaProceso = new Date();
        String nameDoc = "Reporte de Comisiones Bancarias SANTANDER_" + String.valueOf(fechaProceso.getTime()) + ".csv";
        File file = new File(nameDoc);
        try {
            LOG.info("FileManagerServiceImpl. Inicia la generación del Documento 1");
            Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            String nameColumnas = getNombreColumnas();
            writer.write(nameColumnas);
            LOG.info("FileManagerServiceImpl. Inicia la generación del Documento 2");

            BigDecimal totalImporteNeto = BigDecimal.ZERO;
            BigDecimal totalComisionTransaccional = BigDecimal.ZERO;
            BigDecimal totalIvaComisionTransaccional = BigDecimal.ZERO;
            BigDecimal totalSobretasa = BigDecimal.ZERO;
            BigDecimal totalIvaSobretasa = BigDecimal.ZERO;
            BigDecimal totalMontoTotal = BigDecimal.ZERO;




            for (ComisionesBancariasDetalleDTO detalle : comisionesBancarias) {
                //escribe datos
                writer.write("\n" + detalle.toStringSCV());
                //calculo totales
                totalImporteNeto = totalImporteNeto.add(detalle.getImporte());
                totalComisionTransaccional = totalComisionTransaccional.add(detalle.getComisionTransaccional());
                totalIvaComisionTransaccional = totalIvaComisionTransaccional.add(detalle.getIvaTransaccionalidad());
                totalSobretasa = totalSobretasa.add(detalle.getSobreTasa());
                totalIvaSobretasa = totalIvaSobretasa.add(detalle.getIvaSobreTasa());
                totalMontoTotal = totalMontoTotal.add(detalle.getMontoTotal() );
            }
            String separador = ",";
            //escribe lista de totales
            writer.write("\n" +
                    "" + separador +
                    "Total:" + separador +
                    totalImporteNeto.toString()+ separador +
                    "" + separador +
                    "" + separador +
                    "" + separador +
                    "" + separador +
                    totalComisionTransaccional.toString()+ separador +
                    totalIvaComisionTransaccional.toString()+ separador +
                    totalSobretasa.toString()+ separador +
                    totalIvaSobretasa.toString()+ separador +
                    totalMontoTotal.toString()
            );

            writer.close();
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
            // Close the ByteArrayOutputStream when done
            byteArrayOutputStream.close();

            LOG.info("FileManagerServiceImpl. Termina crear file");
            return byteArrayOutputStream;
        } catch (Exception e) {
            LOG.error("FileManagerServiceImpl. Error al generar documento", e);
            throw e;
        } finally {
            file.delete();
        }
    }


    private String getNombreColumnas() {
        Class<ComisionesBancariasDetalleDTO> columnClass = ComisionesBancariasDetalleDTO.class;
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


    /**
     * Mapea un pago conciliado mit en ComisionesBancariasDTO
     * @param pagoConciliado
     * @return Detalle de Comision Bancaria Santander
     */
    private ComisionesBancariasDTO mapToComisionBancaria(PagoConciliadoMIT pagoConciliado){
        ComisionesBancariasDTO comisionBancaria = new ComisionesBancariasDTO();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        if(pagoConciliado != null && pagoConciliado.getCorresponsal() != null){
            comisionBancaria.setImporte(pagoConciliado.getCorresponsal().getImporte());
            comisionBancaria.setSucursal(pagoConciliado.getCorresponsal().getSucursal());
            comisionBancaria.setFecha(dateFormat.format(pagoConciliado.getCorresponsal().getFechaOperacion()));
            comisionBancaria.setEmisor(pagoConciliado.getCorresponsal().getMarcaTarjeta());
            comisionBancaria.setBanco(pagoConciliado.getCorresponsal().getBancoEmisor());
            comisionBancaria.setTipoTarjeta(TipoTarjetaEnum.valueOf(pagoConciliado.getCorresponsal().getTipoTarjeta()));
            comisionBancaria.setTipoPago(pagoConciliado.getCorresponsal().getTipoPago());
            comisionBancaria.setComisionTransaccional(pagoConciliado.getCorresponsal().getComisionTransaccional() != null ? pagoConciliado.getCorresponsal().getComisionTransaccional() : BigDecimal.ZERO) ;
            comisionBancaria.setSobreTasa(pagoConciliado.getCorresponsal().getSobretasa() != null ? pagoConciliado.getCorresponsal().getSobretasa() : BigDecimal.ZERO);
            comisionBancaria.setIvaTransaccionalidad(comisionBancaria.getComisionTransaccional().multiply(IVA).setScale(2,RoundingMode.HALF_UP));
            comisionBancaria.setIvaSobreTasa(comisionBancaria.getSobreTasa().multiply(IVA).setScale(2,RoundingMode.HALF_UP));
        }
        return comisionBancaria;
    }


    /**
     * Método que genera los totales del reporte por tipo tarjeta
     * @param libro libro xlsx
     * @param fila fila para los totales
     * @param initRowData numero de fila de inicio para formula de totales
     * @param finalRowData numero de fila fin para formula de totales
     * @param tipoTarjeta tipo de tarjeta
     * @return libro xlsx
     */
    private XSSFWorkbook buildTotalesByTarjeta(XSSFWorkbook libro, Row fila, int initRowData, int finalRowData, TipoTarjetaEnum tipoTarjeta, CellStyle cellStyle){
        createCellWithCurrenyFormat(libro, fila, 7, MessageFormat.format("SUMIF(F{0}:F{1},\"{2}\",H{0}:H{1})", initRowData, finalRowData, tipoTarjeta.getNombre()), true, cellStyle);
        createCellWithCurrenyFormat(libro, fila, 8, MessageFormat.format("SUMIF(F{0}:F{1},\"{2}\",I{0}:I{1})", initRowData, finalRowData, tipoTarjeta.getNombre()), true, cellStyle);
        createCellWithCurrenyFormat(libro, fila, 9, MessageFormat.format("SUMIF(F{0}:F{1},\"{2}\",J{0}:J{1})", initRowData, finalRowData, tipoTarjeta.getNombre()), true, cellStyle);
        createCellWithCurrenyFormat(libro, fila, 10, MessageFormat.format("SUMIF(F{0}:F{1},\"{2}\",K{0}:K{1})", initRowData, finalRowData,tipoTarjeta.getNombre()), true, cellStyle);
        createCellWithCurrenyFormat(libro, fila, 11, MessageFormat.format("SUMIF(F{0}:F{1},\"{2}\",L{0}:L{1})", initRowData, finalRowData, tipoTarjeta.getNombre()), true, cellStyle);
        return libro;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComisionesPaginadoDTO consultarComisiones(RequestComisionesDTO comisionesDTO) throws SistemaException {

        Boolean paginado = comisionesDTO.getRequierePaginado();
        ComisionesPaginadoDTO resultado = new ComisionesPaginadoDTO();

        Page<PagoConciliadoMIT> result = findComisiones(comisionesDTO);

        resultado.setNumeroPagina(result.getNumber());
        resultado.setNumeroRegistros(result.getNumberOfElements());
        resultado.setTotalRegistros(((Long)result.getTotalElements()).intValue());
        resultado.setTieneMasPaginas(result.hasNext());

        Page<PagoConciliadoMIT> resultTotal = null;
        if(result.hasContent() && Boolean.TRUE.equals(comisionesDTO.getRequierePaginado())){
            comisionesDTO.setRequierePaginado(Boolean.FALSE);
            resultTotal = findComisiones(comisionesDTO);
        }

        if(!result.hasContent()) {
            resultado.setComisionesList(new ArrayList<>());
        } else {

            List<ComisionesDTO> comisionesList = generarResultComisiones(result);

            List<ComisionesDTO> comisionesListTotal = new ArrayList<>();
            if(Boolean.TRUE.equals(paginado) && resultTotal != null) {
                comisionesListTotal= generarResultComisiones(resultTotal);
            }
            resultado.setComisionesList(comisionesList);

            // obtenemos la sumatoria de los totales
            comisionesBancariasHelper.sumarTotalesComisiones(resultado, comisionesListTotal, paginado);

        }
        LOG.info("Termina ejecucion consulta comisiones Santander");
        return resultado;
    }


    private List<ComisionesDTO> generarResultComisiones(Page<PagoConciliadoMIT> result) {
        List<ComisionesDTO> comisionesList= new ArrayList<>();
        LOG.info("Se asocia informacion de WS-OAG-MIDAS");

        result.getContent().stream().forEach( comision->{
                    ComisionesDTO mapeado=  mapToResumenComision(comision);
                    mapeado.setIvaTransaccional(mapeado.getComisionTransaccional().multiply(IVA).setScale(2,RoundingMode.HALF_UP));
                    mapeado.setIvaSobretasa(mapeado.getSobretasa().intValue() >0?mapeado.getSobretasa().multiply(IVA).setScale(2,RoundingMode.HALF_UP): BigDecimal.ZERO);
                    mapeado.setMontoTotal(Utils.calcularMontoTotal(mapeado.getImporteNeto(), mapeado.getComisionTransaccional(), mapeado.getIvaTransaccional(), mapeado.getSobretasa(), mapeado.getIvaSobretasa()));
                    comisionesList.add(mapeado);
                }
        );
        return comisionesList;
    }


    /**
     * Método para consultar las comisiones bancarias por medio de los filtros establecidos en {@link RequestComisionesDTO}
     * @param comisionesDTO Filtro para realizar la consulta
     * @return Page<PagoConciliadoMIT>
     */
    private Page<PagoConciliadoMIT> findComisiones(RequestComisionesDTO comisionesDTO){
        Pageable pageable = null;
        BoolQueryBuilder query = buildFiltros(comisionesDTO);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(query);
        if(Boolean.TRUE.equals(comisionesDTO.getRequierePaginado())) {
            pageable = PageRequest.of(Math.max(comisionesDTO.getNumeroPagina(), 0), comisionesDTO.getNumeroRegistros() < 1 ? 10 : comisionesDTO.getNumeroRegistros());
            nativeSearchQueryBuilder.withPageable(pageable);
        }
        NativeSearchQuery nQuery = nativeSearchQueryBuilder.build();
        SearchHits<PagoConciliadoMIT> result = elasticTemplate.search(nQuery, PagoConciliadoMIT.class);

        return new PageImpl<>(result.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList()), pageable != null ? pageable : Pageable.unpaged(), result.getTotalHits());
    }

    /**
     * Método para mapear un {@link PagoConciliadoMIT} a un {@link ComisionesDTO}
     * @param pagoConciliadoMIT Información del pago conciliado
     * @return comisionesDTO
     */
    private ComisionesDTO mapToResumenComision(PagoConciliadoMIT pagoConciliadoMIT) {
        ComisionesDTO comisiones = new ComisionesDTO();

        if(pagoConciliadoMIT != null && pagoConciliadoMIT.getCorresponsal() != null) {
            comisiones.setBanco(pagoConciliadoMIT.getCorresponsal().getBancoEmisor());
            comisiones.setTipoPago(pagoConciliadoMIT.getCorresponsal().getTipoPago());
            comisiones.setTipoTarjeta(pagoConciliadoMIT.getCorresponsal().getTipoTarjeta());
            comisiones.setImporteNeto(pagoConciliadoMIT.getCorresponsal().getImporte());
            comisiones.setFechaVenta(pagoConciliadoMIT.getCorresponsal().getFechaOperacion());
            comisiones.setSucursal(pagoConciliadoMIT.getCorresponsal().getSucursal());
            comisiones.setIdPago(pagoConciliadoMIT.getIdPago());
            comisiones.setComisionTransaccional(pagoConciliadoMIT.getCorresponsal().getComisionTransaccional() != null ? pagoConciliadoMIT.getCorresponsal().getComisionTransaccional() : BigDecimal.ZERO);
            comisiones.setSobretasa(pagoConciliadoMIT.getCorresponsal().getSobretasa() != null ? pagoConciliadoMIT.getCorresponsal().getSobretasa() : BigDecimal.ZERO);
        }
        return comisiones;
    }


    /**
     * Método que genera los filtros para el reporte de comisiones
     * @param comisionesDTO
     * @return query
     */
    private BoolQueryBuilder buildFiltros(RequestComisionesDTO comisionesDTO) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        // Solo se deben consultar las ventas
        builder.must( QueryBuilders.matchPhraseQuery("corresponsal.tipoOperacion", Constants.TIPO_OPERACION_VENTA));
        builder.must( QueryBuilders.matchPhraseQuery("estado", EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos()));

        if (StringUtils.hasText(comisionesDTO.getSucursal())) {
            builder.must( QueryBuilders.matchPhraseQuery("corresponsal.sucursal", comisionesDTO.getSucursal()));
        }

        if (StringUtils.hasText(comisionesDTO.getTipoPago())) {
            builder.must( QueryBuilders.matchPhraseQuery("corresponsal.tipoPago", removeAccent(comisionesDTO.getTipoPago())));
        }

        if (StringUtils.hasText(comisionesDTO.getTipoTarjeta())) {
            builder.must( QueryBuilders.matchPhraseQuery("corresponsal.tipoTarjeta", removeAccent(comisionesDTO.getTipoTarjeta())));
        }

        if (StringUtils.hasText(comisionesDTO.getBanco())) {
            builder.must( QueryBuilders.matchPhraseQuery("corresponsal.bancoEmisor", removeAccent(comisionesDTO.getBanco())));
        }

        if(comisionesDTO.getFecha() != null){
            Calendar fin = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of(timeZoneId)));
            fin.setTime( comisionesDTO.getFecha());
            fin.add(Calendar.DATE,1);
            LOG.info("Fecha Operacion Fin, {} ",fin.getTime());


            builder.filter(QueryBuilders.rangeQuery("corresponsal.fechaOperacion")
                    .gte(comisionesDTO.getFecha())
                    .lt(fin.getTime()));
        }

        return builder;
    }

    @Override
    public CatalogoComisionesDTO consultarCatalogoComisiones(Date fechaOperacion) {
        LOG.info("Fecha en valor Long, {}",fechaOperacion.getTime());

        LOG.info("Fecha Operacion a consultar filtros, {}",fechaOperacion);

        RequestComisionesDTO filtroComisiones = new RequestComisionesDTO();
        filtroComisiones.setFecha(fechaOperacion);
        Page<PagoConciliadoMIT> result = findComisiones(filtroComisiones);
        return buildCatalogoComisiones(result.getContent());
    }

    private CatalogoComisionesDTO buildCatalogoComisiones(List<PagoConciliadoMIT> pagoConciliadoMIT) {
        return  contruyeCatalogoComision(CorresponsalEnum.SANTANDER, pagoConciliadoMIT, null);
    }



}



