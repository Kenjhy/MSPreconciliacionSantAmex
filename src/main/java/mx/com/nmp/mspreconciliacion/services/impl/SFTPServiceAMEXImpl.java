package mx.com.nmp.mspreconciliacion.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpException;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.services.SFTPBase;
import mx.com.nmp.mspreconciliacion.services.SFTPService;
import mx.com.nmp.mspreconciliacion.util.FechaUtil;
import mx.com.nmp.mspreconciliacion.util.JsonSucursalAMEXUtil;

@Service
public class SFTPServiceAMEXImpl extends SFTPBase implements SFTPService {


	private static final Logger LOGGER = LoggerFactory.getLogger(SFTPServiceAMEXImpl.class);

	public static final ObjectMapper JSON = new ObjectMapper();

	private static final String  IVA_AMEX = "-16000";
	private static final BigDecimal IVA=  BigDecimal.valueOf(0.16);


	@PostConstruct
	public void inicializa(){
		JsonSucursalAMEXUtil.cargarJSONEstablecimientoAMEX();
	}

	@Override
	public List<MovCorresponsalDTO> leerArchivo(PreConciliacionDTO preConciliacionDTO, JSch sftpCanal) throws PagoException{
		List<MovCorresponsalDTO> movimientosList= new ArrayList<>();
		Map<String, List<String>> mapRenglones = null;
		String nombreArchivoServ= null;

		ChannelSftp canalSFTP = crearCanalSFTP(userAMEX,hostAMEX,pwdAMEX, portAMEX,CorresponsalEnum.AMEX, sftpCanal);
		try {
			nombreArchivoServ= obtenerArchivoEjecutado(preConciliacionDTO.getFecha(), CorresponsalEnum.AMEX);
			List<Stream<String>> lineasArchivo =obtenerArchivoSFTP(nombreArchivoServ,canalSFTP, preConciliacionDTO.getNumeroReintento(), folderAMEX, folderReintentoAMEX);
			if (lineasArchivo.isEmpty()) {
				LOGGER.info(PagoException.ERROR_AMEX_SANTANDER_FECHA.getDescripcion());
				throw PagoException.ERROR_AMEX_SANTANDER_FECHA;
			}else {
				
				for(Stream<String> l: lineasArchivo) {
					mapRenglones= obtenerDatosArchivo(l);
					
					movimientosList.addAll(procesarTransacciones(mapRenglones.get("transaccionesList"), nombreArchivoServ));
					List<MovCorresponsalDTO> movimientosPrecios =procesarPrecios(movimientosList, mapRenglones.get("preciosList"));
					for (MovCorresponsalDTO movAj: movimientosPrecios) {
						procesarAjustes(movimientosList, mapRenglones.get("ajustesList"), movAj.getEstablecimiento(), movAj.getReferencia(), movAj.getTarjeta());
					}
					movimientosList.addAll(procesarDevoluciones(mapRenglones.get("devolucionesList"),nombreArchivoServ));
				}
			}

		} catch (SftpException e) {
			LOGGER.error("Error en lectura de archivo AMEX, {0}", e);
			throw PagoException.ERROR_AMEX_SFTP;
		}
		finally {
			desconectarSFTP(canalSFTP, CorresponsalEnum.SANTANDER);
		}
		LOGGER.info("Movimientos obtenidos de lectura AMEX {}", movimientosList.size());
		return movimientosList;
	}

	private Map<String, List<String>> obtenerDatosArchivo(Stream<String> renglonesArchivo) throws PagoException {
		Map<String, List<String>> mapRenglones = new HashMap<>();
		List<String> transaccionesList = new ArrayList<>();
		List<String> preciosList = new ArrayList<>();
		List<String> ajustesList = new ArrayList<>();
		List<String> submiList = new ArrayList<>();
		List<String> devolucionesList = new ArrayList<>();

		if (renglonesArchivo != null) {
			//Hay archivos
			renglonesArchivo.forEach(line-> {
				String[] datosLinea=  line.split(",");
				if (datosLinea.length > 0) {
					String nombreRenglon = datosLinea[0].replace("\"", "");
					switch(nombreRenglon) {
						case "SUBMISSION":
							submiList.add(line);
							break;
						case "TRANSACTN":
							transaccionesList.add(line);
							break;
						case "TXNPRICING":
							preciosList.add(line);
							break;
						case "ADJUSTMENT":
							ajustesList.add(line);
							break;
						case "CHARGEBACK":
							devolucionesList.add(line);
							break;
						default:
							break;
					}
				}
			});
		}else {
			LOGGER.info(PagoException.ERROR_AMEX_SANTANDER_FECHA.getDescripcion());
			throw PagoException.ERROR_AMEX_SANTANDER_FECHA;
		}
		mapRenglones.put("submiList", submiList);
		mapRenglones.put("transaccionesList", transaccionesList);
		mapRenglones.put("preciosList", preciosList);
		mapRenglones.put("ajustesList", ajustesList);
		mapRenglones.put("devolucionesList", devolucionesList);
		LOGGER.info("InformacionDe Archivos, transacciones= {}, devoluciones= {}", transaccionesList.size(), devolucionesList.size());
		return mapRenglones;
	}


