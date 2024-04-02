package mx.com.nmp.mspreconciliacion.controllers;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.exceptions.SistemaException;
import mx.com.nmp.mspreconciliacion.model.dto.ArchivoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesPaginadoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RequestComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RequestTotalDevolucionesDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.services.ComsionBancariaService;
import mx.com.nmp.mspreconciliacion.services.impl.ComisionBancariaAMEXServiceImpl;
import mx.com.nmp.mspreconciliacion.services.impl.ComisionBancariaSantanderServiceImpl;
import mx.com.nmp.mspreconciliacion.services.impl.DevolucionesImpl;
import mx.com.nmp.mspreconciliacion.util.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.util.Date;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @name ComisionesBancariasController
 * @description Clase que expone el servicio para las operaciones relacionadas con las comisiones bancarias para SANTANDER/AMEX
 * @author QuarkSoft
 */
@RestController
@RequestMapping(value= "/mspreconciliacion")
public class ComisionesBancariasController {


	@Autowired
	private BeanFactory beanFactory;


	@Autowired
	private ComisionBancariaAMEXServiceImpl comisionBancariaAMEXServiceImpl;

	/**
	 * Referencia al servicio {@link ComsionBancariaService} para el corresponal SANTANDER
	 */
	@Autowired
	private ComisionBancariaSantanderServiceImpl comisionBancariaSantanderServiceImpl;

	@Autowired
	private DevolucionesImpl devolucionesService;

	private final Logger logger = LoggerFactory.getLogger(ComisionesBancariasController.class);

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/comisiones", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response consultarComisiones(@RequestBody @Valid @NotNull RequestComisionesDTO comisionesDTO) throws SistemaException, PagoException {

		ComisionesPaginadoDTO resultado =null;
		if (StringUtils.hasText(comisionesDTO.getCorresponsal())) {

			if (comisionesDTO.getCorresponsal().trim().equals(CorresponsalEnum.AMEX.getNombre())) {
				resultado = comisionBancariaAMEXServiceImpl.consultarComisiones(comisionesDTO);

			} else if (comisionesDTO.getCorresponsal().trim().equalsIgnoreCase(CorresponsalEnum.SANTANDER.getNombre())) {
				resultado = comisionBancariaSantanderServiceImpl.consultarComisiones(comisionesDTO);
			} else {
				throw PagoException.CORRESPONSAL_INCORRECTO;
			}
		}
		return beanFactory.getBean(Response.class, HttpStatus.OK.toString(), Constants.MSG_EXITOSO, resultado);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/comisiones/totalDevoluciones", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response consultarTotalDevoluciones(@RequestBody RequestTotalDevolucionesDTO totalDevolucionesDTO) throws PagoException {
		Map<String, Object> resMIT = null;
		if (totalDevolucionesDTO.getCorresponsal().isEmpty() || totalDevolucionesDTO.getCorresponsal().trim().equals(CorresponsalEnum.AMEX.getNombre())
				|| totalDevolucionesDTO.getCorresponsal().trim().equals(CorresponsalEnum.SANTANDER.getNombre()) ) {
			resMIT = devolucionesService.consultaTotalDevolucionesLiquidadas(totalDevolucionesDTO);
			return beanFactory.getBean(Response.class, HttpStatus.OK.toString(), Constants.MSG_EXITOSO, resMIT);
		}else{
			throw PagoException.CORRESPONSAL_INCORRECTO;
		}
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/comisiones/reporte", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response generarReporteComsiones(@RequestBody @Valid @NotNull RequestComisionesDTO comisionesDTO) throws SistemaException, PagoException {
		String cadena= null;
		ArchivoDTO respuesta = null;
		if (StringUtils.hasText(comisionesDTO.getCorresponsal())) {
			respuesta= new ArchivoDTO();
			if (comisionesDTO.getCorresponsal().trim().equals(CorresponsalEnum.AMEX.getNombre())) {
				cadena = comisionBancariaAMEXServiceImpl.reporteComisiones(comisionesDTO);
			} else if (comisionesDTO.getCorresponsal().trim().equalsIgnoreCase(CorresponsalEnum.SANTANDER.getNombre())) {
				cadena = comisionBancariaSantanderServiceImpl.reporteComisiones(comisionesDTO);
			} else {
				throw PagoException.CORRESPONSAL_INCORRECTO;
			}
			respuesta.setArchivoBase64(cadena);
		}
		return beanFactory.getBean(Response.class, HttpStatus.OK.toString(), Constants.MSG_EXITOSO, respuesta);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/comisiones/santander/{fechaOperacion}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response consultarCatalogosSantander(@PathVariable(value = "fechaOperacion", required = true) @DateTimeFormat(pattern = "ddMMyyyy")  Date fechaOperacion) {

		logger.info(" Ejecución Ctrl/consultarCatalogoSantander");
		return beanFactory.getBean(Response.class, HttpStatus.OK.toString(), Constants.MSG_EXITOSO, comisionBancariaSantanderServiceImpl.consultarCatalogoComisiones(fechaOperacion));
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/comisiones/amex/{fechaOperacion}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response consultarCatalogosAMEX(@PathVariable(value = "fechaOperacion", required = true) @DateTimeFormat(pattern = "ddMMyyyy")  Date fechaOperacion) {

		logger.info(" Ejecución Ctrl/consultarCatalogoAMEX");
		return beanFactory.getBean(Response.class, HttpStatus.OK.toString(), Constants.MSG_EXITOSO, comisionBancariaAMEXServiceImpl.consultarCatalogoComisiones(fechaOperacion));
	}

}
