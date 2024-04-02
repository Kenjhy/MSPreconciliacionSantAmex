package mx.com.nmp.mspreconciliacion.controllers;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ResponsePreConciliacionDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import mx.com.nmp.mspreconciliacion.services.PreConciliacionAMEXService;
import mx.com.nmp.mspreconciliacion.services.PreConciliacionSantanderService;
import mx.com.nmp.mspreconciliacion.util.Response;


/**
 * @name PreConciliacionController
 * @description Clase que expone el servicio para las operaciones relacionadas con la Preconciliacion de los diferentes corresponsales
 * @author QuarkSoft
 *
 */

@RestController
@RequestMapping(value= "/mspreconciliacion")
public class PreConciliacionController {
	
	/**
	 * Bean de la fabrica de instancias
	 */
	@Autowired
	private BeanFactory beanFactory;
	
	
	@Autowired
	private PreConciliacionSantanderService preConciliacionSantanderService;
	
	@Autowired
	private PreConciliacionAMEXService preConciliacionAMEXService;
	
	
	private final Logger logger = LoggerFactory.getLogger(PreConciliacionController.class);
	
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/movimientos/santander", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response consultarTransaccionesSantander(@RequestBody @Valid @NotNull PreConciliacionDTO preConciliacionDTO) throws PagoException, PreconciliacionExcetion {
		String estatus= HttpStatus.OK.toString();
		String request= new Gson().toJson(preConciliacionDTO);
		String msj= Constants.MSG_EXITOSO;
		logger.info(" Ejecución Ctrl/consultarTransaccionesSantander");
		logger.info("Request entrante:");
		logger.info( request);
		ResponsePreConciliacionDTO result = preConciliacionSantanderService.consultarTransacciones(preConciliacionDTO);
		if (!result.isExito()) {
			estatus= HttpStatus.NOT_FOUND.toString();
			msj= result.getMensaje();
		}
		logger.info("Finaliza Ejecución Ctrl/consultarTransaccionesSantander");
		return beanFactory.getBean(Response.class, estatus, msj, result);
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/movimientos/amex", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response consultarTransaccionesAMEX(@RequestBody @Valid @NotNull PreConciliacionDTO preConciliacionDTO) throws PagoException, PreconciliacionExcetion {
		String estatus= HttpStatus.OK.toString();
		String request= new Gson().toJson(preConciliacionDTO);
		String msj= Constants.MSG_EXITOSO;
		logger.info(" Ejecución Ctrl/consultarTransaccionesAMEX");
		logger.info("Request entrante:");
		logger.info(request);
		ResponsePreConciliacionDTO result = preConciliacionAMEXService.consultarTransacciones(preConciliacionDTO);
		if (!result.isExito()) {
			estatus= HttpStatus.NOT_FOUND.toString();
			msj= result.getMensaje();
		}
		logger.info("Finaliza Ejecución Ctrl/consultarTransaccionesAMEX");
		return beanFactory.getBean(Response.class, estatus, msj, result);
	}
	

}