	private List<MovCorresponsalDTO> procesarTransacciones(List<String> transacciones, String nombreArchivoServ) throws PagoException{
		List<MovCorresponsalDTO> movimientosList= new ArrayList<>();
		try {
			if (transacciones != null) {

				for(String s: transacciones) {
					MovCorresponsalDTO movDTO= null;
					String[] datosLinea=  s.split(",");
					if (datosLinea.length > 0) {
						movDTO= mapearTransaccion(datosLinea);
						if (movDTO != null) {
							//Fecha devoluciíon mapeando campo (8)AMEX PROCESSING DATE
							mapearTransaccionDev(movDTO,datosLinea[8].replace("\"", ""));
							mapearTransaccionCalculados(movDTO, nombreArchivoServ);
							movimientosList.add(movDTO);
						}
					}
				}
			}
		}catch(Exception ex) {
			LOGGER.info("La estructura del archivo a procesar es incorrecta, {0}", ex);
			throw PagoException.ERROR_AMEX_SFTP_PROC;
		}
		return movimientosList;

	}

	private String aplicarFormatoTexto(String[] datosLinea, int indice) {
		String texto= null;

		if (tieneValor(datosLinea,indice)) {
			texto= datosLinea[indice].replace("\"", "");
		}
		return texto;
	}

	private MovCorresponsalDTO mapearTransaccion(String[] datosLinea) throws PagoException {
		MovCorresponsalDTO movDTO= null;
		if (datosLinea.length > 0) {
			try {
				movDTO = new MovCorresponsalDTO();
				movDTO.setTipoOperacion(Constants.TIPO_OPERACION_VENTA);
				movDTO.setReferencia(tieneValor(datosLinea,3)?obtenerCadenaSinCerosIzquierda(aplicarFormatoTexto(datosLinea,3)):null);
				movDTO.setFechaDeposito(tieneValor (datosLinea, 4)? FechaUtil.convierteaFecha(datosLinea[4],FechaUtil.FORMATO_ARCHIVO):null);
				movDTO.setMoneda(aplicarFormatoTexto(datosLinea,5));
				movDTO.setEstablecimiento(aplicarFormatoTexto(datosLinea,6));
				//se mapea valor de establecimiento a campo afiliacion
				movDTO.setAfiliacion(movDTO.getEstablecimiento());
				movDTO.setTarjeta(aplicarFormatoTexto(datosLinea,14));
				movDTO.setImporteBruto(obtieneImporte(datosLinea, 17));
				movDTO.setFechaOperacion(tieneValor (datosLinea, 18)? FechaUtil.convierteaFecha(datosLinea[18].replace("\"", ""), FechaUtil.FORMATO_ARCHIVO):null);
				movDTO.setHoraOperacion(tieneValor (datosLinea, 19)?obtieneHora(datosLinea, 19): null);
				String numeroOp= obtenerCadenaSinCerosIzquierda(aplicarFormatoTexto(datosLinea,20));
				movDTO.setNumeroOperacion(numeroOp.replaceAll("[^a-zA-Z0-9]", ""));
				movDTO.setBancoEmisor(Constants.CORRESPONSAL_AMEX); //No lo regresa el archivo, solo sincronizar con busqueda en comisiones
				movDTO.setAutorizacion(tieneValor(datosLinea, 21)? datosLinea[21].replace("\"", ""): "");
				movDTO.setNumeroMesesPromocion(tieneValor(datosLinea,35)? Integer.valueOf(datosLinea[35].replace("\"", "")):0);
				movDTO.setComisionPromoServicio(conviertePositivo(obtieneImporte(datosLinea, 38),2));
				movDTO.setComisionPromoAceleracion(conviertePositivo(obtieneImporte(datosLinea, 39),2));

			}catch(Exception ex) {
				throw PagoException.ERROR_AMEX_SFTP_RESPONSE_MAPPER;
			}
		}
		return movDTO;
	}

