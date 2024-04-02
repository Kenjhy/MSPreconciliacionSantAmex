package mx.com.nmp.mspreconciliacion.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/mspreconciliacion")
public class RevisionController {
	
	private final Logger logger = LoggerFactory.getLogger(RevisionController.class);

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/revisionhealth")
	public void revisionhealth() {
		
		logger.info("Ejecución Ctrl/revisionhealth exitosa");
		ResponseEntity.status(HttpStatus.OK);
	}	
}
