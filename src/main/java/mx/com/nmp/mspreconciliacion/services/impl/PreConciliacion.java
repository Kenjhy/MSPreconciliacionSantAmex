package mx.com.nmp.mspreconciliacion.services.impl;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.CorePagoMitDTO;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RestPreconcilacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.model.enums.EstadoEjecucionEnum;
import mx.com.nmp.mspreconciliacion.model.enums.EstadoPagosEnum;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.CoreConciliadoMIT;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.Corresponsal;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.CorresponsalConciliadoEPA;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.CorresponsalConciliadoMIT;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.CorresponsalMIT;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.Devolucion;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.Pago;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoEPA;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoMIT;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoMIT;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.Partida;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoEPARepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoMITRepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoMITRepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoRepository;
import mx.com.nmp.mspreconciliacion.services.PagoPrendarioRestService;
import mx.com.nmp.mspreconciliacion.util.ArchivoSFTPUtil;
import mx.com.nmp.mspreconciliacion.util.FechaUtil;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @name PreConciliacion
 * @description Clase con metodos usados para la actualiación de los indices de
 *          mo_pagos y mo_pagos_mit dentro del proceso de la preconciliacion
 *
 * @author Quarksoft
 * @creationDate 26/07/2022
 * @version 0.1
 */
@Service
public class PreConciliacion {

	@Autowired
	private ElasticsearchOperations elasticTemplate;

	protected List<Integer> estadosPagosMIT;

	protected List<Integer> estadosDevsMIT;

	@Autowired
	protected IPagoRepository pagoRepository;

	@Autowired
	protected IPagoMITRepository pagoMITRepository;

	@Autowired
	protected IPagoConciliadoMITRepository pagoConciliadoMITRepository;

	@Autowired
	protected IPagoConciliadoEPARepository pagoConciliadoEPARepository;

	@Autowired
	private PagoPrendarioRestService pagoPrendarioRestService;

	@Value(value = "${mspreconciliacion.variables.habilitarPrePagoNoEncontradoA15}")
	public Boolean habilitarPrePagoNoEncontradoA15;
	