	private void mapearTransaccionCalculados(MovCorresponsalDTO movDTO, String nombreArchivoServ) {
		if (movDTO !=null) {
			movDTO.setSucursal(JsonSucursalAMEXUtil.consultarSucursal(movDTO.getEstablecimiento()));
			movDTO.setTipoTarjeta(Constants.TIPO_TARJETA_CREDITO);
			movDTO.setTipoPago(obtieneTipoPago(movDTO.getNumeroMesesPromocion()));
			movDTO.setNombreArchivo(nombreArchivoServ);

			if (movDTO.getTipoOperacion().equals(Constants.TIPO_OPERACION_VENTA)) {
				movDTO.setSobretasa(calcularSobretasaIndividual(movDTO));
				movDTO.setIvaSobretasa(calcularIVASobreTasa(movDTO.getSobretasa()));
				movDTO.setSumaSobretasaIva(sumaSobreTasa(movDTO.getSobretasa(), movDTO.getIvaSobretasa()));
			}else {
				movDTO.setSobretasa(BigDecimal.ZERO);
				movDTO.setIvaSobretasa(BigDecimal.ZERO);
				movDTO.setSumaSobretasaIva(BigDecimal.ZERO);
			}
			movDTO.setFechaDepositoCadena(movDTO.getFechaDeposito()!= null? FechaUtil.convierteFechaaCadena(movDTO.getFechaDeposito(), FechaUtil.FORMATO_FECHA): null);
			movDTO.setFechaOperacionCadena(movDTO.getFechaOperacion()!= null? FechaUtil.convierteFechaaCadena(movDTO.getFechaOperacion(),FechaUtil.FORMATO_FECHA):null);
		}
	}

	private void mapearTransaccionDev(MovCorresponsalDTO movDTO, String fechaDevolucion){

		if (movDTO!= null && movDTO.getImporteBruto().signum() == -1 ) {
			//valor negativo
			movDTO.setImporteBruto(conviertePositivo(movDTO.getImporteBruto(), 2));
			//De acuerdo a negocio es la fecha procesamiento-AMERICAN EXPRESS PROCESSING DATE
			movDTO.setFechaDevolucion(fechaDevolucion!= null? FechaUtil.convierteaFecha(fechaDevolucion, FechaUtil.FORMATO_ARCHIVO):null);
			movDTO.setTipoOperacion(Constants.TIPO_OPERACION_DEVOLUCION);
			movDTO.setTipoDevolucion(Constants.TIPO_DEVOLUCION_AUTOMATICA);
			movDTO.setEstatusDevolucion(Constants.ESTATUS_DEVOLUCION_SOLICITADA);
			movDTO.setEstatusDevolucionId(Constants.ESTATUS_DEVOLUCION_SOLICITADA_ID);
			movDTO.setFechaLiquidacion(movDTO.getFechaDeposito());//Fecha deposito reportada por AMEX- en campo(4) PAYMENT DATE
			movDTO.setImporteDevolucion(conviertePositivo(movDTO.getImporteBruto(), 2));
			
			movDTO.setComisionTransaccional(BigDecimal.ZERO);
			movDTO.setComisionTransaccionalEPA(BigDecimal.ZERO);
			movDTO.setIvaTransaccional(BigDecimal.ZERO);
			movDTO.setImporteNeto(movDTO.getImporteBruto());
		}
	}

	private BigDecimal calcularSobretasaIndividual(MovCorresponsalDTO mov) {
		BigDecimal sobreTasa = BigDecimal.ZERO;
		if (mov != null && mov.getNumeroMesesPromocion() > 0) {
			//Tiene sobretasa
			BigDecimal suma = mov.getComisionPromoAceleracion().add(mov.getComisionPromoServicio());
			sobreTasa = suma.setScale(2, RoundingMode.HALF_UP);

		}
		return sobreTasa;
	}

	private BigDecimal calcularIVASobreTasa(BigDecimal sobreTasa) {
		BigDecimal iva= BigDecimal.ZERO;

		if (sobreTasa.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal ivaM = sobreTasa.multiply(IVA);
			iva = BigDecimal.valueOf(Math.abs(ivaM.doubleValue())).setScale(2, RoundingMode.HALF_UP);
		}
		return iva;
	}

