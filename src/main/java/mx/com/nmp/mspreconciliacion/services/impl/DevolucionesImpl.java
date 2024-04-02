package mx.com.nmp.mspreconciliacion.services.impl;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.*;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.model.enums.EstadoPagosEnum;
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
import mx.com.nmp.mspreconciliacion.services.DevolucionesService;
import mx.com.nmp.mspreconciliacion.util.FechaUtil;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 * @author QuarkSoft
 *
 */

@Service
public class DevolucionesImpl implements DevolucionesService {

	/**
	 * Log de actividades en el servidor
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DevolucionesImpl.class);


	@Autowired
	private ElasticsearchOperations elasticTemplate;

	@Autowired
	private IPagoConciliadoEPARepository pagoConciliadoEPARepository;

	@Autowired
	private IPagoConciliadoMITRepository pagoConciliadoMITRepository;

	@Autowired
	private IPagoMITRepository pagoMITRepository;

	@Autowired
	private IPagoRepository pagoRepository;

	/**
	 * Consulta listado de devoluciones
	 * @param requestDevolucionesDTO
	 * @return List<ResponseDevolucionesDTO>
	 */
	@Override
	public DevolucionPaginadoDTO consultarDevoluciones(RequestDevolucionesDTO requestDevolucionesDTO) throws PagoException{
		DevolucionPaginadoDTO responseDevoluciones= null;

		if (requestDevolucionesDTO != null) {
			if (requestDevolucionesDTO.getNumeroRegistros() > 0) {
				List<String> idsTermsFiltro= consultarEnConciliados(requestDevolucionesDTO);

				SearchHits<PagoMIT> list= ejecutarConsultaDevoluciones(requestDevolucionesDTO, idsTermsFiltro);
				if (list != null) {
					responseDevoluciones= armaRespuestaDevoluciones(list, requestDevolucionesDTO);
				}
			}else {
				LOG.info(PagoException.ERROR_DEVOLUCION_PAGINADO.getDescripcion());
				throw PagoException.ERROR_DEVOLUCION_PAGINADO;
			}
		}
		return responseDevoluciones;
	}


	@Override
	public List<ResponseDevolucionesDTO> consultarDevolucionesEPA(DevolucionAMEXDTO requestDevolucionAMEXDTO) throws PagoException {
		List<ResponseDevolucionesDTO> respuesta= null;
		if (requestDevolucionAMEXDTO != null) {
			List<PagoConciliadoEPA> devolucionesList= ejecutarConsultaDevolucionesEPA(requestDevolucionAMEXDTO);
			respuesta= mapearDevolucionesAMEX(devolucionesList);
		}
		return respuesta;
	}

	@Override
	public List<ResponseDevolucionesDTO> actualizarEstatusLiquidar(ActualizarDevolucionDTO movimientosLiquidar) throws PagoException {
		List<ResponseDevolucionesDTO> respuesta = new ArrayList<>();
		List<MovDevolucionDTO> liquidarLista= null;
		List<String> idsLista = new ArrayList<>();
		List<PagoMIT> pagosActualizados= null;

		if (movimientosLiquidar != null && movimientosLiquidar.getCorresponsal() != null && !movimientosLiquidar.getDevolucionesLiquidar().isEmpty()) {
			liquidarLista = movimientosLiquidar.getDevolucionesLiquidar().stream().filter(MovDevolucionDTO::getLiquidar).collect(Collectors.toList());

			if (!liquidarLista.isEmpty()) {
				liquidarLista.forEach(mov-> idsLista.add(mov.getIdPago()));
				List<PagoMIT> pagosLiquidar= pagoMITRepository.findByIdPagoIn(idsLista);
				LOG.info("actualizarEstatusLiquidar- IdPagos a liquidar: ");
				idsLista.stream().forEach(LOG::info);

				if (!pagosLiquidar.isEmpty()) {
					pagosActualizados = actualizarEstatusDevPagoMIT(pagosLiquidar, liquidarLista, movimientosLiquidar.getProcesoAutomatico().booleanValue());

					if (movimientosLiquidar.getCorresponsal().equals(CorresponsalEnum.SANTANDER.getNombre())) {
						LOG.info("actualizarEstatusLiquidar- Actualizar indice SANTANDER");
						actualizarIndice(liquidarLista,idsLista, CorresponsalEnum.SANTANDER, movimientosLiquidar.getProcesoAutomatico(), pagosActualizados);
					}else if (movimientosLiquidar.getCorresponsal().equals(CorresponsalEnum.AMEX.getNombre())) {
						LOG.info("actualizarEstatusLiquidar- Actualizar indice AMEX");
						actualizarIndice(liquidarLista,idsLista, CorresponsalEnum.AMEX, movimientosLiquidar.getProcesoAutomatico(), pagosActualizados);
					}
				}
				respuesta= mapearDevolucionesMIT(pagosActualizados);
			}
		}
		return respuesta;
	}

	private void actualizarIndice(List<MovDevolucionDTO> devolucionesLiquidar, List<String> idsLista, CorresponsalEnum corresponsal, boolean automatico, List<PagoMIT> pagosActualizados) {
		if (devolucionesLiquidar != null && corresponsal != null) {
			if (corresponsal.equals(CorresponsalEnum.SANTANDER)) {
				actualizarEstatusDevPagoConciliadoMIT(devolucionesLiquidar,idsLista, automatico, pagosActualizados);
			}else if (corresponsal.equals(CorresponsalEnum.AMEX)) {
				actualizarEstatusDevPagoConciliadoEPA(devolucionesLiquidar, idsLista, automatico, pagosActualizados);
			}
		}

	}

	private Date obtenerFechaCBancario(List<MovDevolucionDTO> liquidarLista, boolean automatico, PagoMIT pago) {
		Date fechaCargoBancario= null;
		if (automatico) {
			//Proceso cron establece fecha de ejecución como Fecha de liquidación
			fechaCargoBancario= Date.from(Instant.now());
		}else {
			Optional<MovDevolucionDTO> obtenerFecha= liquidarLista.stream().filter(dev-> dev.getIdPago().equals(pago.getIdPago())).findFirst();
			fechaCargoBancario = obtenerFecha.isPresent()?obtenerFecha.get().getFechaCargoBancario(): null;

		}
		return FechaUtil.obtenerFechaIni(fechaCargoBancario);
	}

	private List<PagoMIT> actualizarEstatusDevPagoMIT(List<PagoMIT> pagosLiquidar, List<MovDevolucionDTO> liquidarLista, boolean automatico) {
		List<PagoMIT> pagosActualizados= new ArrayList<>();
		if (!pagosLiquidar.isEmpty()) {
			pagosActualizados= actualizarPagosALiquidar(pagosLiquidar, liquidarLista, automatico);
		}
		return pagosActualizados;
	}