	public PreConciliacion() {
		estadosPagosMIT = new ArrayList<>();
		estadosPagosMIT.add(EstadoPagosEnum.PAGO_APLICADO.getEstadoPagos());

		estadosDevsMIT = new ArrayList<>();
		estadosDevsMIT.add(EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos());
		estadosDevsMIT.add(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos());
		estadosDevsMIT.add(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
		estadosDevsMIT.add(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
		estadosDevsMIT.add(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos());
		estadosDevsMIT.add(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos());
	}

	private static final Logger LOG = LoggerFactory.getLogger(PreConciliacion.class);

	private CorePagoMitDTO buscarPagoMITAplicadooNoReconocido(MovCorresponsalDTO movimiento, CorresponsalEnum corresponsalEnum) throws PagoException {
		CorePagoMitDTO core = null;
		Pago moPago= null;
		PagoMIT pagosMIT= null;
		
		//Revisar si existe por preconciliación previa en estatus 15 o 16 como cargo no reconocido por falta de info en MIDAS
		if (corresponsalEnum.equals(CorresponsalEnum.AMEX)) {
			//Se busca por duplicidad en Santander y para poder crear en AMEX
			PagoMIT pagoMITDoble= buscarPagoAplicadoAMEX(movimiento);
			if (pagoMITDoble != null) {
				core = new CorePagoMitDTO();
				core.setActivo(Boolean.TRUE);
				core.setPagoMIT(pagoMITDoble);
			}else {
				// Agregamos un registro  en estado sin cargo reconocido
				core = new CorePagoMitDTO();
				core.setActivo(Boolean.FALSE);
				moPago = getPago(movimiento, corresponsalEnum);
				Pago moPagoG =pagoRepository.save(moPago);
				pagosMIT = getPagoMIT(movimiento,moPagoG.getId(),corresponsalEnum);
				pagoMITRepository.save(pagosMIT);
				core.setPagoMIT(pagosMIT);
			}
			
		}else {
			PagoMIT pagoMITConciliado= buscarPagoMIT(movimiento, true);
			if (pagoMITConciliado == null) {
				core = new CorePagoMitDTO();
				// Agregamos un registro  en estado a enviado a conciliar sin cargo reconocido
				core.setActivo(Boolean.FALSE);
				moPago = getPago(movimiento, corresponsalEnum);
				Pago moPagoG =pagoRepository.save(moPago);
				pagosMIT = getPagoMIT(movimiento,moPagoG.getId(),corresponsalEnum);
				pagoMITRepository.save(pagosMIT);
				core.setPagoMIT(pagosMIT);
			}else {
				//Revisar si existe en concliados y epa, por posible reintento
				if (!existePagoConcliado(pagoMITConciliado, corresponsalEnum)) {
					core = new CorePagoMitDTO();
					core.setActivo(Boolean.TRUE);
					core.setPagoMIT(pagoMITConciliado);
				}
			}
		}
		
		return core;
	}

	/**
	 * {@inheritDoc}
	 * @throws PagoException
	 */
	protected CorePagoMitDTO updateIndicesPreconciliacion(MovCorresponsalDTO movimiento, CorresponsalEnum corresponsalEnum, Date fecha) throws PagoException {
		// se buscan los registro en el indice de pagos_mit esto con la finalidad de ver si existe una contraparte del mismo
		CorePagoMitDTO core = null;
		Pago moPago = null;
		PagoMIT pagosMIT= buscarPagoMIT(movimiento, false);
		if(pagosMIT != null){
			// Actualizamos el estado a enviado a conciliar en:	mo_pago, mo_pago_mit
			moPago = pagoRepository.findById(pagosMIT.getIdPago()).orElse(null);
			if (moPago != null) {
				core = actualizarPago(moPago, pagosMIT, corresponsalEnum, movimiento);
	
	
				//Sincronizar datos de proveedor a Devolución en MIDAS
				if (EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos().equals(pagosMIT.getEstado()) ||
						EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos().equals(pagosMIT.getEstado())
						|| EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos().equals(pagosMIT.getEstado())) {
	
					pagosMIT.getDevolucion().setEstadoDevolucion(Constants.ESTATUS_DEVOLUCION_SOLICITADA_ID);
					pagosMIT.getDevolucion().setFechaCargoBancario(movimiento.getFechaLiquidacion());
					pagosMIT.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos());
					pagoMITRepository.save(pagosMIT);
	
					if (moPago != null) {
						moPago.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos().intValue());
						pagoRepository.save(moPago);
					}
				}
				
				else if (EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos().equals(pagosMIT.getEstado()) ||
						EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos().equals(pagosMIT.getEstado())
						|| EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos().equals(pagosMIT.getEstado())) {
	
					pagosMIT.getDevolucion().setEstadoDevolucion(Constants.ESTATUS_DEVOLUCION_SOLICITADA_ID);
					pagosMIT.getDevolucion().setFechaCargoBancario(movimiento.getFechaLiquidacion());
					pagosMIT.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos());
					pagoMITRepository.save(pagosMIT);
	
					if (moPago != null) {
						moPago.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos().intValue());
						pagoRepository.save(moPago);
					}
				}
				
				if (core != null) {
					core.setPago(moPago);
				}
			}else {
				LOG.info("El pago con numero de operacion= {}, no tiene asociado pago en mo_pagos, no es posible conciliarlo", pagosMIT.getCorresponsal().getRspOperationNumber());
			}

		}else{
			//Revisar si existe por preconciliación previa en estatus 15 o 16 como cargo no reconocido por falta de info en MIDAS
			core = buscarPagoMITAplicadooNoReconocido(movimiento,corresponsalEnum);
		}
		return core;
	}
	
	private PagoMIT buscarPagoAplicadoAMEX(MovCorresponsalDTO movimiento) throws PagoException {
		
		PagoMIT pagoEncontrado=null;
		//No se encuentra pago en estatus 3, se deberá buscar en estatus 15- ya que posiblmente se aplico para Santander
		PagoMIT pagoMITConciliado= buscarPagoMIT(movimiento, true);
		
		if (pagoMITConciliado!= null &&  pagoMITConciliado.getEstado().equals(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos())) {
			//Buscar si existe en corresponsal Santander
			 List<PagoConciliadoMIT> pagosAMEX= pagoConciliadoMITRepository.findByCorresponsalEsAMEXAndCorresponsalNumeroOperacionAndEstado(true, movimiento.getNumeroOperacion(), EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos());
			 for (PagoConciliadoMIT pA: pagosAMEX) {
				 
				BigDecimal montoPagoMit = pA.getCorresponsal().getImporte().setScale(2);
				BigDecimal montoArchivo = movimiento.getImporteBruto().setScale(2);
				
				//Se compara autorización en String ya que llega en ocasiones  alfanumerica, riesgo con 0 a la izquierda no sera igual
					if (movimiento.getAutorizacion().equals(pA.getCorresponsal().getAutorizacion())) {
						
						int compara= montoPagoMit.compareTo(montoArchivo);
						if (compara== 0) {
							pagoEncontrado= pagoMITConciliado;
							break;
						}
					}
			 }
		}
		return pagoEncontrado;
	}

	private CorePagoMitDTO actualizarPago(Pago moPago, PagoMIT pagosMIT, CorresponsalEnum corresponsalEnum, MovCorresponsalDTO movimiento) {
		CorePagoMitDTO core = null;
		Integer fondoAGarantia= 205;
		
		if (moPago != null && pagosMIT != null) {
			// Actualizamos el estado a enviado a conciliar en:	mo_pago, mo_pago_mit
			if (moPago.getEstado() == EstadoPagosEnum.PAGO_APLICADO.getEstadoPagos().intValue()) {
				moPago.setEstado(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos());
				core = new CorePagoMitDTO();
				core.setActivo(Boolean.TRUE);
				
				//Fondo a garantia
				if (!pagosMIT.isNoEncontradoConvertA15()) {
					if (!moPago.getPartidas().isEmpty()  && moPago.getPartidas().get(0).getIdOperacion().equals(fondoAGarantia)){
						LOG.info("Se asigna sucursal de proveedor a operacion-205- numeroOperacion ={} ", movimiento.getNumeroOperacion());
						moPago.getPartidas().get(0).setNumeroSucursalPartida(Integer.valueOf(movimiento.getSucursal()));
						moPago.getPartidas().get(0).setSucursalOperacion(movimiento.getSucursal());
						
						//Asignar tipo operacion y abreviatura
						//AFG - Abono a Fondo de Garantía
						moPago.getPartidas().get(0).setOperacion("Abono a Fondo de Garantia");
						moPago.getPartidas().get(0).setOperacionAbr("AFG");
					}
				}
				
				pagoRepository.save(moPago);
				pagosMIT.setEstado(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos());
				pagoMITRepository.save(pagosMIT);
				core.setPagoMIT(pagosMIT);
			}else {
				//Devolución, revisar si existe registrada en conciliados y epa
				if (!existePagoConcliado(pagosMIT, corresponsalEnum)) {
					core = new CorePagoMitDTO();
					core.setActivo(Boolean.TRUE);
					core.setPagoMIT(pagosMIT);

				}
			}
		}
		return core;
	}


	private List<Integer> revisarEstadosBuscar(MovCorresponsalDTO movimiento, boolean conciliado){
		List<Integer> estadosBuscar= null;
		if (!conciliado) {
			if (movimiento.getTipoOperacion().equals(Constants.TIPO_OPERACION_VENTA)) {
				estadosBuscar= estadosPagosMIT;
			}else if (movimiento.getTipoOperacion().equals(Constants.TIPO_OPERACION_DEVOLUCION)) {
				estadosBuscar= estadosDevsMIT;
			}

		}else {
			estadosBuscar = new ArrayList<>();
			estadosBuscar.add(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos());
			estadosBuscar.add(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.getEstadoPagos());
			estadosBuscar.add(EstadoPagosEnum.PAGO_CANCELADO_CORE.getEstadoPagos());
			estadosBuscar.add(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos());
		}

		return estadosBuscar;
	}

	private PagoMIT buscarPagoMITCriterios(List<Integer> estadosBuscar, MovCorresponsalDTO movimiento) {
		PagoMIT pagoMITencontrado= null;
		List<PagoMIT> listaPagosMIT= null;
		List<String> mensajesNoEncuentra= new ArrayList<>();
		boolean alfanumerico= false;
		boolean autorizacionIgual= false;
		
		listaPagosMIT = pagoMITRepository.findByCorresponsalRspOperationNumberAndEstadoIn(
				movimiento.getNumeroOperacion(),estadosBuscar);

		if (!listaPagosMIT.isEmpty()) {
			Integer aut = null;
			Integer autMov= null;
			
			for (PagoMIT pago: listaPagosMIT) {
				//Proceso para asegurarnos que el valor sea comparado por datos enteros y que sea venta(3)
				//Para evitar error en autorizaciones con 0 al inicio
				try {
					aut =  Integer.valueOf(pago.getAutorizacion());
					autMov= Integer.valueOf(movimiento.getAutorizacion());
				
				}catch(NumberFormatException temp) {
					LOG.info("Autorizacion alfanumerica, se hace conversion a integer para evitar error con valores q empiezan con 0");
					alfanumerico= true;
				}
				BigDecimal montoPagoMit = pago.getMontoTotal().setScale(2);
				BigDecimal montoArchivo = movimiento.getImporteBruto().setScale(2);
				
				if (!alfanumerico) {
					if (autMov.toString().equals(aut.toString())) {
						autorizacionIgual= true;
					}
				}else {
					if (pago.getAutorizacion().equals(movimiento.getAutorizacion())) {
						autorizacionIgual= true;
						LOG.info("Autorizacion alfanumerica, encontrada igual archivo/indice {}", movimiento.getAutorizacion());
					}
				}
				
				if (autorizacionIgual) {
					int compara= montoPagoMit.compareTo(montoArchivo);
					if (compara== 0) {
						pagoMITencontrado= pago;
						break;
					}else {
						mensajesNoEncuentra.add("Pago no encontrado por comparativa BigDecimal, montoPagoMIT="+ montoPagoMit + ","+ Constants.CAMPO_NUMERO_OPERACION +"="+   movimiento.getNumeroOperacion());
						mensajesNoEncuentra.add("Pago no encontrado por comparativa BigDecimal, montoArchivo="+ montoArchivo + ","+ Constants.CAMPO_NUMERO_OPERACION +"="+   movimiento.getNumeroOperacion());
					}
				}
			}
			//Revisar si pago fue nullo se loguean mensajes,
			if (pagoMITencontrado == null) {
				mensajesNoEncuentra.stream().forEach(msj-> LOG.info("Errores al buscar pago :{}",msj));
			}
			
		}else {
			LOG.info("Pago no encontrado por no cumplir con los criterios (numeroOperacion, estadosBuscar) numeroOperacion= {} ,estadosBuscar={}", movimiento.getNumeroOperacion() , estadosBuscar);				
		}

		
		return pagoMITencontrado;
	}
	
	
	/***
	 * Método que busca la existencia de un pago en indice mo_pagos_mit filtrando por estado, número de operación y autorización
	 * @param movimiento
	 * @param conciliado
	 * @return
	 * @throws PagoException
	 */
	private PagoMIT buscarPagoMIT(MovCorresponsalDTO movimiento, boolean conciliado) throws PagoException {
		PagoMIT pagoMITencontrado= null;
		List<Integer> estadosBuscar= null;

		estadosBuscar= revisarEstadosBuscar(movimiento, conciliado);
		
		//Se busca diferente Venta a Devolucion
		if (!movimiento.isEsDevolucion()) {
			LOG.info("Entra a busqueda pago/venta/devolucion automatica Santander/Amex, numeroOperacion= {} ",movimiento.getNumeroOperacion());
			pagoMITencontrado = buscarPagoMITCriterios(estadosBuscar, movimiento);

		}else {
			LOG.info("Entra a busqueda devolucion administrativa Amex");
			//Busqueda aplicable solo a chargeback-AMEX- Ya q no se tienen numeroOperacion
			pagoMITencontrado= aplicarFiltroDevolucion(movimiento, estadosBuscar);
		}
		if (pagoMITencontrado == null) {
			LOG.info("Pago no encontrado: numeroOperacion= {}, importe= {}, estadosBuscar {}",movimiento.getNumeroOperacion(), movimiento.getImporteBruto(),  estadosBuscar);
			
		}else {
			LOG.info("Encontrado-Entra a busqueda pago/venta/devolucion automatica Santander/Amex, numeroOperacion= {}" , movimiento.getNumeroOperacion());
		}
		return pagoMITencontrado;
	}

	private String obtenerTarjetaUltimos(String tarjetaPreconciliacion) {
		//Obtener los ultimos 4 digitos de tarjeta, ya que por TPV, asi lo guarda
		//y por preconciliación regresa 371778XXXXX1028
		String tarjeta = null;
		tarjeta= tarjetaPreconciliacion.substring(tarjetaPreconciliacion.length()-4, tarjetaPreconciliacion.length());
		return tarjeta;
	}

	private boolean existePagoConcliado(PagoMIT pagoMIT, CorresponsalEnum corresponsal) {
		boolean existe= false;

		//Devolución, revisar si existe registrada en concliados y epa
		if (corresponsal.equals(CorresponsalEnum.SANTANDER)) {
			existe = pagoConciliadoMITRepository.findByIdPago(pagoMIT.getIdPago())!= null;

		}else if (corresponsal.equals(CorresponsalEnum.AMEX)) {
			existe = pagoConciliadoEPARepository.findByIdPago(pagoMIT.getIdPago())!= null;
		}
		return existe;
	}

	/**
	 * Método que on ayuda a poblar un objeto {@link PagoMIT} para que sea almacenado sobre el indice mo_pagos_mit
	 * @param m - movimiento
	 * @param idPago identificador del indice mo_pagos
	 * @return
	 */
	private PagoMIT getPagoMIT(MovCorresponsalDTO m, String idPago, CorresponsalEnum corresponsalEnum){
		PagoMIT pago = new PagoMIT();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

		pago.setIdPago(idPago);

		if (m.getTipoOperacion().trim().equals(Constants.TIPO_OPERACION_VENTA)) {
			pago.setEstado(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.getEstadoPagos());
		}else if (m.getTipoOperacion().trim().equals(Constants.TIPO_OPERACION_DEVOLUCION) && !m.isEsSobreCargo()) {
			Devolucion devolucion = new Devolucion();
			devolucion.setFechaDevolucion(formatoFecha.format(m.getFechaDevolucion()));
			devolucion.setFechaCargoBancario(m.getFechaLiquidacion());
			devolucion.setEstadoDevolucion(m.getEstatusDevolucionId());
			pago.setDevolucion(devolucion);
			pago.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos());
			
		}else if (m.getTipoOperacion().trim().equals(Constants.TIPO_OPERACION_DEVOLUCION) && m.isEsSobreCargo()) {
			//esSobreCargo--Dev Admvas- solo AMEX- Registro CHARGEBACK
			Devolucion devolucion = new Devolucion();
			devolucion.setFechaDevolucion(formatoFecha.format(m.getFechaDevolucion()));
			devolucion.setFechaCargoBancario(null);
			devolucion.setEstadoDevolucion(m.getEstatusDevolucionId());
			pago.setDevolucion(devolucion);
			pago.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos());

		}

		pago.setIdTransaccionMIDAS("0");
		pago.setIdTransaccion("0");
		pago.setCorresponsal(getCorresponsalMIT(m, corresponsalEnum));
		pago.setAutorizacion(m.getAutorizacion());
		pago.setFechaCreacion(new Date());
		pago.setFechaOperacion(m.getFechaOperacion());
		pago.setMoneda(m.getMoneda());
		pago.setMontoTotal(m.getImporteBruto());
		pago.setPlataformaOrigen(corresponsalEnum.getNombre());
		pago.setVigente(Boolean.TRUE);

		//Agregar sucursal
		Partida part = null;
		List<Partida> partidaLista = null;
		part = new Partida();
		partidaLista = new ArrayList<>();

		if (m.getSucursal() != null) {  
			part.setNumeroSucursalPartida(Integer.valueOf(m.getSucursal()));
			//Devolución Administrativa- solo AMEX
		}else if( m.getSucursal()== null && pago.getEstado().equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos())) {
			part.setNumeroSucursalPartida(Integer.valueOf("13000"));
		}
		
		part.setImporte(m.getImporteBruto());
		part.setIdOperacion(2); //Se establece como Operación Venta al público las operaciones transformadas de 16 a 15
		partidaLista.add(part);
		pago.setPartidas(partidaLista);

		return pago;
	}


	private CorresponsalMIT getCorresponsalMIT(MovCorresponsalDTO m, CorresponsalEnum corresponsalEnum) {
		CorresponsalMIT corresponsal = null;
		String marcaT= "AMEX";

		if (m != null) {
			corresponsal = new CorresponsalMIT();

			if (corresponsalEnum.equals(CorresponsalEnum.AMEX)) {
				//Se simula la inserción que se realiza desde TPV
				corresponsal.setCcNumber(obtenerTarjetaUltimos(m.getTarjeta()));
				corresponsal.setCcType(marcaT);
			}else {
				corresponsal.setCcNumber(m.getTarjeta());
				corresponsal.setCcType(m.getMarcaTarjeta().toUpperCase());
			}
			
			corresponsal.setRspAuth(m.getAutorizacion());
			corresponsal.setRspDate(m.getFechaOperacion());
			corresponsal.setRspDsMerchant(m.getAfiliacion());
			corresponsal.setRspDsOperationType(m.getTipoOperacion());
			corresponsal.setRspOperationNumber(m.getNumeroOperacion());
			String importe = null;
			if (m.isEsDevolucion()){
				importe = m.getImporteDevolucion().toString();
			}
			else {
				importe = m.getImporteBruto()!= null? m.getImporteBruto().toString(): "0";
			}
			corresponsal.setTxAmount(importe);
			corresponsal.setTxReference(m.getReferencia());
		}
		return corresponsal;
	}

	/**
	 * Método que on ayuda a poblar un objeto {@link Pago} para que sea almacenado sobre el indice mo_pagos
	 * @param m - movimiento
	 * @return
	 */
	private Pago getPago(MovCorresponsalDTO m, CorresponsalEnum corresponsalEnum){
		Pago pago = new Pago();
		
		pago.setAutorizacion(m.getAutorizacion());
		Corresponsal corresponsal = new Corresponsal(m.getFechaOperacion(),m.getImporteBruto(),m.getAutorizacion());
		pago.setCorresponsal(corresponsal);
		pago.setFechaCreacion(new Date());

		if (m.getTipoOperacion().trim().equals(Constants.TIPO_OPERACION_VENTA)) {
			pago.setEstado(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.getEstadoPagos());
			pago.setEstadoMIT(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.toString());
			
		}else if (m.getTipoOperacion().trim().equals(Constants.TIPO_OPERACION_DEVOLUCION) && !m.isEsSobreCargo()) {
			pago.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos());
			pago.setEstadoMIT(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.toString());
			
		}else if (m.getTipoOperacion().trim().equals(Constants.TIPO_OPERACION_DEVOLUCION) && m.isEsSobreCargo()) {
			//esSobreCargo--Dev Admvas- solo AMEX- Registro CHARGEBACK
			pago.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos());
			pago.setEstadoMIT(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.toString());
		}
		pago.setIdTransaccion("0");
		pago.setIdTransaccionMIDAS("0");
		pago.setFechaOperacion(m.getFechaOperacion());
		pago.setMoneda(m.getMoneda());
		pago.setMontoTotal(m.getImporteBruto());
		pago.setPlataformaOrigen(corresponsalEnum.getNombre());
		pago.setVigente(Boolean.TRUE);
		
		//Agregar sucursal
		Partida part = null;
		List<Partida> partidaLista = null;
		part = new Partida();
		partidaLista = new ArrayList<>();

		if (m.getSucursal() != null) {
			part.setNumeroSucursalPartida(Integer.valueOf(m.getSucursal()));
			part.setSucursalOperacion(m.getSucursal());
			
			//Devolución Administrativa- solo AMEX
		}else if (m.getSucursal()== null &&  pago.getEstado() ==  EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue()) {
			part.setNumeroSucursalPartida(Integer.valueOf("13000"));
		}
		
		part.setImporte(m.getImporteBruto());
		part.setIdOperacion(2);
		partidaLista.add(part);
		pago.setPartidas(partidaLista);
		
		
		return pago;
	}

	protected CorresponsalConciliadoEPA convertirACorresponsalConciliadoEPA(MovCorresponsalDTO mov) {
		CorresponsalConciliadoEPA corresponsal= null;

		if (mov != null) {
			corresponsal = new CorresponsalConciliadoEPA();
			corresponsal.setAfiliacion(mov.getAfiliacion() != null && !mov.getAfiliacion().isEmpty() ? mov.getAfiliacion(): "");
			corresponsal.setAutorizacion(mov.getAutorizacion());
			corresponsal.setComisionTransaccional(mov.getComisionTransaccional());
			corresponsal.setComisionTransaccionalEPA(mov.getComisionTransaccionalEPA());
			corresponsal.setEstablecimiento(mov.getEstablecimiento());
			corresponsal.setEstado(mov.getEstado());
			corresponsal.setEstatusDevolucion(mov.getEstatusDevolucion());
			corresponsal.setFechaDeposito(mov.getFechaDeposito());
			corresponsal.setFechaLiquidacion(mov.getFechaLiquidacion());
			corresponsal.setFechaOperacion(mov.getFechaOperacion());
			corresponsal.setFechaDevolucion(mov.getFechaDevolucion());
			corresponsal.setHoraOperacion(mov.getHoraOperacion());
			corresponsal.setImporte(mov.getImporteNeto());
			corresponsal.setImporteBruto(mov.getImporteBruto());
			corresponsal.setImporteNeto(mov.getImporteNeto());
			corresponsal.setIvaSobretasa(mov.getIvaSobretasa() != null ?  mov.getIvaSobretasa().floatValue(): 0);
			corresponsal.setIvaTransaccional(mov.getIvaTransaccional() != null ?  mov.getIvaTransaccional().floatValue(): 0);
			corresponsal.setLote(mov.getLote());
			corresponsal.setMarcaTarjeta(mov.getMarcaTarjeta());
			corresponsal.setMoneda(mov.getMoneda());
			corresponsal.setNombreArchivo(mov.getNombreArchivo());
			corresponsal.setNombreTH(mov.getNombreTH());
			corresponsal.setNumeroMesesPromocion(mov.getNumeroMesesPromocion() != null ? mov.getNumeroMesesPromocion().toString():  "");
			corresponsal.setNumeroOperacion(mov.getNumeroOperacion());
			corresponsal.setReferencia(mov.getReferencia());
			corresponsal.setSobretasa(mov.getSobretasa() != null ?  mov.getSobretasa().floatValue(): 0);
			corresponsal.setSucursal(mov.getSucursal());
			corresponsal.setSumaSobretasa(mov.getSumaSobretasaIva() != null ?  mov.getSumaSobretasaIva().floatValue(): 0);
			corresponsal.setTarjeta(mov.getTarjeta());
			corresponsal.setTipoDevolucion(mov.getTipoDevolucion());
			corresponsal.setTipoOperacion(mov.getTipoOperacion());
			corresponsal.setTipoPago(mov.getTipoPago());
			corresponsal.setTipoTarjeta(mov.getTipoTarjeta());
			corresponsal.setUsrTrx(mov.getUsrTrx());
			corresponsal.setUsuario(mov.getUsuario());
			corresponsal.setBancoEmisor(mov.getBancoEmisor());
			corresponsal.setNombreAfiliacion(mov.getNombreAfiliacion());
			corresponsal.setImporteDevolucion(mov.getImporteDevolucion());
			corresponsal.setEsSobreCargo(mov.isEsSobreCargo());
		}
		return corresponsal;
	}

	protected CorresponsalConciliadoMIT convertirACorresponsalConciliadoMIT(MovCorresponsalDTO mov, CorePagoMitDTO core) {
		CorresponsalConciliadoMIT corresponsal= null;

		if (mov != null) {
			corresponsal= new CorresponsalConciliadoMIT();
			corresponsal.setAfiliacion(mov.getAfiliacion() != null && !mov.getAfiliacion().isEmpty() ? mov.getAfiliacion(): "");
			corresponsal.setAutorizacion(mov.getAutorizacion());
			corresponsal.setBancoEmisor(mov.getBancoEmisor());
			corresponsal.setEstado(mov.getEstado());
			corresponsal.setFechaDeposito(mov.getFechaDeposito());
			corresponsal.setFechaLiquidacion(mov.getFechaLiquidacion());
			corresponsal.setFechaOperacion(mov.getFechaOperacion());
			corresponsal.setHoraOperacion(mov.getHoraOperacion());
			corresponsal.setImporte(mov.getImporteBruto());
			corresponsal.setLote(mov.getLote());
			corresponsal.setMarcaTarjeta(mov.getMarcaTarjeta());
			corresponsal.setMoneda(mov.getMoneda());
			corresponsal.setNombreAfiliacion(mov.getNombreAfiliacion());
			corresponsal.setNombreArchivo(mov.getNombreArchivo());
			corresponsal.setNombreTH(mov.getNombreTH());
			corresponsal.setNumeroOperacion(mov.getNumeroOperacion());
			corresponsal.setReferencia(mov.getReferencia());
			corresponsal.setSucursal(mov.getSucursal());
			corresponsal.setTarjeta(mov.getTarjeta() != null && !mov.getTarjeta().isEmpty() ? Integer.valueOf(mov.getTarjeta()): 0);
			corresponsal.setTipoDevolucion(mov.getTipoDevolucion());
			corresponsal.setTipoOperacion(mov.getTipoOperacion());
			corresponsal.setTipoPago(mov.getTipoPago());
			corresponsal.setTipoTarjeta(mov.getTipoTarjeta());
			corresponsal.setUsrTrx(mov.getUsrTrx());
			corresponsal.setUsuario(mov.getUsuario());
			corresponsal.setEstatusDevolucion(mov.getEstatusDevolucion());

			//Nos traemos comision transaccional y sobretasa reportada en mo_pagos(tpv)
			if (core != null && core.getPago()!= null) {
				corresponsal.setComisionTransaccional(core.getPago().getCorresponsal().getComisionTransaccional());
				corresponsal.setSobretasa(core.getPago().getCorresponsal().getSobreTasa());
			}
			
			//Bandera para indicar pago en indice -Santander, como AMEX
			if (mov.getMarcaTarjeta().equals(Constants.CORRESPONSAL_AMEX)) {
				corresponsal.setEsAMEX(true);
			}
		}

		return corresponsal;
	}

	private boolean procesarIndices(List<MovCorresponsalDTO> resultado, Date fecha, CorresponsalEnum corresponsalEnum) {
		boolean exitoso = true;
		MovCorresponsalDTO movEjecutado= null;
		int movProcesados = 0;
		List<PagoConciliadoMIT> pagosSantanderNoEncontrados= new ArrayList<>();
		List<PagoConciliadoEPA> pagosAMEXNoEncontrados= new ArrayList<>();
		String ejecucionDetalle = null;
		try {
			ejecucionDetalle = corresponsalEnum.getNombre()+" "+ FechaUtil.convierteFechaaCadena(fecha, FechaUtil.FORMATO_FECHA, FechaUtil.ZONE_AMERICA);
			for (MovCorresponsalDTO movimiento: resultado) {
				movEjecutado = movimiento;
				CorePagoMitDTO core = updateIndicesPreconciliacion(movimiento,corresponsalEnum, fecha);

				if (core != null) {

					if (corresponsalEnum.equals(CorresponsalEnum.SANTANDER)) {
						// agregar el registro al indice mo_pagos_mit_conciliados
						PagoConciliadoMIT pagoConciliadoMIT = getPagoConciliadoMIT(movimiento, core);
						pagoConciliadoMITRepository.save(pagoConciliadoMIT);
						
						if (pagoConciliadoMIT.getEstado().equals(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.getEstadoPagos())){
							pagosSantanderNoEncontrados.add(pagoConciliadoMIT);
						}else {
							movProcesados++;
						}
							
					}else if (corresponsalEnum.equals(CorresponsalEnum.AMEX)) {
						// agregar el registro al indice mo_pagos_conciliados_epa
						PagoConciliadoEPA pagoConciliadoEPA = getPagoConciliadoEPA(movimiento, core);
						pagoConciliadoEPARepository.save(pagoConciliadoEPA);
						
						if (pagoConciliadoEPA.getEstado().equals(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.getEstadoPagos())){
							pagosAMEXNoEncontrados.add(pagoConciliadoEPA);
						}else {
							movProcesados++;
						}
					}
					
				}
			}
		}catch(PagoException ex) {
			exitoso = false;
			LOG.error(ejecucionDetalle+ " procesarIndices-ejecutarProcesoAsincrono--Error al procesar archivo de preconciliación fecha {} error generado {} en movimiento-numeroOperacion {}, referencia {}", fecha, ex, movEjecutado.getNumeroOperacion(), movEjecutado.getReferencia());
		}finally {
			LOG.info(ejecucionDetalle+ " procesarIndices-ejecutarProcesoAsincrono, se procesan {} movimientos de {}", movProcesados, resultado.size());
			int conv= convertirPagoNoEncontradoConciliado(pagosSantanderNoEncontrados, pagosAMEXNoEncontrados);
			LOG.info(ejecucionDetalle+ " procesarIndices-ejecutarProcesoAsincrono, se convierten {} de No encontrado(16) a 15", conv);
		}
		return exitoso;
	}
	
	
	private int convertirPagoNoEncontradoConciliado(List<PagoConciliadoMIT> pagosConciliadoMIT, List<PagoConciliadoEPA> pagosConciliadoEPA) {
		int convertidos = 0;
		if (!pagosConciliadoMIT.isEmpty()) {
			convertidos=  convertirPagoNoEncontradoSantander(pagosConciliadoMIT);
		}else if (!pagosConciliadoEPA.isEmpty()) {
			convertidos= convertirPagoNoEncontradoAmex(pagosConciliadoEPA);
		}
		return convertidos;
	}
	
	/** 
	 * Método para solventar problema de comunicación entre MSAplicación de Pagos, se realizará su ejecución 
	 * hasta que se tenga implementada el Reprocesamiento de PAGOS desde MIDAS.
	 * Se habilita con variable de entorno= habilitarPrePagoNoEncontradoA15= true.
	 * @param pagoConciliadoMIT
	 * @return
	 */
	private int convertirPagoNoEncontradoSantander(List<PagoConciliadoMIT> pagosConciliadoMIT) {
		int convertidos = 0;
		if (!pagosConciliadoMIT.isEmpty() && habilitarPrePagoNoEncontradoA15 != null &&  habilitarPrePagoNoEncontradoA15) {
				
			//Convertir pagos 16 a 15
			for(PagoConciliadoMIT pagoConciliadoMIT: pagosConciliadoMIT) {
				if (pagoConciliadoMIT.getEstado().equals(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.getEstadoPagos())){
					
					//pago Conciliado
					pagoConciliadoMIT.setEstado(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos());
					pagoConciliadoMITRepository.save(pagoConciliadoMIT);
					
					actualizarPagosA15(pagoConciliadoMIT.getIdPago());
					convertidos++;
				}
			}
			pagosConciliadoMIT.stream().forEach(pN-> LOG.info("pagos No encontrados-Santander A Conciliados:{}, monto:{}",pN.getCorresponsal().getNumeroOperacion(),pN.getCorresponsal().getImporte()));
		}
		return convertidos;
	}
	
	private int convertirPagoNoEncontradoAmex(List<PagoConciliadoEPA> pagosConciliadoEPA) {
		int convertidos = 0;
		if (!pagosConciliadoEPA.isEmpty() && habilitarPrePagoNoEncontradoA15 != null &&  habilitarPrePagoNoEncontradoA15) {
				
			//Convertir pagos 16 a 15
			for(PagoConciliadoEPA pagoConciliadoEPA: pagosConciliadoEPA) {
				if (pagoConciliadoEPA.getEstado().equals(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.getEstadoPagos())){
					
					//pago Conciliado
					pagoConciliadoEPA.setEstado(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos());
					pagoConciliadoEPARepository.save(pagoConciliadoEPA);
					
					actualizarPagosA15(pagoConciliadoEPA.getIdPago());
					convertidos++;
				}
			}
			pagosConciliadoEPA.stream().forEach(pN-> LOG.info("pagos No encontrados-AMEX A Conciliados:{}, monto:{}",pN.getCorresponsal().getNumeroOperacion(),pN.getCorresponsal().getImporte()));
		}
		return convertidos;
	}
	
	private void actualizarPagosA15(String idPago) {
		//pago mit
		ArrayList<String> id = new ArrayList<>();
		id.add(idPago);
		List<PagoMIT> pagoMIT16= pagoMITRepository.findByIdPagoIn(id);
		if (!pagoMIT16.isEmpty()) {
			for(PagoMIT p: pagoMIT16) {
				p.setEstado(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos());
				p.setNoEncontradoConvertA15(true);
				pagoMITRepository.save(p);
			}
		}
		//Pago
		Pago pago = pagoRepository.findById(idPago).orElse(null);
		if (pago != null) {
			pago.setEstado(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos());
			pago.setEstadoMIT(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.toString());
			pago.setNoEncontradoConvertA15(true);
			pagoRepository.save(pago);
		}
	}


	/**
	 * Método que tiene como finalidad poblar un objeto {@link PagoConciliadoMIT} para que sea almacenado
	 * dentro del indice de pago conciliados mit
	 * @param mov movimiento
	 * @return
	 */
	private PagoConciliadoMIT getPagoConciliadoMIT(MovCorresponsalDTO mov,CorePagoMitDTO core){
		PagoConciliadoMIT pagoConciliado =  new PagoConciliadoMIT();
		pagoConciliado.setIdPago(core.getPagoMIT().getIdPago());
		pagoConciliado.setFechaCreacion(getFechaCreacion()); //Asignar fecha solo con dia, mes y año

		if(core.getActivo().booleanValue()) {
			pagoConciliado.setCore(new CoreConciliadoMIT(core.getPagoMIT().getCliente(),null,core.getPagoMIT().getFechaAplicacion(),
					core.getPagoMIT().getFechaOperacion(),core.getPagoMIT().getIdTransaccionMIDAS(),core.getPagoMIT().getMoneda(),core.getPagoMIT().getMontoTotal()));
		}
		
		//Si la devolucion es administrativa se cambiara en conciliados en base al pago encontrado en mo_pagos_mit
		if(core.getPagoMIT().getEstado().equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos())) {
			mov.setTipoDevolucion(Constants.TIPO_DEVOLUCION_ADMVA);
		}
		pagoConciliado.setEstado(core.getPagoMIT().getEstado());
		pagoConciliado.setCorresponsal(convertirACorresponsalConciliadoMIT(mov, core));
		return pagoConciliado;
	}

	private Date getFechaCreacion() {
		Instant instant = new Date().toInstant();
		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT-6"));
		ZonedDateTime truncatedZonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
		Instant truncatedInstant = truncatedZonedDateTime.toInstant();
		return Date.from(truncatedInstant);

	}

	/**
	 * Método que tiene como finalidad poblar un objeto {@link PagoConciliadoEPA} para que sea almacenado
	 * dentro del indice de pago conciliados epa
	 * @param mov
	 * @return
	 */
	private PagoConciliadoEPA getPagoConciliadoEPA(MovCorresponsalDTO mov,CorePagoMitDTO core){
		PagoConciliadoEPA pagoConciliado = new PagoConciliadoEPA();

		pagoConciliado.setIdPago(core.getPagoMIT().getIdPago());
		pagoConciliado.setFechaCreacion(getFechaCreacion()); //Asignar fecha solo con dia, mes y año

		if(core.getActivo().booleanValue()) {
			pagoConciliado.setCore(new CoreConciliadoMIT(core.getPagoMIT().getCliente(),null,core.getPagoMIT().getFechaAplicacion(),
					core.getPagoMIT().getFechaOperacion(),core.getPagoMIT().getIdTransaccionMIDAS(),core.getPagoMIT().getMoneda(),core.getPagoMIT().getMontoTotal()));
		}
		//Si la devolucion es administrativa se cambiara en conciliados en base al pago encontrado en mo_pagos_mit
		if(core.getPagoMIT().getEstado().equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos()) ||
				core.getPagoMIT().getEstado().equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos())) {
			mov.setTipoDevolucion(Constants.TIPO_DEVOLUCION_ADMVA);
			mov.setEsSobreCargo(true);
		}
		pagoConciliado.setEstado(core.getPagoMIT().getEstado());
		pagoConciliado.setCorresponsal(convertirACorresponsalConciliadoEPA(mov));
		return pagoConciliado;
	}

	public synchronized boolean  procesoAsincrono(List<MovCorresponsalDTO> resultado, PreConciliacionDTO preConciliacionDTO, CorresponsalEnum corresponsalEnum) throws PreconciliacionExcetion {
		
		if (preConciliacionDTO.isReprocesamiento()) {
			reProcesamiento(preConciliacionDTO, corresponsalEnum);
		}
		
		//Proceso asincrono- inserción en indices.
		LOG.info("Entra método procesoAsincrono- synchronized, {}",preConciliacionDTO.getFecha());
		RestPreconcilacionDTO resp= null;
		boolean exito = true;
		EstadoEjecucionEnum ejecucion= null;
		try {
			LOG.info("EjecutarProcesoAsincrono-Incia proceso asincrono de preconciliación fecha {}", preConciliacionDTO.getFecha());
			ejecucion= procesarIndices(resultado, preConciliacionDTO.getFecha(), corresponsalEnum)?EstadoEjecucionEnum.DESCARGACORRECTA:  EstadoEjecucionEnum.DESCARGAINCORRECTA;
			if (preConciliacionDTO.isActualizarProcesoCron()) {
				resp = pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(preConciliacionDTO.getIdProcesoCron(), ejecucion, Constants.WSPRECONCILIACION);
			}

		}catch(PreconciliacionExcetion ex) {
			exito= false;
			if (preConciliacionDTO.isActualizarProcesoCron()) {
				resp = pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(preConciliacionDTO.getIdProcesoCron(), EstadoEjecucionEnum.DESCARGAINCORRECTA, Constants.WSPRECONCILIACION);
				LOG.error("EjecutarProcesoAsincrono-Error al procesar archivo de preconciliación fecha {} error generado {}", preConciliacionDTO.getFecha(), ex);
			}
		}finally {
			if(preConciliacionDTO.isActualizarProcesoCron()) {
				String textLog= resp != null && resp.getCode().equals(HttpStatus.OK.toString()) ? "Se actualizo el estado de la ejecución": "Error al actualizar el estado de la ejecución";
				LOG.info(textLog);
			}
			LOG.info("EjecutarProcesoAsincrono-Finaliza método asincrono de fecha {}",preConciliacionDTO.getFecha() );
			LOG.info("Finaliza método procesoAsincrono- synchronized, {}",preConciliacionDTO.getFecha());
		}
		return exito;
	}


	private PagoMIT aplicarFiltroDevolucion(MovCorresponsalDTO movimiento, List<Integer> estadosBuscar) throws PagoException{
		BoolQueryBuilder builder= QueryBuilders.boolQuery();
		List<PagoMIT> resultadoFiltros = new ArrayList<>();
		PagoMIT pagoMIT= null;
		BigDecimal montoArchivo = movimiento.getImporteBruto().setScale(2);
		
		//Construir filtros dinámicos
		try {
			//Armado Query
			builder.must(QueryBuilders.existsQuery("plataformaOrigen")); //filtro default

			//AMEX
			builder.must( QueryBuilders.matchBoolPrefixQuery("plataformaOrigen", "MIT AMEX"));


			//tarjeta
			if (movimiento.getTarjeta()!= null) {
				String tarjeta = obtenerTarjetaUltimos(movimiento.getTarjeta());
				builder.must( QueryBuilders.matchBoolPrefixQuery("corresponsal.getCc_Number", tarjeta));
			}

			//estado
			if (estadosBuscar != null) {
				builder.must( QueryBuilders.termsQuery(Constants.FILTRO_ESTADO, estadosBuscar));
			}

			//fecha
			if (movimiento.getFechaOperacion() != null) {

				Date iniF= FechaUtil.obtenerFechaIni(movimiento.getFechaOperacion());
				Date finF= FechaUtil.obtenerFechaFin(movimiento.getFechaOperacion());

				builder.filter(QueryBuilders.rangeQuery("fechaOperacion").timeZone("America/Mexico_City")
						.gte(iniF.getTime())
						.lte(finF.getTime()));
			}

			NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
			nativeSearchQueryBuilder.withQuery(builder);
			NativeSearchQuery n = nativeSearchQueryBuilder.build();
			SearchHits<PagoMIT> resultado= elasticTemplate.search(n, PagoMIT.class);


			for(SearchHit<PagoMIT> a : resultado) {
				resultadoFiltros.add(a.getContent());
			}
			if (!resultadoFiltros.isEmpty()) {
				
				for (PagoMIT pa: resultadoFiltros) {
					//Comparar monto
					BigDecimal montoPagoMit = pa.getMontoTotal();
					int compara= montoPagoMit.compareTo(montoArchivo);
					if (compara== 0) {
						pagoMIT= pa;
						break;
					}else {
						LOG.info("Devolucion administrativa Amex no encontrada, montoMIT no coincide= {} con montoArchivo={}", montoPagoMit, montoArchivo);
					}
				}
			}else {
				LOG.info("Devolucion administrativa Amex no encontrada, filtro= {}", builder);
			}

		}catch(Exception ex) {
			LOG.error(Constants.GENERIC_EXCEPTION_INITIAL_MESSAGE, ex);
			throw PagoException.ERROR_DEVOLUCION_GENERAR_FILTRO;
		}
		return pagoMIT;
	}

	public void reProcesamiento(PreConciliacionDTO preConciliacionDTO, CorresponsalEnum corresponsal) {

		if (preConciliacionDTO != null && preConciliacionDTO.getFecha() != null && preConciliacionDTO.isReprocesamiento()) {

			//Para iniciar reprocesamiento, es necesario obtener el nombre del archivo ejecutado de acuerdo a la fecha
			String nombreArchivo= ArchivoSFTPUtil.obtenerNombreArchivo(preConciliacionDTO.getFecha(), corresponsal);
			
			//Obtener ids de indices conciliados
			List<String> idsBorrar= reProcesarPagosConciliadosPorArchivo(corresponsal, preConciliacionDTO.getFecha(), nombreArchivo);
			
			//Actualizar estatus en indice mo_pagos_mit
			actualizarPagosMIT(idsBorrar, corresponsal);

			//Actualizar estatus en indice mo_pagos
			actualizarPagos(idsBorrar);

			LOG.info("Se  termina reprocesamiento de fecha= {}", preConciliacionDTO.getFecha());
		}
	}

	private void actualizarPagos(List<String> idsBorrar) {
		int countActPagoA3= 0;
		int countDel= 0;
		int countActDev20A13= 0;
		int countActDev21A14= 0;

		for(String id: idsBorrar) {
			Pago p = pagoRepository.findById(id).orElse(null);
			if (p != null) {
				if(p.getEstado() == EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos().intValue() && !p.isNoEncontradoConvertA15()) {
					//Se cambia de Pago Enviado a conciliar(15) a Pago Aplicado(3)
					p.setEstado(EstadoPagosEnum.PAGO_APLICADO.getEstadoPagos());
					pagoRepository.save(p);
					countActPagoA3++;
				}else if (p.getEstado() == EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos().intValue() && p.isNoEncontradoConvertA15()){
					pagoRepository.delete(p);
					countDel++;
					
				}else if (p.getEstado() == EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue() ||
						p.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue() || 
						p.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue() ||
						p.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue() ||
						p.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue() ||
						p.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue() ||
						p.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue()) {
						
					//Se elimina Pago Sin Cargo Reconocido (16)-se genera en preconciliación por no econtrar Pago Aplicado(3)
					//Se elimina Devolucion sin Cargo Reconocido-18 por no encontrar devolucion en 13
					//Se elimina Devolucion Admva sin Cargo Reconocido-22 por no encontrar devolucion Admva en 14
					pagoRepository.delete(p);
					countDel++;

				}else if(p.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue() ||
						p.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos().intValue()) {
					//Se cambia de Devolucion Liquidada Procesada(20) o Liquidada (19) a Devolucion Automatica(13)
					p.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos());
					pagoRepository.save(p);
					countActDev20A13++;
					
				}else if(p.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos().intValue() ||
						p.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue()) {
					//Se agrega reprocesamiento para  devs-Admvas--estaado liquidao (17, 21)
					p.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos());
					pagoRepository.save(p);
					countActDev21A14++;
				}
				
				else {
					LOG.info("No se actualiza-Pago, es pago en estatus 3= {}", p.getEstado());
					LOG.info("No se actualiza-Pago, es pago en estatus 3= {}", p.getId());
				}
			}
		}
		LOG.info("Se actualizan {} registros de indice Pagos de 15 a 3", countActPagoA3 );
		LOG.info("Se eliminan {} registros de indice Pagos por pago y devolucion no encontrado(16, 18)", countDel );
		LOG.info("Se actualizan {} devoluciones automaticas de indice Pagos de 20/19 a 13- Solicitada", countActDev20A13 );
		LOG.info("Se actualizan {} devoluciones administrativas de indice Pagos de 21/17 a 14- Solicitada", countActDev21A14 );
	}

	private boolean revisaPagoMITEliminarEnReproceso(Integer estado) {
		boolean elimina = false;
		
		if (estado.equals(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO.getEstadoPagos())||
				estado.equals(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos()) ||
				estado.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos()) ||
				
				estado.equals(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos()) ||
				estado.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos()) ||
				
				estado.equals(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos()) ||
				estado.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos())){
			elimina= true;
		}
			
		
		return elimina;
	}
	
	private boolean revisarPagoMITConvertidoA15EliminarEnReproceso(PagoMIT pagoMIT) {
		boolean elimina = false;
		
		if(pagoMIT.getEstado().equals(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos()) && pagoMIT.isNoEncontradoConvertA15()){
			elimina= true;
		}
		return elimina;
	
	}
	
	private void actualizarDevsMITReproceso(List<PagoMIT> pagosMIT, CorresponsalEnum corresponsal) {
		int countActDev13Sol= 0;
		int countActDev14Sol= 0;
		int countActDev20A13= 0;
		int countActDev21A14= 0;

		for(PagoMIT pMIT: pagosMIT) {
			//Si es devolucion automatica, se deja en estatus SOLICITADA
			if(pMIT.getEstado().equals(EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos()) ) {
				pMIT.getDevolucion().setEstadoDevolucion(2);
				pagoMITRepository.save(pMIT);
				countActDev13Sol++;
			}else if (pMIT.getEstado().equals(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos()) ||
					pMIT.getEstado().equals(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos())) {
				//devoluciones Automaticas liquidadas-Procesadas(20) o Liquidadas (19), se regresa a estatus 13
				pMIT.getDevolucion().setEstadoDevolucion(2);
				pMIT.getDevolucion().setFechaCargoBancario(null);
				pMIT.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos());
				pagoMITRepository.save(pMIT);
				countActDev20A13++;
			}else if (pMIT.getEstado().equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos())){
				//Se agrega reprocesamiento para  devs-Admvas--estado 14
				
				//Solo para AMEX- chargebacks
				if (corresponsal.equals(CorresponsalEnum.AMEX)) {
					pMIT.getDevolucion().setEstadoDevolucion(2);
					pMIT.getDevolucion().setFechaCargoBancario(null);
					pagoMITRepository.save(pMIT);
					countActDev14Sol++;
				}

			}else if (pMIT.getEstado().equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos()) ||
					pMIT.getEstado().equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos())) {
				//Se agrega reprocesamiento para  devs-Admvas--estaado liquidao (17, 21)
				pMIT.getDevolucion().setEstadoDevolucion(2);
				pMIT.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos());
				pMIT.getDevolucion().setFechaCargoBancario(null);
				pagoMITRepository.save(pMIT);
				countActDev21A14++;
			}else {
				LOG.info("No se actualiza-PagosMIT, es Pago3 id= {}", pMIT.getIdPago());
			}
		}
		LOG.info("Se actualizan {} devoluciones de indice MIT de 13 Automatica-a Solicitada", countActDev13Sol );
		LOG.info("Se actualizan {} devoluciones de indice MIT de 14 Administrativa-a- Solicitada", countActDev14Sol );
		LOG.info("Se actualizan {} devoluciones de indice MIT de 20/19 a 13- Solicitada", countActDev20A13 );
		LOG.info("Se actualizan {} devoluciones de indice MIT de 21/17 a 14- Solicitada", countActDev21A14 );
	}
	
	private void actualizarPagosMIT(List<String> idsBorrar, CorresponsalEnum corresponsal) {
		int countActPagoA3= 0;
		int countDel= 0;
		
		//Actualizar estatus en indice mo_pagos_mit
		List<PagoMIT> pagosMIT = pagoMITRepository.findByIdPagoIn(idsBorrar);
		if (!pagosMIT.isEmpty()) {
			for (PagoMIT pMIT: pagosMIT) {
				if(pMIT.getEstado().equals(EstadoPagosEnum.PAGO_ENVIADO_CONCILIAR.getEstadoPagos()) && !pMIT.isNoEncontradoConvertA15()) {
					//Se cambia de Pago Enviado a conciliar(15) a Pago Aplicado(3)
					pMIT.setEstado(EstadoPagosEnum.PAGO_APLICADO.getEstadoPagos());
					pagoMITRepository.save(pMIT);
					countActPagoA3++;
				}
				else if (revisaPagoMITEliminarEnReproceso(pMIT.getEstado()) || revisarPagoMITConvertidoA15EliminarEnReproceso(pMIT)) {
					//Se elimina Pago Sin Cargo Reconocido (16)-se genera en preconciliación por no econtrar Pago Aplicado(3)
					//Se elimina Devolucion Automatica sin Cargo Reconocido-18 por no encontrar devolucion Aut en 13
					//Se elimina Devolucion Admva sin Cargo Reconocido-22 por no encontrar devolucion Admva en 14
					pagoMITRepository.delete(pMIT);
					countDel++;
				}else {
					//Si es devolucion automatica, se deja en estatus SOLICITADA
					actualizarDevsMITReproceso(pagosMIT, corresponsal);
				}
			}
		}
		LOG.info("Se actualizan {} registros de indice MIT de 15 a estatusPago= 3", countActPagoA3 );
		LOG.info("Se eliminan {} registros de indice MIT por pago y devolucion no encontrado(16, 18)", countDel );
	}
	
	private List<String> reProcesarPagosConciliadosPorArchivo(CorresponsalEnum corresponsal, Date fecha,  String nombreArchivo) {
		int countConciliados= 0;
		List<String> idsReprocesar= new ArrayList<>();

		String ejecucionDetalle = corresponsal.getNombre()+" "+ FechaUtil.convierteFechaaCadena(fecha, FechaUtil.FORMATO_FECHA, FechaUtil.ZONE_AMERICA);
		LOG.info(ejecucionDetalle+ " Se  inicia reprocesamiento de corresponsal= {}", corresponsal);
		LOG.info(ejecucionDetalle+ " Se  inicia reprocesamiento de fecha= {}", fecha);
		LOG.info(ejecucionDetalle+ " Se  inicia reprocesamiento de archivo= {}", nombreArchivo);


		if (corresponsal.equals(CorresponsalEnum.SANTANDER)) {
			List<PagoConciliadoMIT> pagosSantander= pagoConciliadoMITRepository.findByCorresponsalNombreArchivo(nombreArchivo);
			if (!pagosSantander.isEmpty()) {
				pagosSantander.stream().forEach(p-> idsReprocesar.add(p.getIdPago()));
				countConciliados= reprocesaSantander(pagosSantander);
			}
			
		}else if (corresponsal.equals(CorresponsalEnum.AMEX)) {
			List<PagoConciliadoEPA> pagosAMEX= pagoConciliadoEPARepository.findByCorresponsalNombreArchivo(nombreArchivo);
			if(!pagosAMEX.isEmpty()) {
				pagosAMEX.stream().forEach(p-> idsReprocesar.add(p.getIdPago()));
				countConciliados= reprocesaAMEX(pagosAMEX);
			}
		}
		LOG.info(ejecucionDetalle+ " Se borran {} registros de indices conciliados", countConciliados );
		return idsReprocesar;
	}

	private int reprocesaSantander(List<PagoConciliadoMIT> pagosSantander) {
		int countConciliados= 0;
		if (!pagosSantander.isEmpty()) {
			for(PagoConciliadoMIT pSant: pagosSantander) {
				pagoConciliadoMITRepository.delete(pSant);
				countConciliados++;
			}
		}
		return countConciliados;
	}

	private int reprocesaAMEX(List<PagoConciliadoEPA> pagosAMEX) {
		int countConciliados= 0;
		for(PagoConciliadoEPA pAmex: pagosAMEX) {
			pagoConciliadoEPARepository.delete(pAmex);
			countConciliados++;
		}
		return countConciliados;
	}

}