	private BigDecimal sumaSobreTasa(BigDecimal sobreTasa, BigDecimal ivaSobreTasa) {
		BigDecimal suma2Dec = BigDecimal.ZERO;

		if (sobreTasa.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal suma = sobreTasa.add(ivaSobreTasa);
			suma2Dec = suma.setScale(2, RoundingMode.HALF_UP);
		}
		return suma2Dec;
	}

	private String obtieneHora(String[] cadena, int indice) {
		String hora= null;
		if (tieneValor (cadena, indice)) {
			hora= formatHora(cadena[indice].replace("\"", ""));
		}
		return hora;
	}

	private String obtieneTipoPago(Integer numeroMeses) {
		String tipoPago= null;

		if (numeroMeses > 0) {
			//Asigar tipoPago --Meses
			tipoPago= numeroMeses.toString() + " MSI";
		}else {
			tipoPago= Constants.TIPO_PAGO_CONTADO;
		}
		return tipoPago;
	}

	private BigDecimal obtieneImporte(String[] cadena, int indice) {
		BigDecimal importe= BigDecimal.ZERO;

		if (tieneValor(cadena,indice)) {
			String valor= cadena[indice].replace("\"", "").replace(" ", "");
			if (valor != null && valor.length() > 1) {
				importe = formatImportes(valor.trim(), 2);
			}
		}
		return importe;
	}

	private List<MovCorresponsalDTO> procesarPrecios(List<MovCorresponsalDTO> movimientos, List<String> precios ) throws PagoException{
		try {
			for(String t: precios) {
				String[] datosLinea=  t.split(",");
				if (datosLinea.length > 0) {
					for(MovCorresponsalDTO movFilter: movimientos) {
						if (movFilter.getReferencia() != null) {
							mapearPrecios(movFilter, datosLinea);
						}else {
							mapearPreciosSinReferencia(movFilter, datosLinea);
						}
					}
				}
			}
		}catch(Exception ex) {
			LOGGER.info("La estructura del archivo a procesar es incorrecta {0}", ex);
			throw PagoException.ERROR_AMEX_SFTP_PROC;
		}
		return movimientos;
	}
	private MovCorresponsalDTO mapearPreciosDevs(MovCorresponsalDTO movFilter, String[] datosLinea, boolean sinReferencia) {
		MovCorresponsalDTO dev= null;
		boolean esDev= false;
		
		dev = movFilter;
		if (!sinReferencia) {
			if (movFilter.getEstablecimiento().equals(aplicarFormatoTexto(datosLinea, 1))
					&& movFilter.getTarjeta().equals( aplicarFormatoTexto(datosLinea, 11))
					&& movFilter.getReferencia().equals( aplicarFormatoTexto(datosLinea, 3))
					&& ! movFilter.getTipoOperacion().equals(Constants.TIPO_OPERACION_VENTA)){
				esDev= true;
			}
		}else if (movFilter.getEstablecimiento().equals(aplicarFormatoTexto(datosLinea, 1))
				&& movFilter.getTarjeta().equals( aplicarFormatoTexto(datosLinea, 11))
				&& ! movFilter.getTipoOperacion().equals(Constants.TIPO_OPERACION_VENTA)) {
			esDev= true;
		}
		if (esDev) {
			movFilter.setComisionTransaccional(BigDecimal.ZERO);
			movFilter.setComisionTransaccionalEPA(BigDecimal.ZERO);
			movFilter.setIvaTransaccional(BigDecimal.ZERO);
			movFilter.setImporteNeto(calcularMontoNeto(movFilter));
		}
		return dev;
	}

	
	private MovCorresponsalDTO mapearPrecios(MovCorresponsalDTO movFilter, String[] datosLinea) {

		if (movFilter.getEstablecimiento().equals(aplicarFormatoTexto(datosLinea, 1))
				&& movFilter.getTarjeta().equals( aplicarFormatoTexto(datosLinea, 11))
				&& movFilter.getReferencia().equals( aplicarFormatoTexto(datosLinea, 3))
				&& movFilter.getTipoOperacion().equals(Constants.TIPO_OPERACION_VENTA)) {

			if (movFilter.getComisionTransaccional() == null || movFilter.getComisionTransaccional() == BigDecimal.ZERO) {
				movFilter.setComisionTransaccional(obtenerComsionTransaccional(datosLinea, movFilter));
			}
			if (movFilter.getComisionTransaccionalEPA()==null || movFilter.getComisionTransaccionalEPA()== BigDecimal.ZERO) {
				movFilter.setComisionTransaccionalEPA(obtenerComisionTransaccionalEPA(datosLinea));
			}
			if (movFilter.getIvaTransaccional()== null || movFilter.getIvaTransaccional()== BigDecimal.ZERO) {
				movFilter.setIvaTransaccional(obtenerIVaTransaccional(datosLinea, movFilter));
			}
			if (movFilter.getImporteNeto()== null || movFilter.getImporteNeto()== BigDecimal.ZERO) {
				movFilter.setImporteNeto(calcularMontoNeto(movFilter));
			}
		}else {
			mapearPreciosDevs(movFilter, datosLinea, false);
		}
		
		return movFilter;
	}
	
