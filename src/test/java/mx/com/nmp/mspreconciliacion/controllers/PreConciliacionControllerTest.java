package mx.com.nmp.mspreconciliacion.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Date;


import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ResponsePreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.services.impl.PreConciliacionAMEXServiceImpl;
import mx.com.nmp.mspreconciliacion.services.impl.PreConciliacionSantanderServiceImpl;
import mx.com.nmp.mspreconciliacion.util.Response;

@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PreConciliacionControllerTest {

	private final String ENDPOINT_BASE = "/mspreconciliacion/";
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	    

	@MockBean
	private PreConciliacionSantanderServiceImpl preConciliacionSantanderService;
	    
	@MockBean
	private PreConciliacionAMEXServiceImpl preConciliacionAMEXService;
	    
	@BeforeEach
	void setUp(){
		reset(preConciliacionSantanderService, preConciliacionAMEXService); 
	}
	
	@Test
    void consultarTransaccionesSantanderTest() throws Exception {
		
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(163L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(false);
		
		ResponsePreConciliacionDTO respuesta= new ResponsePreConciliacionDTO();
		respuesta.setExito(true);
		
		when(preConciliacionSantanderService.consultarTransacciones(any(PreConciliacionDTO.class))).thenReturn(respuesta);
		
	       this.mockMvc.perform(post(ENDPOINT_BASE +"movimientos/santander")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(datosPrecon)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(result -> {
            Response response = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(Constants.MSG_EXITOSO, response.getMessage());
            Boolean devResult = objectMapper.convertValue(response.getObject(), Boolean.class);
            Assertions.assertTrue(devResult);
        });
	}
	
	@Test
    void consultarTransaccionesAMEXTest() throws Exception {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(163L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(false);
		
		ResponsePreConciliacionDTO respuesta= new ResponsePreConciliacionDTO();
		respuesta.setExito(true);
		
		when(preConciliacionAMEXService.consultarTransacciones(any(PreConciliacionDTO.class))).thenReturn(respuesta);
		
	       this.mockMvc.perform(post(ENDPOINT_BASE +"movimientos/amex")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(datosPrecon)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(result -> {
            Response response = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(Constants.MSG_EXITOSO, response.getMessage());
            Boolean devResult = objectMapper.convertValue(response.getObject(), Boolean.class);
            Assertions.assertTrue(devResult);
        });		
	}
	
	@Test
    void revisionhealthTest() throws Exception {
		
		 ResultActions response = this.mockMvc.perform(get(ENDPOINT_BASE +"revisionhealth"));
				 response.andDo(print())
				 .andExpect(status().isOk());
			Assertions.assertNotNull(response);
	}
	    
}