	private List<PagoMIT> actualizarPagosALiquidar(List<PagoMIT> pagosLiquidar, List<MovDevolucionDTO> liquidarLista, boolean automatico) {
		List<PagoMIT> pagosActualizados= new ArrayList<>();
		int countAct= 0;

		for(PagoMIT pago: pagosLiquidar) {
			//Asignar Fecha Cargo Bancario y estatus= Liquidada
			if (pago.getDevolucion() != null && !pago.getDevolucion().getEstadoDevolucion().equals(Constants.ESTATUS_DEVOLUCION_LIQUIDADA_ID)) {

				Date fechaCargoBancario= obtenerFechaCBancario(liquidarLista, automatico, pago);
				if (fechaCargoBancario != null) {
					countAct++;
					if (actualizarPagoLiquidado(pago, fechaCargoBancario))
						pagosActualizados.add(pago);
				}
			}
		}
		LOG.info("actualizarEstatusDevPagoMIT- Número de devoluicones actualizadas {}", countAct);
		return pagosActualizados;
	}
	
	private PagoMIT actualizarPagoMIT(PagoMIT pago) {
		//Actualizar indice mo_pagos a estatus 17 solo admvas
		//14
		if (pago.getEstado().equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos())) {
			pago.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos());
			
		//13					
		}else if (pago.getEstado().equals(EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos())){
			pago.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos());
			
		//22
		}else if (pago.getEstado().equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos())) {
			pago.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos());
		//18
		}else if (pago.getEstado().equals(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos())) {
			pago.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos());
		}
		pagoMITRepository.save(pago);
		
		return pago;
	}

	private boolean actualizarPagoLiquidado(PagoMIT pago, Date fechaCargoBancario) {
		boolean actualizar = false;
		if (pago != null) {

			Pago pagoP = pagoRepository.findById(pago.getIdPago()).orElse(null);
			if (pagoP != null) {

				//Si la devolucion es Administrativa- 14
				if (pagoP.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos().intValue()){
					pagoP.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos());
					pagoP.setEstadoMIT(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.toString());
					LOG.info("actualizarEstatusDevPagoMIT- Actualización indice mo_pagos- es Admva14- a estatus 17 idpago {}", pagoP.getId());
					
				//Si la devolucion es Automatica- 13	
				}else if (pagoP.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos().intValue()){
					pagoP.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos());
					pagoP.setEstadoMIT(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.toString());
					LOG.info("actualizarEstatusDevPagoMIT- Actualización indice mo_pagos- es Automatica13- a estatus 19 idpago {}", pagoP.getId());
					
				//Si la devolucion es Administrativa- 22 (Devolucion Admv No reconocida)	
				}else if(pagoP.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue()) {
					pagoP.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos());
					pagoP.setEstadoMIT(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.toString());
					LOG.info("actualizarEstatusDevPagoMIT- Actualización indice mo_pagos- es Admva No Reconocida22- a estatus 24 idpago {}", pagoP.getId());
					
				//Si la devolucion es Automatica- 18 (Devolucion Automatica No Reconocida)
				}else if(pagoP.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue()) {
					pagoP.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos());
					pagoP.setEstadoMIT(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.toString());
					LOG.info("actualizarEstatusDevPagoMIT- Actualización indice mo_pagos- es Automatica No Reconocida18- a estatus 23 idpago {}", pagoP.getId());
				}
				pagoRepository.save(pagoP);
				pago.getDevolucion().setFechaCargoBancario(fechaCargoBancario);
				pago.getDevolucion().setEstadoDevolucion(Constants.ESTATUS_DEVOLUCION_LIQUIDADA_ID);

				actualizarPagoMIT(pago);
				actualizar = true;
				LOG.info("actualizarPagoLiquidado- Actualización indice mo_pagos_mit- idpago {}", pago.getIdPago());
			}else {
				LOG.info("actualizarPagoLiquidado- No es posible actualizar el pago, no se encuentra id, en mo_pagos {}",pago.getIdPago());
			}
		}
		return actualizar;
	}


	private void actualizarEstatusDevPagoConciliadoEPA(List<MovDevolucionDTO> devolucionesLiquidar, List<String> idsLista, boolean automatico, List<PagoMIT> pagosActualizados) {
		Date fechaCargoBancario = null;

		List<PagoConciliadoEPA> pagosEPALiquidar = pagoConciliadoEPARepository.findByIdPagoIn(idsLista);
		for(PagoConciliadoEPA pagoEPA: pagosEPALiquidar) {
			if (!pagoEPA.getCorresponsal().getEstatusDevolucion().equals(Constants.ESTATUS_DEVOLUCION_LIQUIDADA)) {

				for(MovDevolucionDTO mov: devolucionesLiquidar) {
					if (mov.getIdPago().equals(pagoEPA.getIdPago())) {
						fechaCargoBancario= FechaUtil.obtenerFechaIni(mov.getFechaCargoBancario());
					}
				}
				if (automatico) //Proceso cron estaablece fecha de ejecución como Fecha de liquidación
					fechaCargoBancario = Date.from(Instant.now());
				
				Integer estatusMIT= obtenerEstatusActLiquidacion(pagosActualizados, pagoEPA.getIdPago());

				pagoEPA.getCorresponsal().setFechaLiquidacion(fechaCargoBancario);
				pagoEPA.getCorresponsal().setEstatusDevolucion(Constants.ESTATUS_DEVOLUCION_LIQUIDADA);
				pagoEPA.setEstado(estatusMIT);
				pagoConciliadoEPARepository.save(pagoEPA);
				LOG.info("actualizarEstatusDevPagoConciliadoEPA- Actualización indice mo_pagos_conciliados_epa- idpago {}", pagoEPA.getIdPago());
			}
		}
	}
	
	private Integer obtenerEstatusActLiquidacion(List<PagoMIT> pagosActualizados, String idPago ) {
		Integer estatusMIT= null;
		Optional<PagoMIT> pagoEstatus=  pagosActualizados.stream().filter(pMit-> pMit.getIdPago().equals(idPago)).findFirst();
		if(pagoEstatus.isPresent()) {
			estatusMIT = pagoEstatus.orElse(null).getEstado();
		}
		
		return estatusMIT;
	}

	private Date obtenerFechaLiquidacion(List<MovDevolucionDTO> devolucionesLiquidar,PagoConciliadoMIT pagoCon, boolean automatico) {
		Date fechaCargoBancario = null;
		
		Optional<MovDevolucionDTO> obtenerFecha= devolucionesLiquidar.stream().filter(dev-> dev.getIdPago().equals(pagoCon.getIdPago())).findFirst();
		if (obtenerFecha.isPresent()) {
			String fecha= FechaUtil.convierteFechaaCadena(obtenerFecha.get().getFechaCargoBancario(), FechaUtil.FORMATO_FECHA);
			fechaCargoBancario= FechaUtil.convierteaFecha(fecha, FechaUtil.FORMATO_FECHA);
		}

		if (automatico) //Proceso cron estaablece fecha de ejecución como Fecha de liquidación
			fechaCargoBancario = Date.from(Instant.now());
		
		return fechaCargoBancario;
	}
	
	private void actualizarEstatusDevPagoConciliadoMIT(List<MovDevolucionDTO> devolucionesLiquidar, List<String> idsLista, boolean automatico, List<PagoMIT> pagosActualizados) {
		Date fechaCargoBancario = null;

		if (!pagosActualizados.isEmpty()) {
			List<PagoConciliadoMIT> pagosConciliadosLiquidar= pagoConciliadoMITRepository.findByIdPagoIn(idsLista);
			for(PagoConciliadoMIT pagoCon: pagosConciliadosLiquidar) {
				if (pagoCon.getCorresponsal().getEstatusDevolucion() == null) {
					pagoCon.getCorresponsal().setEstatusDevolucion("");
					pagoConciliadoMITRepository.save(pagoCon);
				}
				if (pagoCon.getCorresponsal().getEstatusDevolucion() != null && !pagoCon.getCorresponsal().getEstatusDevolucion().equals(Constants.ESTATUS_DEVOLUCION_LIQUIDADA)) {
	
					fechaCargoBancario= obtenerFechaLiquidacion(devolucionesLiquidar, pagoCon, automatico);
					Integer estatusMIT= obtenerEstatusActLiquidacion(pagosActualizados, pagoCon.getIdPago());

					pagoCon.getCorresponsal().setFechaLiquidacion(fechaCargoBancario);
					pagoCon.getCorresponsal().setEstatusDevolucion(Constants.ESTATUS_DEVOLUCION_LIQUIDADA);
					pagoCon.setEstado(estatusMIT);
					
					pagoConciliadoMITRepository.save(pagoCon);
					LOG.info("actualizarEstatusDevPagoConciliadoMIT- Actualización indice mo_pagos_conciliados_mit- idpago {}", pagoCon.getIdPago());
				}
			}
		}
	}

	private DevolucionPaginadoDTO armaRespuestaDevoluciones(SearchHits<PagoMIT> resultado, RequestDevolucionesDTO requestDevolucionesDTO) throws PagoException {
		DevolucionPaginadoDTO responseDevoluciones= new DevolucionPaginadoDTO();
		List<ResponseDevolucionesDTO> respuestaLista= null;
		boolean masPaginas= false;

		if (resultado != null) {
			long totalReg= resultado.getTotalHits();
			if (totalReg > 0) {

				List<PagoMIT> resultadoFiltros = new ArrayList<>();
				for(SearchHit<PagoMIT> a : resultado) {
					resultadoFiltros.add(a.getContent());
				}
				respuestaLista= procesarPagos(resultadoFiltros);
				responseDevoluciones.setTotalRegistros((int)totalReg);

				int registrosPaginados = requestDevolucionesDTO.getNumeroPagina() > 0 ? requestDevolucionesDTO.getNumeroPagina() * requestDevolucionesDTO.getNumeroRegistros() + requestDevolucionesDTO.getNumeroRegistros(): requestDevolucionesDTO.getNumeroRegistros();
				if (registrosPaginados < totalReg)
					masPaginas= true;
			}
		}
		responseDevoluciones.setNumeroPagina(requestDevolucionesDTO.getNumeroPagina());
		responseDevoluciones.setNumeroRegistros(respuestaLista!= null?respuestaLista.size():0);
		responseDevoluciones.setTieneMasPaginas(masPaginas);
		responseDevoluciones.setDevolucionesList(respuestaLista);
		return responseDevoluciones;
	}

	private SearchHits<PagoMIT> ejecutarConsultaDevoluciones(RequestDevolucionesDTO requestDevolucionesDTO, List<String> idsTermsFiltro) throws PagoException {
		SearchHits<PagoMIT> resultado = null;
		BoolQueryBuilder builder= null;

		if (requestDevolucionesDTO.getNumeroRegistros() > 0) {
			PageRequest page = PageRequest.of(requestDevolucionesDTO.getNumeroPagina(), requestDevolucionesDTO.getNumeroRegistros());
			builder = aplicarFiltros(requestDevolucionesDTO, idsTermsFiltro);

			NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
			nativeSearchQueryBuilder.withQuery(builder);
			nativeSearchQueryBuilder.withPageable(page);
			NativeSearchQuery n = nativeSearchQueryBuilder.build();

			resultado= elasticTemplate.search(n, PagoMIT.class);
		}
		return resultado;
	}

	private List<String> consultarEnConciliados(RequestDevolucionesDTO requestDevolucionesDTO) {
		List<String> idsDevoluciones= new ArrayList<>();

		if (requestDevolucionesDTO != null) {
			if (requestDevolucionesDTO.getCorresponsal().equals(CorresponsalEnum.SANTANDER.getNombre())) {
				//devoluciones Santander
				List<PagoConciliadoMIT> devolucionesSantander = pagoConciliadoMITRepository.findByCorresponsalTipoOperacion(Constants.TIPO_OPERACION_DEVOLUCION);
				if (!devolucionesSantander.isEmpty()) {
					devolucionesSantander.forEach(dev-> idsDevoluciones.add(dev.getIdPago()));
				}

			}else if (requestDevolucionesDTO.getCorresponsal().equals(CorresponsalEnum.AMEX.getNombre())) {
				//devoluciones Amex
				List<PagoConciliadoEPA> devolucionesAMEX= pagoConciliadoEPARepository.findByCorresponsalTipoOperacion(Constants.TIPO_OPERACION_DEVOLUCION);
				if (!devolucionesAMEX.isEmpty()) {
					devolucionesAMEX.forEach(dev-> idsDevoluciones.add(dev.getIdPago()));
				}
			}
		}
		return idsDevoluciones;
	}


	private List<PagoConciliadoEPA> ejecutarConsultaDevolucionesEPA(DevolucionAMEXDTO requestDevolucionAMEXDTO) throws PagoException {
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		List<PagoConciliadoEPA> resultadoFiltros= null;

		try {
			builder.must( QueryBuilders.matchPhraseQuery("corresponsal.estatusDevolucion", Constants.ESTATUS_DEVOLUCION_SOLICITADA ));


			if (requestDevolucionAMEXDTO.getFechaOperacion() != null) {
				Calendar ini =Calendar.getInstance();
				Calendar fin = Calendar.getInstance();

				ini.setTime( requestDevolucionAMEXDTO.getFechaOperacion());
				fin.setTime( requestDevolucionAMEXDTO.getFechaOperacion());

				ini.set(Calendar.HOUR_OF_DAY, 0);
				ini.set(Calendar.MINUTE, 0);
				ini.set(Calendar.SECOND, 0);
				ini.set(Calendar.MILLISECOND, 0);
				fin.set(Calendar.HOUR_OF_DAY, 23);
				fin.set(Calendar.MINUTE, 59);
				fin.set(Calendar.SECOND, 59);
				fin.set(Calendar.MILLISECOND, 59);


				builder.filter(QueryBuilders.rangeQuery("corresponsal.fechaOperacion")
						.gte(ini.getTime().getTime())
						.lte(fin.getTime().getTime()));
			}

			if (requestDevolucionAMEXDTO.getTipoDevolucion() != null){
				builder.must( QueryBuilders.matchQuery("corresponsal.tipoDevolucion", requestDevolucionAMEXDTO.getTipoDevolucion()));
			}
			nativeSearchQueryBuilder.withQuery(builder);
			NativeSearchQuery n = nativeSearchQueryBuilder.build();
			SearchHits<PagoConciliadoEPA> resultado= elasticTemplate.search(n, PagoConciliadoEPA.class);
			resultadoFiltros = new ArrayList<>();
			for(SearchHit<PagoConciliadoEPA> a : resultado) {
				resultadoFiltros.add(a.getContent());
			}
		}catch(Exception ex) {
			LOG.info(PagoException.ERROR_DEVOLUCION_GENERAR_FILTRO.getDescripcion().concat(ex.getMessage()));
			throw PagoException.ERROR_DEVOLUCION_GENERAR_FILTRO;
		}
		return resultadoFiltros;
	}


	private String obtenerSucursal(List<Partida> partidas) {
		String sucursal= null;

		if (partidas != null && !partidas.isEmpty()){
			Optional<Partida> obtenerSuc= partidas.stream().findFirst();
			if (obtenerSuc.isPresent()) {
				int suc = obtenerSuc.get().getNumeroSucursalPartida();
				sucursal= String.valueOf(suc);
			}
		}
		return sucursal;
	}

	private String obtenerEstatusDevolucion(Integer estado) {
		String tipoDevolucion= null;
		if (estado != null){
			//Tipo Devolución
			if (estado == Constants.ESTADO_PAGO_DEVUELTO || 
					estado ==Constants.ESTADO_PAGO_DEVUELTO_LIQUIDADO || 
					estado ==Constants.ESTADO_PAGO_LIQUIDADO_CONCILIADO ||
					estado == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue() ||
					estado == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue() ||
					estado == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue() ) {
				tipoDevolucion =Constants.TIPO_DEVOLUCION_AUTOMATICA; //Mismo dia
			}else if (estado == Constants.ESTADO_PAGO_POR_DEVOLVER || 
					estado == Constants.ESTADO_PAGO_LIQUIDADO || 
					estado == Constants.ESTADO_PAGO_POR_DEVOLVER_LIQUIDADO_CONCILIADO  ||
					estado == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue() ||
					estado == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue() ||
					estado == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue()) {
				tipoDevolucion=  Constants.TIPO_DEVOLUCION_ADMVA; //Cuando se pasa del mismo día y una persona debe intervenir
			}
		}
		return tipoDevolucion;
	}

	private ResponseDevolucionesDTO mapearDevolucion(ResponseDevolucionesDTO devolucionDTO, Devolucion devolucion) throws ParseException {
		String estadoDev = null;
		if (devolucion != null) {

			if (devolucion.getEstadoDevolucion() != null) {
				estadoDev= devolucion.getEstadoDevolucion().equals(Constants.ESTATUS_DEVOLUCION_LIQUIDADA_ID)? Constants.ESTATUS_DEVOLUCION_LIQUIDADA: Constants.ESTATUS_DEVOLUCION_SOLICITADA;
				devolucionDTO.setEstatus(estadoDev);
			}

			String fechaDev= devolucion.getFechaDevolucion();
			if (fechaDev != null)
				devolucionDTO.setFechaDevolucion(new SimpleDateFormat("dd/MM/yyyy").parse(fechaDev));

			devolucionDTO.setFechaCargoBancario(devolucion.getFechaCargoBancario()!= null? devolucion.getFechaCargoBancario(): null);
		}
		return devolucionDTO;
	}

	private ResponseDevolucionesDTO mapearCorresponsalMIT(ResponseDevolucionesDTO devolucionDTO, CorresponsalMIT corresponsal) {
		if (corresponsal != null) {
			devolucionDTO.setTipoTarjeta(corresponsal.getCcType());
			devolucionDTO.setTarjeta(corresponsal.getCcNumber());
	
			String afiliacion = corresponsal.getRspDsMerchant()!= null && corresponsal.getRspDsMerchant().trim().length() > 0?corresponsal.getRspDsMerchant(): null;
			devolucionDTO.setAfiliacion(afiliacion);
			devolucionDTO.setAutorizacion(corresponsal.getRspAuth());
			devolucionDTO.setNumeroOperacion(corresponsal.getRspOperationNumber());
		}
		return devolucionDTO;
	}

	private List<ResponseDevolucionesDTO> mapearDevolucionesAMEX(List<PagoConciliadoEPA> devolucionesList) throws PagoException {
		List<ResponseDevolucionesDTO> respuesta= new ArrayList<>();
		ResponseDevolucionesDTO devolucion= null;
		try {
			for(PagoConciliadoEPA pago: devolucionesList) {
				devolucion= new ResponseDevolucionesDTO();
				devolucion.setAfiliacion(pago.getCorresponsal().getAfiliacion()!= null? pago.getCorresponsal().getAfiliacion():"");
				devolucion.setAutorizacion(pago.getCorresponsal().getAutorizacion()!= null? pago.getCorresponsal().getAutorizacion().toString():null);
				devolucion.setCorresponsal(CorresponsalEnum.AMEX.getNombre());
				devolucion.setTipoDevolucion(pago.getCorresponsal().getTipoDevolucion());
				devolucion.setIdPago(pago.getIdPago());
				devolucion.setEntidad(pago.getCorresponsal().getBancoEmisor());
				devolucion.setFechaCargoBancario(pago.getCorresponsal().getFechaLiquidacion());
				devolucion.setFechaDevolucion(pago.getCorresponsal().getFechaDevolucion());
				devolucion.setFechaTransaccion(pago.getCorresponsal().getFechaOperacion());
				devolucion.setImporteDevolucion(pago.getCorresponsal().getImporteDevolucion());
				devolucion.setMontoTransaccion(pago.getCorresponsal().getImporteNeto()!= null?pago.getCorresponsal().getImporteNeto().toString(): null);
				devolucion.setNumeroOperacion(pago.getCorresponsal().getNumeroOperacion());
				devolucion.setSucursal(pago.getCorresponsal().getSucursal());
				devolucion.setTarjeta(pago.getCorresponsal().getTarjeta());
				devolucion.setTipoTarjeta(pago.getCorresponsal().getTipoTarjeta());
				devolucion.setEstatus(pago.getCorresponsal().getEstatusDevolucion());
				respuesta.add(devolucion);
			}
		}catch(Exception ex) {
			LOG.info("Error al mapearDevolucionesAMEX {0}", ex);
			throw PagoException.ERROR_DEVOLUCION_ACT_MAPEO;
		}
		return respuesta;
	}

	private List<ResponseDevolucionesDTO> mapearDevolucionesMIT(List<PagoMIT> devoluciones) throws PagoException{
		List<ResponseDevolucionesDTO> respuesta= new ArrayList<>();
		try {
			if (devoluciones != null && !devoluciones.isEmpty()) {
				for(PagoMIT pago: devoluciones) {
					ResponseDevolucionesDTO devolucion= new ResponseDevolucionesDTO();
					devolucion.setAutorizacion(pago.getCorresponsal().getRspAuth());
					devolucion.setCorresponsal(validarFiltroCorresponsal(pago.getPlataformaOrigen()));
					devolucion.setTipoDevolucion(obtenerTipoDevolucion(pago.getEstado()));
					devolucion.setIdPago(pago.getIdPago());
					devolucion.setFechaTransaccion(pago.getCorresponsal().getRspDate());
					devolucion.setImporteDevolucion(pago.getCorresponsal().getTxAmount() != null? BigDecimal.valueOf(Double.valueOf(pago.getCorresponsal().getTxAmount())): null);
					devolucion.setMontoTransaccion(pago.getMontoTotal().toString());
					devolucion.setNumeroOperacion(pago.getCorresponsal().getRspOperationNumber());
					devolucion.setSucursal(obtenerSucursaldPartida(pago.getPartidas()));
					devolucion.setTarjeta(pago.getCorresponsal().getCcNumber());
					devolucion.setTipoTarjeta(pago.getCorresponsal().getCcType());
					devolucion.setFechaCargoBancario(formatearFechasDevoluciones(pago.getDevolucion(), false));
					devolucion.setFechaDevolucion(formatearFechasDevoluciones(pago.getDevolucion(), true));
					devolucion.setEstatus(obtenerEstatus(pago.getDevolucion().getEstadoDevolucion()));
					devolucion.setEstado(pago.getEstado());
					respuesta.add(devolucion);
				}
			}
		}catch(Exception ex) {
			throw PagoException.ERROR_DEVOLUCION_ACT_MAPEO;
		}
		return respuesta;
	}

	private Date formatearFechasDevoluciones(Devolucion devolucion, boolean dev) throws ParseException {
		Date fechaDev= null;

		if (devolucion != null) {
			if (dev) {
				fechaDev= new SimpleDateFormat("dd/MM/yyyy").parse(devolucion.getFechaDevolucion());

			}else{
				fechaDev= devolucion.getFechaCargoBancario();
			}
		}
		return fechaDev;

	}

	private String obtenerEstatus(Integer estado) {
		String estatus = null;
		if (estado.equals(Constants.ESTATUS_DEVOLUCION_LIQUIDADA_ID)){
			estatus= Constants.ESTATUS_DEVOLUCION_LIQUIDADA;
		}else if (estado.equals(Constants.ESTATUS_DEVOLUCION_SOLICITADA_ID)){
			estatus = Constants.ESTATUS_DEVOLUCION_SOLICITADA;
		}
		return estatus;
	}

	private String obtenerSucursaldPartida(List<Partida> partidas) {
		String sucursal = null;
		if (!partidas.isEmpty()) {
			sucursal = String.valueOf(partidas.get(0).getNumeroSucursalPartida());
		}
		return sucursal;
	}

	private String obtenerTipoDevolucion(Integer estadoPagoMIT) {
		String tipoDevolucion= null;
		if (estadoPagoMIT.equals(EstadoPagosEnum.PAGO_DEVUELTO_CORE.getEstadoPagos()) ||
				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos()) ||
				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos()) ||

				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos()) ||
				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos()) ||
				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos())){
					
			tipoDevolucion=  Constants.TIPO_DEVOLUCION_AUTOMATICA;
		}else if (estadoPagoMIT.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_CORE.getEstadoPagos()) ||
				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos()) ||
				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos()) ||
				
				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos()) ||
				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos()) ||
				estadoPagoMIT.equals(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos()) ){
			tipoDevolucion=  Constants.TIPO_DEVOLUCION_ADMVA;
		}
		return tipoDevolucion;
	}

	private int[] validarFiltro(String filtro) {
		int[] arreglo= null;

		if (filtro != null && filtro.trim().length() > 0) {
			arreglo= generaCadenas(filtro);
		}
		return arreglo;
	}

	private String validarFiltroCorresponsal(String corresponsal) throws PagoException {
		String enumCorresponsal= null;
		if (corresponsal != null && corresponsal.trim().length() > 0) {
			if (corresponsal.trim().equals(CorresponsalEnum.SANTANDER.getNombre())){
				enumCorresponsal= CorresponsalEnum.SANTANDER.toString();
			}else if (corresponsal.trim().equals(CorresponsalEnum.AMEX.getNombre())){
				enumCorresponsal= CorresponsalEnum.AMEX.toString();
			}else {
				LOG.info(PagoException.ERROR_DEVOLUCION_CORRESPONSAL.getDescripcion());
				throw PagoException.ERROR_DEVOLUCION_CORRESPONSAL;
			}
		}
		return enumCorresponsal;
	}

	private Integer validarFiltroEstatus(String estatus) {
		Integer estadoValor= null;

		if (estatus != null && estatus.trim().length()> 0) {
			if (estatus.equals( Constants.ESTATUS_DEVOLUCION_SOLICITADA)) {
				estadoValor = Constants.ESTATUS_DEVOLUCION_SOLICITADA_ID;
			}else if (estatus.equals( Constants.ESTATUS_DEVOLUCION_LIQUIDADA)) {
				estadoValor = Constants.ESTATUS_DEVOLUCION_LIQUIDADA_ID;
			}
		}
		return estadoValor;

	}

	private String validarFiltroTipoDev(String tipoDev) throws PagoException {
		String tipoDevolucion= null;

		if (tipoDev != null && tipoDev.trim().length() > 0) {
			if (tipoDev.toUpperCase().trim().equals(Constants.TIPO_DEVOLUCION_AUTOMATICA)) {
				tipoDevolucion= Constants.TIPO_DEVOLUCION_AUTOMATICA;
			}else if (tipoDev.toUpperCase().trim().equals(Constants.TIPO_DEVOLUCION_ADMVA)){
				tipoDevolucion= Constants.TIPO_DEVOLUCION_ADMVA;
			}else {
				LOG.info(PagoException.ERROR_DEVOLUCION_TIPO.getDescripcion());
				throw PagoException.ERROR_DEVOLUCION_TIPO;
			}
		}
		return tipoDevolucion;
	}

	private BoolQueryBuilder aplicarFiltros(RequestDevolucionesDTO requestDevolucionesDTO, List<String> idsTermsFiltro) throws PagoException{
		BoolQueryBuilder builder= null;
		String corresponsalBuscar= null;
		int[] arreglo= null;

		//Construir filtros dinámicos
		try {
			//Armado Query
			builder = QueryBuilders.boolQuery();
			builder.must(QueryBuilders.existsQuery(Constants.FILTRO_PLATAFORMA_ORIGEN)); //filtro default

			//Corresponsal- MIT-SANTANDER/ MIT-AMEX
			corresponsalBuscar= validarFiltroCorresponsal(requestDevolucionesDTO.getCorresponsal());
			if (corresponsalBuscar != null){
				builder.must( QueryBuilders.matchBoolPrefixQuery(Constants.FILTRO_PLATAFORMA_ORIGEN, corresponsalBuscar));
			}

			//Tipo Devolución - AUTOMATICA/ADMINISTRATIVA
			String tipoDev= validarFiltroTipoDev(requestDevolucionesDTO.getTipoDevolucion());

			if (tipoDev == null) {
				arreglo= new int[] {
						Constants.ESTADO_PAGO_DEVUELTO,Constants.ESTADO_PAGO_POR_DEVOLVER,
						Constants.ESTADO_PAGO_LIQUIDADO, Constants.ESTADO_PAGO_DEVUELTO_LIQUIDADO,
						Constants.ESTADO_PAGO_LIQUIDADO_CONCILIADO , Constants.ESTADO_PAGO_POR_DEVOLVER_LIQUIDADO_CONCILIADO,
						EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue(),
						EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue(),
						
						EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue(),
						EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue(),
						EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue(),
						EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue()
						};
				builder.must( QueryBuilders.termsQuery(Constants.FILTRO_ESTADO, arreglo));

			}else if (tipoDev.equals(Constants.TIPO_DEVOLUCION_AUTOMATICA)){
				arreglo= new int[] {
						EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue(),
						Constants.ESTADO_PAGO_DEVUELTO, Constants.ESTADO_PAGO_DEVUELTO_LIQUIDADO, 
						Constants.ESTADO_PAGO_LIQUIDADO_CONCILIADO,
						EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue(),
						EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue()
						};
				builder.must( QueryBuilders.termsQuery(Constants.FILTRO_ESTADO, arreglo));

			}else if (tipoDev.equals(Constants.TIPO_DEVOLUCION_ADMVA)){
				arreglo= new int[] {
						EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue(),
						Constants.ESTADO_PAGO_POR_DEVOLVER, Constants.ESTADO_PAGO_LIQUIDADO, 
						Constants.ESTADO_PAGO_POR_DEVOLVER_LIQUIDADO_CONCILIADO,
						EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue(),
						EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue()
						};
				builder.must( QueryBuilders.termsQuery(Constants.FILTRO_ESTADO, arreglo));
			}

			//ESTATUS- SOLICITADA
			Integer estado= validarFiltroEstatus(requestDevolucionesDTO.getEstatus());
			if (estado != null) {
				builder.must(QueryBuilders.existsQuery("devolucion")).filter(QueryBuilders.matchQuery("devolucion.estadoDevolucion", estado));
			}

			//Sucursal- ejemplo (135,134)
			arreglo= validarFiltro(requestDevolucionesDTO.getSucursal());
			if (arreglo != null) {
				builder.must(QueryBuilders.nestedQuery("partidas", QueryBuilders.termsQuery("partidas.numeroSucursalPartida", arreglo), ScoreMode.None));
			}

			//Afiliación- ejemplo (8679485,8679486)
			arreglo= validarFiltro(requestDevolucionesDTO.getAfiliacion());
			if (arreglo != null) {
				builder.must( QueryBuilders.termsQuery("corresponsal.getRspDsMerchant", arreglo));
			}

			//Rango de fechas
			if (requestDevolucionesDTO.getFechaDesde() != null && requestDevolucionesDTO.getFechaHasta() != null) {

				Calendar ini = Calendar.getInstance();
				Calendar fin = Calendar.getInstance();
				ini.setTime( requestDevolucionesDTO.getFechaDesde());
				fin.setTime( requestDevolucionesDTO.getFechaHasta());

				ini.set(Calendar.HOUR_OF_DAY, 0);
				ini.set(Calendar.MINUTE, 0);
				ini.set(Calendar.SECOND, 0);
				ini.set(Calendar.MILLISECOND, 0);

				fin.set(Calendar.HOUR_OF_DAY, 23);
				fin.set(Calendar.MINUTE, 59);
				fin.set(Calendar.SECOND, 59);
				fin.set(Calendar.MILLISECOND, 59);


				builder.filter(QueryBuilders.rangeQuery(Constants.FILTRO_FECHA_OPERACION)
						.gte(ini.getTimeInMillis())
						.lte(fin.getTimeInMillis()));
			}
			
			if (!idsTermsFiltro.isEmpty()) {
				builder.must( QueryBuilders.termsQuery("idPago", idsTermsFiltro));
			}
			
		}catch(PagoException e) {
			throw e;
		}
		catch(Exception ex) {
			LOG.info(PagoException.ERROR_DEVOLUCION_GENERAR_FILTRO.getDescripcion().concat(ex.getMessage()));
			throw PagoException.ERROR_DEVOLUCION_GENERAR_FILTRO;
		}
		return builder;
	}

	private int[] generaCadenas(String cadena) {
		int[] arreglo= null;
		String[] splitCadena = cadena.split(",");

		if (splitCadena.length > 0) {
			int longAfil= splitCadena.length;
			arreglo = new int[longAfil];
			int i= 0;
			for(String a: splitCadena) {
				arreglo[i++]= Integer.valueOf(a.trim());
			}
		}
		return arreglo;
	}

	private List<ResponseDevolucionesDTO>  procesarPagos(List<PagoMIT> pagosList) throws PagoException{
		List<ResponseDevolucionesDTO> responseDevolucionesList = null;
		ResponseDevolucionesDTO devolucionDTO= null;

		try {
			responseDevolucionesList= new ArrayList<>();
			if (!pagosList.isEmpty()){

				for (PagoMIT pago: pagosList) {
					devolucionDTO= new ResponseDevolucionesDTO();

					devolucionDTO.setSucursal(obtenerSucursal(pago.getPartidas()));
					devolucionDTO.setFechaTransaccion(pago.getFechaOperacion());
					devolucionDTO =mapearCorresponsalMIT(devolucionDTO, pago.getCorresponsal());
					//Al ser devolución, debe traer nodo- Devolucion
					devolucionDTO= mapearDevolucion(devolucionDTO, pago.getDevolucion());
					
					//mapeo devoluciones no Reconocidas
					if (pago.getEstado().equals(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos())) {
						devolucionDTO.setFechaCargoBancario(null);
					}
					devolucionDTO.setEstado(pago.getEstado());
					devolucionDTO.setImporteDevolucion(pago.getMontoTotal());
					devolucionDTO.setMontoTransaccion(pago.getCorresponsal().getTxAmount() != null? pago.getCorresponsal().getTxAmount():"0");
					devolucionDTO.setTipoDevolucion (obtenerEstatusDevolucion(pago.getEstado()));
					devolucionDTO.setIdPago(pago.getIdPago() != null ? pago.getIdPago(): null);
					devolucionDTO.setCorresponsal(pago.getPlataformaOrigen());
					responseDevolucionesList.add(devolucionDTO);
				}
			}
		}catch(Exception ex) {
			LOG.info(PagoException.ERROR_DEVOLUCION_MAPPEO.getDescripcion().concat(ex.getMessage()));
			throw PagoException.ERROR_DEVOLUCION_MAPPEO;
		}
		return responseDevolucionesList;
	}


	@Override
	public boolean actualizarLiquidadasConciliadas(List<ResponseDevolucionesDTO> devoluciones) throws PagoException {
		List<String> ids = new ArrayList<>();
		boolean exito = true;
		try {
			if (!devoluciones.isEmpty()) {
				devoluciones.stream().forEach(dev-> ids.add(dev.getIdPago()));
				actualizarEstatusMoPagos(devoluciones);
				actualizarEstatusMoPagosMIT(ids);
				actualizarEstatusConciliados(ids, devoluciones.get(0).getCorresponsal());
			}
		}catch(Exception ex) {
			LOG.info(PagoException.ERROR_DEVOLUCION_PROCESO.getDescripcion().concat(ex.getMessage()));
			exito= false;
		}
		return exito;
	}
	private void actualizarEstatusConciliadosAMEX(List<String> ids) {
		List<PagoConciliadoEPA>  devsAmex= pagoConciliadoEPARepository.findByIdPagoIn(ids);
		for(PagoConciliadoEPA devA: devsAmex) {
			devA.getCorresponsal().setEstatusDevolucion(Constants.ESTATUS_DEVOLUCION_LIQUIDADA_CONCILIADA);
			
			//Actualizacion estatus en conciliados
			// 17-Admvas
			if (devA.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos().intValue()) {
				devA.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
				
			//19- Automticas
			}else if (devA.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos().intValue()){
				devA.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
			
			
			//24- Administrativa- No reconocida Liquidada	
			}else if(devA.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue()) {
				devA.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos());
				//--24 a 27
				
			//-23 Automatica- No Reconocida Liquidada
			}else if(devA.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue()) {
				devA.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos());
				//23-26
			}				
			pagoConciliadoEPARepository.save(devA);
		}		
	}
	
	private void actualizarEstatusConciliados(List<String> ids, String corresponsal) {
		if (corresponsal.equals(CorresponsalEnum.SANTANDER.toString())) {
			List<PagoConciliadoMIT> devsSant= pagoConciliadoMITRepository.findByIdPagoIn(ids);
			for(PagoConciliadoMIT dev: devsSant) {
				dev.getCorresponsal().setEstatusDevolucion(Constants.ESTATUS_DEVOLUCION_LIQUIDADA_CONCILIADA);

				//Actualizacion estatus en conciliados
				// 17-Admvas
				if (dev.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos().intValue()) {
					dev.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
				
				// 19- Automatica
				}else if (dev.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos().intValue()){
					dev.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
				
				
				//24- Administrativa- No reconocida Liquidada	
				}else if(dev.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue()) {
					dev.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos());
					//--24 a 27
					
				//-23 Automatica- No Reconocida Liquidada
				}else if(dev.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue()) {
					dev.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos());
					//23-26
				}
				pagoConciliadoMITRepository.save(dev);
			}
		}else if (corresponsal.equals(CorresponsalEnum.AMEX.toString())) {
			actualizarEstatusConciliadosAMEX(ids);
		}
	}

	private void actualizarEstatusMoPagosMIT(List<String> ids) {
		if (!ids.isEmpty()) {
			//Actualizar estatus mo_pagos_mit
			List<PagoMIT> pagosMIT= pagoMITRepository.findByIdPagoIn(ids);
			if (!pagosMIT.isEmpty()) {
				for(PagoMIT pMIT: pagosMIT) {
					
					//17- Administrativa 21
					if (pMIT.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos().intValue()){ //Admvas
						pMIT.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
						LOG.info("actualizarEstatus- Actualización indice mo_pagos-Admva-PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION(21) idpago {}", pMIT.getIdPago());
					}
					//19-Automatica 20
					else if (pMIT.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos().intValue()){ //Automaticas
						pMIT.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
						LOG.info("actualizarEstatus- Actualización indice mo_pagos-Automatica-PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION-(20) idpago {}", pMIT.getIdPago());
					
					
					//24- Administrativa- No reconocida Liquidada	
					}else if(pMIT.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue()) {
						pMIT.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos());
						//--24 a 27
						LOG.info("actualizarEstatus- Actualización indice mo_pagos-Administrativa No reconocidas-24-PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO-(27) idpago {}", pMIT.getId());
						
					//-23 Automatica- No Reconocida Liquidada
					}else if(pMIT.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue()) {
						pMIT.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos());
						//23-26
						LOG.info("actualizarEstatus- Actualización indice mo_pagos-Automatica No reconocidas-23-PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO-(26) idpago {}", pMIT.getId());
					}
					
					pagoMITRepository.save(pMIT);
				}
			}
		}


	}

	private void actualizarEstatusMoPagos(List<ResponseDevolucionesDTO> devoluciones) {
		if (!devoluciones.isEmpty()) {
			for(ResponseDevolucionesDTO dev: devoluciones) {

				//Actualizar estatus mo_pagos
				Pago pagoP = pagoRepository.findById(dev.getIdPago()).orElse(null);
				if (pagoP != null) {
					
					//17- Admvas Liquidadas
					if (pagoP.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO.getEstadoPagos().intValue()){
						pagoP.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
						pagoP.setEstadoMIT(EstadoPagosEnum.PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION.toString());
						LOG.info("actualizarEstatus- Actualización indice mo_pagos-Admva- 17- PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION(21) idpago {}", pagoP.getId());
					}
					//19 -Automaticas Liquidadas
					else if (pagoP.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO.getEstadoPagos().intValue()){
						pagoP.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.getEstadoPagos());
						pagoP.setEstadoMIT(EstadoPagosEnum.PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION.toString());
						LOG.info("actualizarEstatus- Actualización indice mo_pagos-Automatica-19-PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION-(20) idpago {}", pagoP.getId());
					}
					//24- Administrativa- No reconocida Liquidada
					else if(pagoP.getEstado() == EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue()) {
						pagoP.setEstado(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos());
						pagoP.setEstadoMIT(EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.toString());
						//--24 a 27
						LOG.info("actualizarEstatus- Actualización indice mo_pagos-Administrativa No reconocidas-24-PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO-(27) idpago {}", pagoP.getId());
					}
					
					//-23 Automatica- No Reconocida Liquidada					
					else if(pagoP.getEstado() == EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue()) {
						pagoP.setEstado(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos());
						pagoP.setEstadoMIT(EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.toString());
						//23-26
						LOG.info("actualizarEstatus- Actualización indice mo_pagos-Automatica No reconocidas-23-PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO-(26) idpago {}", pagoP.getId());
					}
					pagoRepository.save(pagoP);
				}
			}
		}
	}

	/**
	 * Consulta el total de las devoluciones liquidadas
	 * @param devolucionesDTO
	 * @return Map<String, Object> d_total, d_fechaInicio, d_fechaFin
	 */
	@Override
	public Map<String, Object> consultaTotalDevolucionesLiquidadas(RequestTotalDevolucionesDTO devolucionesDTO) throws PagoException {
		Map<String, Object> resMIT = new HashMap<>();
		BoolQueryBuilder query = buildFiltros(devolucionesDTO);
		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		nativeSearchQueryBuilder.withQuery(query);
		nativeSearchQueryBuilder.addAggregation(AggregationBuilders.min("min_value").field(Constants.FILTRO_FECHA_OPERACION));
		nativeSearchQueryBuilder.addAggregation(AggregationBuilders.max("max_value").field(Constants.FILTRO_FECHA_OPERACION));
		NativeSearchQuery nQuery = nativeSearchQueryBuilder.build();
		SearchHits<PagoMIT> result = elasticTemplate.search(nQuery, PagoMIT.class);

		if (result != null && !result.isEmpty()) {
			resMIT.put("d_total",result.getTotalHits());
			Aggregation aggregationMin = result.getAggregations().get("min_value");
			if (aggregationMin instanceof Min) {
				Min minValueAgg = (Min) aggregationMin;
				resMIT.put("d_fechaInicio", Long.valueOf((long) minValueAgg.getValue()));
			}

			Aggregation aggregationMax = result.getAggregations().get("max_value");
			if (aggregationMax instanceof Max) {
				Max maxValueAgg = (Max) aggregationMax;
				resMIT.put("d_fechaFin",Long.valueOf((long) maxValueAgg.getValue()));
			}
		}
		else{
			resMIT.put("d_total",0);
			resMIT.put("d_fechaInicio",0L);
			resMIT.put("d_fechaFin",0L);
		}
		return resMIT;
	}

	/**
	 * Método que genera los filtros para el total de devoluciones
	 * @param devolucionesDTO
	 * @return query
	 */
	private BoolQueryBuilder buildFiltros(RequestTotalDevolucionesDTO devolucionesDTO) {
		BoolQueryBuilder builder = QueryBuilders.boolQuery();

		builder.must( QueryBuilders.matchPhraseQuery("devolucion.estadoDevolucion",3));

		List<String> idsPagosFiltro;
		if(devolucionesDTO.getCorresponsal().trim().equals(CorresponsalEnum.AMEX.getNombre()) || devolucionesDTO.getCorresponsal().trim().equals(CorresponsalEnum.SANTANDER.getNombre())){
			idsPagosFiltro = getIdsPagosByCorresponsal(devolucionesDTO.getCorresponsal());
		}else{
			idsPagosFiltro = getIdsPagosByCorresponsal(CorresponsalEnum.SANTANDER.getNombre());
			idsPagosFiltro.addAll(getIdsPagosByCorresponsal(CorresponsalEnum.AMEX.getNombre()));
		}
		if (!idsPagosFiltro.isEmpty()) {
			builder.must( QueryBuilders.termsQuery("idPago", idsPagosFiltro));
		}

		if (StringUtils.hasText(devolucionesDTO.getCorresponsal())) {
			builder.must( QueryBuilders.matchBoolPrefixQuery("plataformaOrigen", devolucionesDTO.getCorresponsal()));
		}

		int[] estados = new int[] {Constants.ESTADO_PAGO_DEVUELTO,Constants.ESTADO_PAGO_POR_DEVOLVER,
				Constants.ESTADO_PAGO_LIQUIDADO, Constants.ESTADO_PAGO_DEVUELTO_LIQUIDADO,
				Constants.ESTADO_PAGO_LIQUIDADO_CONCILIADO , Constants.ESTADO_PAGO_POR_DEVOLVER_LIQUIDADO_CONCILIADO,
				
				//estados devoluciones No encontradas 18, 22-- y estatus liquidado y procesado
				EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue(),
				EstadoPagosEnum.PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue(),
				EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue(),
				EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO.getEstadoPagos().intValue(),
				EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO.getEstadoPagos().intValue(),
				EstadoPagosEnum.PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION.getEstadoPagos().intValue()
		};
		builder.must( QueryBuilders.termsQuery(Constants.FILTRO_ESTADO, estados));

		if(devolucionesDTO.getFechaHasta() != null && devolucionesDTO.getFechaDesde() != null){
			Calendar ini = Calendar.getInstance();
			Calendar fin = Calendar.getInstance();
			ini.setTime( devolucionesDTO.getFechaDesde());
			fin.setTime( devolucionesDTO.getFechaHasta());

			ini.set(Calendar.HOUR_OF_DAY, 0);
			ini.set(Calendar.MINUTE, 0);
			ini.set(Calendar.SECOND, 0);
			ini.set(Calendar.MILLISECOND, 0);

			fin.set(Calendar.HOUR_OF_DAY, 23);
			fin.set(Calendar.MINUTE, 59);
			fin.set(Calendar.SECOND, 59);
			fin.set(Calendar.MILLISECOND, 59);

			builder.filter(QueryBuilders.rangeQuery(Constants.FILTRO_FECHA_OPERACION)
					.gte(ini.getTimeInMillis())
					.lte(fin.getTimeInMillis()));
		}
		return builder;
	}

	/**
	 * Medoto que nos ayuda a obtener los ids de pago de su indice dependiendo del corresponsal
	 * @param corresponsal
	 * @return
	 */
	private List<String> getIdsPagosByCorresponsal(String corresponsal){
		RequestDevolucionesDTO requestDevolucionesDTO = new RequestDevolucionesDTO();
		requestDevolucionesDTO.setCorresponsal(corresponsal);
		return consultarEnConciliados(requestDevolucionesDTO);
	}

}