	private MovCorresponsalDTO mapearPreciosSinReferencia(MovCorresponsalDTO movFilter, String[] datosLinea) {

		if (movFilter.getEstablecimiento().equals(aplicarFormatoTexto(datosLinea, 1))
				&& movFilter.getTarjeta().equals( aplicarFormatoTexto(datosLinea, 11))
				&& movFilter.getTipoOperacion().equals(Constants.TIPO_OPERACION_VENTA)) {

			if (movFilter.getComisionTransaccional() == null || movFilter.getComisionTransaccional() == BigDecimal.ZERO) {
				movFilter.setComisionTransaccional(obtenerComsionTransaccional(datosLinea, movFilter));
			}
			if (movFilter.getComisionTransaccionalEPA()==null || movFilter.getComisionTransaccionalEPA()== BigDecimal.ZERO) {
				movFilter.setComisionTransaccionalEPA(obtenerComisionTransaccionalEPA(datosLinea));
			}
			if (movFilter.getIvaTransaccional()== null || movFilter.getIvaTransaccional()== BigDecimal.ZERO) {
				movFilter.setIvaTransaccional(obtenerIVaTransaccional(datosLinea, movFilter));
			}
			if (movFilter.getImporteNeto()== null || movFilter.getImporteNeto()== BigDecimal.ZERO) {
				movFilter.setImporteNeto(calcularMontoNeto(movFilter));
			}
		}else {
			mapearPreciosDevs(movFilter, datosLinea, true);
		}
		
		return movFilter;
	}


	private BigDecimal obtenerIVaTransaccional(String[] datosLinea, MovCorresponsalDTO movFilter) {
		BigDecimal montoRedondeado= BigDecimal.ZERO;
		String comision= "";
		if (datosLinea != null && movFilter.getIvaTransaccional() == null || movFilter.getIvaTransaccional().compareTo(BigDecimal.ZERO)== 0) {
			if (revisarLinea(datosLinea, 14, "1A")) {
				if (tieneValor(datosLinea, 17)) {
					comision= datosLinea[17].replace("\"", "").trim();
				}
				if (comision.equals("-016000") || comision.equals(IVA_AMEX)) { //Iva
					BigDecimal monto = conviertePositivo( formatImportes(datosLinea[18].replace("\"", ""), 6), 6);
					montoRedondeado= monto.setScale(2, RoundingMode.HALF_UP);
				}
			}
		}else {
			if (movFilter.getIvaTransaccional() != null) {
				montoRedondeado = movFilter.getIvaTransaccional();
			}
		}
		return  montoRedondeado;
	}

	private BigDecimal obtenerComsionTransaccional(String[] datosLinea, MovCorresponsalDTO movFilter) {
		BigDecimal montoRedondeado= BigDecimal.ZERO;
		String comision = null;
		if (datosLinea != null && movFilter.getComisionTransaccional() == null || movFilter.getComisionTransaccional().compareTo(BigDecimal.ZERO)== 0) {

			if (revisarLinea(datosLinea, 14, "1A")) {
				if (tieneValor(datosLinea, 17)) {
					comision= datosLinea[17].replace("\"", "").trim();
				}
				if (comision != null && Integer.valueOf(comision).equals(2000)) { //Comision
					BigDecimal monto = conviertePositivo(formatImportes(datosLinea[18].replace("\"", ""), 6), 6);
					montoRedondeado= monto.setScale(2, RoundingMode.HALF_UP);
				}
			}
		}else {
			if (movFilter.getComisionTransaccional() != null) {
				montoRedondeado = movFilter.getComisionTransaccional();
			}
		}

		return montoRedondeado;
	}

	private boolean revisarLinea(String[] datosLinea, int indice, String datoBuscar) {
		boolean contiene= false;
		String valor = null;
		if (datosLinea != null && datosLinea.length > 17) {
			valor =tieneValor(datosLinea, indice)?datosLinea[indice].replace("\"", "").trim(): null;
			if (valor != null && valor.equals(datoBuscar)){
				contiene= true;
			}
		}
		return  contiene;
	}

	private BigDecimal obtenerComisionTransaccionalEPA(String[] datosLinea) {
		BigDecimal comision= BigDecimal.ZERO;
		String val= tieneValor(datosLinea, 17)?datosLinea[17].replace("\"", "").trim(): null;
		if (val != null && !val.isEmpty()  && !val.contains(IVA_AMEX)) {
			comision= conviertePositivo(formatImportes(val, 3),3);
		}
		return comision;
	}

	private BigDecimal calcularMontoNeto(MovCorresponsalDTO movFilter) {
		BigDecimal monto= BigDecimal.ZERO;
		if (movFilter != null && movFilter.getComisionTransaccional().compareTo(BigDecimal.ZERO) > 0 && movFilter.getIvaTransaccional().compareTo(BigDecimal.ZERO) > 0) {
			double montoNeto = movFilter.getImporteBruto().doubleValue()- movFilter.getComisionTransaccional().doubleValue() - movFilter.getIvaTransaccional().doubleValue() - movFilter.getSobretasa().doubleValue() - movFilter.getIvaSobretasa().doubleValue();
			monto = BigDecimal.valueOf(montoNeto).setScale(2, RoundingMode.HALF_UP);
		}
		return monto;
	}


	private double sumarAceleracion(List<MovCorresponsalDTO> movimientos, String estab, String referencia, String tarjeta) {
		double sumAceleracionTrans= 0.0;

		if (!movimientos.isEmpty() && estab != null && referencia != null) {
			sumAceleracionTrans= movimientos.stream().filter(mov -> mov.getEstablecimiento().equals(estab) && mov.getReferencia().equals(referencia))
					.mapToDouble(mov -> mov.getComisionPromoAceleracion().doubleValue())
					.sum();
		}else if (!movimientos.isEmpty() && estab != null && referencia == null && tarjeta != null) {
			sumAceleracionTrans= movimientos.stream().filter(mov -> mov.getEstablecimiento().equals(estab) && mov.getTarjeta().equals(tarjeta))
					.mapToDouble(mov -> mov.getComisionPromoAceleracion().doubleValue())
					.sum();
		}
		return sumAceleracionTrans;
	}

	private double sumarServicio(List<MovCorresponsalDTO> movimientos, String estab, String referencia, String tarjeta) {
		double sumServicioTrans= 0.0;

		if (!movimientos.isEmpty() && estab != null && referencia != null) {
			sumServicioTrans= movimientos.stream().filter(mov -> mov.getEstablecimiento().equals(estab) && mov.getReferencia().equals(referencia))
					.mapToDouble(mov -> mov.getComisionPromoServicio().doubleValue())
					.sum();
		}else if (!movimientos.isEmpty() && estab != null && referencia == null && tarjeta != null) {
			sumServicioTrans= movimientos.stream().filter(mov -> mov.getEstablecimiento().equals(estab) && mov.getTarjeta().equals(tarjeta))
					.mapToDouble(mov -> mov.getComisionPromoServicio().doubleValue())
					.sum();
		}
		return sumServicioTrans;
	}

	private double sumarIVASobretasa(List<MovCorresponsalDTO> movimientos, String estab, String referencia, String tarjeta) {
		double sumIVASobretasa= 0.0;

		if (!movimientos.isEmpty() && estab != null && referencia != null) {
			sumIVASobretasa= movimientos.stream().filter(mov -> mov.getEstablecimiento().equals(estab) && mov.getReferencia().equals(referencia))
					.mapToDouble(mov -> mov.getIvaSobretasa().doubleValue())
					.sum();
		}else if (!movimientos.isEmpty() && estab != null && referencia == null && tarjeta != null) {
			sumIVASobretasa= movimientos.stream().filter(mov -> mov.getEstablecimiento().equals(estab) && mov.getTarjeta().equals(tarjeta))
					.mapToDouble(mov -> mov.getIvaSobretasa().doubleValue())
					.sum();
		}
		return sumIVASobretasa;
	}
	
	private boolean revisarAjustePorReferencia(String[] datosLinea, String estab,  String referencia) {
		boolean puedeSumar= false;
		boolean referYestabEncontrado= false;
		boolean estabEncontrado= false;

		String establecimiento= aplicarFormatoTexto(datosLinea, 1);
		String referenciaAdj= aplicarFormatoTexto(datosLinea,3);					
		
		if (referencia != null && referenciaAdj!= null &&  referenciaAdj.equals(referencia)){
			if (establecimiento != null && establecimiento.equals(estab)) {
				referYestabEncontrado= true;
			}
		}else if (referencia == null && establecimiento != null && establecimiento.equals(estab)) {
			estabEncontrado= true;
		}
				
		if (referYestabEncontrado || estabEncontrado) {
			puedeSumar= true;
		}
		
		return puedeSumar;
	}

	private double sumarComision(List<String> ajustes, String estab, boolean esComision, String referencia) {
		double sumAj= 0.0;
		
		for (String t: ajustes) {
			String[] datosLinea=  t.split(",");
			if (datosLinea.length > 0 && revisarAjustePorReferencia(datosLinea, estab, referencia)) {
				if (esComision && tieneValor(datosLinea, 21)) { //Comision
					sumAj+= formatImportes(datosLinea[21].replace("\"", ""),2).doubleValue();
				}else if (!esComision && tieneValor(datosLinea, 22)) { //IVA
					sumAj+= formatImportes(datosLinea[22].replace("\"", ""),2).doubleValue();
				}
			}
		}
		return sumAj;
	}

	private List<MovCorresponsalDTO> procesarAjustes(List<MovCorresponsalDTO> movimientos, List<String> ajustes, String estab, String referencia, String tarjeta) throws PagoException{
		double sumComisionAj;
		double sumIvaAj;

		try {
			double sumAceleracionTrans= sumarAceleracion(movimientos,estab, referencia, tarjeta);
			double sumServicioTrans= sumarServicio(movimientos,estab, referencia, tarjeta);
			double sumComisionesTrans = sumAceleracionTrans+ sumServicioTrans;
			double sumIvaSobretasa = sumarIVASobretasa(movimientos, estab, referencia, tarjeta);

			if (sumComisionesTrans > 0.0) {
				//Comprobar comisión e iva de renglón-ajustes

				sumComisionAj= sumarComision(ajustes, estab, true, referencia);
				sumIvaAj= sumarComision(ajustes, estab, false, referencia);
				BigDecimal suma=  BigDecimal.valueOf(sumComisionAj);
				BigDecimal suma2Dec= suma.setScale(2, RoundingMode.CEILING);
				if (suma2Dec.doubleValue() == sumComisionesTrans && sumIvaSobretasa == sumIvaAj) {
					LOGGER.info("Lectura GRCCN-Procesamiento de ajustes correctos por sucursal-Establecimiento={}, sumComision={}, sumIva={}", estab, sumComisionesTrans, sumIvaSobretasa);
				}
			}

		}catch(Exception ex) {
			LOGGER.info("La estructura del archivo a procesar es incorrecta {0}", ex);
			throw PagoException.ERROR_AMEX_SFTP_PROC;
		}

		return movimientos;
	}
	
	

	private List<MovCorresponsalDTO> procesarDevoluciones(List<String> devolucionesList, String nombreArchivoServ){
		//Llega en archivo, renglón CHARGEBACK NACIONALMONTEMEXA64608.GRRCN.220819021156.P63MHJ21155S32
		MovCorresponsalDTO dev = null;
		List<MovCorresponsalDTO> devs= new ArrayList<>();

		if (devolucionesList != null) {
			for(String s: devolucionesList) {
				String[] datosLinea=  s.split(",");
				if (datosLinea.length > 0) {

					dev = mapearDevoluciones(datosLinea);
					if (dev != null && dev.isEsDevolucion()) {
						//Valores fijos default
						dev.setBancoEmisor(Constants.CORRESPONSAL_AMEX); //No lo regresa el archivo, solo sincronizar con busqueda en comisiones
						dev.setTipoOperacion(Constants.TIPO_OPERACION_DEVOLUCION);
						dev.setTipoDevolucion(Constants.TIPO_DEVOLUCION_AUTOMATICA);
						dev.setEstatusDevolucion(Constants.ESTATUS_DEVOLUCION_SOLICITADA);
						dev.setEstatusDevolucionId(Constants.ESTATUS_DEVOLUCION_SOLICITADA_ID);
						dev.setTipoTarjeta(Constants.TIPO_TARJETA_CREDITO);
						dev.setSucursal(JsonSucursalAMEXUtil.consultarSucursal(dev.getEstablecimiento()));
						dev.setNombreArchivo(nombreArchivoServ);
						dev.setEsSobreCargo(true);
						devs.add(dev);
					}
				}
			}
		}
		return devs;
	}


	private MovCorresponsalDTO mapearDevoluciones(String[] datosLinea) {
		//Crear movimiento
		MovCorresponsalDTO dev= new MovCorresponsalDTO();
		//Solo procesar las disputas-que se vuelven contracargos con importe negativo y diferente a 0, sin fecha de liquidación ya que Nancy lo añadira al Layout manualmente
		BigDecimal importeChargeBack=  obtieneImporte(datosLinea, 19);
		if (importeChargeBack.signum() == -1 ) {

			dev.setEstablecimiento(aplicarFormatoTexto(datosLinea, 1));
			dev.setAfiliacion(dev.getEstablecimiento());

			//De acuerdo a negocio es la fecha procesamiento-AMERICAN EXPRESS PROCESSING DATE
			dev.setFechaOperacion(FechaUtil.convierteaFecha(tieneValor (datosLinea, 13)?datosLinea[13].replace("\"", ""):"", FechaUtil.FORMATO_ARCHIVO));
			dev.setFechaDevolucion(FechaUtil.convierteaFecha(tieneValor (datosLinea, 13)?datosLinea[13].replace("\"", ""):"", FechaUtil.FORMATO_ARCHIVO));

			dev.setReferencia(aplicarFormatoTexto(datosLinea,3));
			dev.setMoneda(aplicarFormatoTexto(datosLinea,5));
			dev.setTarjeta(aplicarFormatoTexto(datosLinea,11));
			//Número de cargo-CHARGEBACK NUMBER
			dev.setNumeroOperacion(obtenerCadenaSinCerosIzquierda(aplicarFormatoTexto(datosLinea,16)));

			//Devolución Automática Solicitada
			dev.setEsDevolucion(true);
			dev.setImporteBruto(conviertePositivo(importeChargeBack, 2));
			dev.setImporteDevolucion(dev.getImporteBruto());
			dev.setImporteNeto(conviertePositivo(obtieneImporte(datosLinea, 23), 2));

			//Se establece la fecha de liquidación como -Fecha Depósito (4-PAYMENT DATE)
			dev.setFechaLiquidacion(FechaUtil.convierteaFecha(tieneValor (datosLinea, 4)?datosLinea[4].replace("\"", ""):"", FechaUtil.FORMATO_ARCHIVO));
			dev.setFechaDeposito(dev.getFechaLiquidacion());
		}
		return dev;
	}

	private BigDecimal formatImportes(String valor, int scala) {

		StringBuilder valorDecimal = new StringBuilder();
		Double valorDouble=0.0;

		if (valor != null) {
			int longitud= valor.length();

			String parteDecimal = valor.substring(longitud-scala, longitud);
			String parteEntera = valor.substring(0, longitud-scala);
			valorDecimal.append(parteEntera).append(".").append(parteDecimal);

			valorDouble= Double.valueOf(valorDecimal.toString());
		}
		return  BigDecimal.valueOf(valorDouble).setScale(scala);
	}

	private String formatHora(String valor) {
		StringBuilder hora = new StringBuilder();
		valor = valor.trim();
		hora.append(valor.substring(0, 2))
				.append(":")
				.append(valor.substring(2, 4))
				.append(":")
				.append(valor.substring(4, 6));
		return hora.toString();
	}

	private boolean tieneValor(String[] valor, int indice) {
		boolean tieneValor = false;

		if (valor != null && indice >= 0) {
			String valorCelda = valor[indice].replace("\"", "");

			if (valorCelda.trim() != null && valorCelda.trim().length() > 0) {
				tieneValor = true;
			}
		}
		return tieneValor;
	}

	private BigDecimal conviertePositivo(BigDecimal negativo, int scala) {
		BigDecimal pos= BigDecimal.ZERO;
		if (negativo != null) {
			Double valorDouble= Double.valueOf(negativo.toString());
			pos= BigDecimal.valueOf(Math.abs(valorDouble)).setScale(scala);
		}
		return pos;
	}

}
