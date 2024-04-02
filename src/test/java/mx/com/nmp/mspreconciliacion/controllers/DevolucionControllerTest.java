package mx.com.nmp.mspreconciliacion.controllers;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.model.dto.ActualizarDevolucionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.DevolucionAMEXDTO;
import mx.com.nmp.mspreconciliacion.model.dto.DevolucionPaginadoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.MovDevolucionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RequestDevolucionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ResponseDevolucionesDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.services.impl.DevolucionesImpl;
import mx.com.nmp.mspreconciliacion.util.Response;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DevolucionControllerTest {
	
	private final String ENDPOINT_BASE = "/mspreconciliacion/devoluciones";
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private DevolucionesImpl devolucionesService;
    
    
    @BeforeEach
    void setUp(){
       reset(devolucionesService); 
    }
    
    @Test
    void consultarDevolucionesSantanderTest() throws Exception {
		RequestDevolucionesDTO requestDev = new RequestDevolucionesDTO();
		requestDev.setCorresponsal(CorresponsalEnum.SANTANDER.getNombre());
		requestDev.setNumeroPagina(0);
		requestDev.setNumeroRegistros(10);
		requestDev.setTipoDevolucion(Constants.TIPO_DEVOLUCION_ADMVA);
		requestDev.setSucursal("11");
		requestDev.setAfiliacion("0,100");
		requestDev.setEstatus(Constants.ESTATUS_DEVOLUCION_LIQUIDADA);
		
		DevolucionPaginadoDTO respuesta = new DevolucionPaginadoDTO();
		
		respuesta.setNumeroPagina(0);
		respuesta.setNumeroRegistros(3);
		respuesta.setTieneMasPaginas(false);
		respuesta.setTotalRegistros(3);
		
		List<ResponseDevolucionesDTO> devolucionesList= new ArrayList<>();
		ResponseDevolucionesDTO dev1 = new ResponseDevolucionesDTO();
		
		dev1.setAfiliacion("261468");
		dev1.setAutorizacion("12365");
		dev1.setCorresponsal("MIT Santader");
		dev1.setEntidad(null);
		dev1.setFechaTransaccion(Date.from(Instant.now()));;
		dev1.setIdPago("hyypqIYB4XtLsNJGG0PP");
		dev1.setMontoTransaccion("1351.0");
		dev1.setNumeroOperacion("12345078");
		dev1.setSucursal("11");
		dev1.setTarjeta("1234");
		dev1.setTipoDevolucion("ADMINISTRATIVA");
		dev1.setEstatus("LIQUIDADA");
		dev1.setTipoTarjeta("VISA");
		devolucionesList.add(dev1);
		respuesta.setDevolucionesList(devolucionesList);
		
		when(devolucionesService.consultarDevoluciones(any(RequestDevolucionesDTO.class))).thenReturn(respuesta);
		
	       this.mockMvc.perform(post(ENDPOINT_BASE)
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(this.objectMapper.writeValueAsString(requestDev)))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(result -> {
               Response response = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
               Assertions.assertNotNull(response);
               Assertions.assertEquals(Constants.MSG_EXITOSO, response.getMessage());
               DevolucionPaginadoDTO devResult = objectMapper.convertValue(response.getObject(), DevolucionPaginadoDTO.class);
               Assertions.assertFalse(devResult.getDevolucionesList().isEmpty());
           });
    }
    
    
    
    @Test
    void consultarDevolucionesAMEXTest() throws Exception {
		RequestDevolucionesDTO requestDev = new RequestDevolucionesDTO();
		requestDev.setCorresponsal(CorresponsalEnum.AMEX.getNombre());
		requestDev.setNumeroPagina(0);
		requestDev.setNumeroRegistros(10);
		
		DevolucionPaginadoDTO respuesta = new DevolucionPaginadoDTO();
		
		respuesta.setNumeroPagina(0);
		respuesta.setNumeroRegistros(3);
		respuesta.setTieneMasPaginas(false);
		respuesta.setTotalRegistros(3);
		
		List<ResponseDevolucionesDTO> devolucionesList= new ArrayList<>();
		ResponseDevolucionesDTO dev1 = new ResponseDevolucionesDTO();
		
		dev1.setAfiliacion("0");
		dev1.setAutorizacion("12365");
		dev1.setCorresponsal("MIT AMEX");
		dev1.setEntidad(null);
		dev1.setEstatus("LIQUIDADA");
		dev1.setFechaTransaccion(Date.from(Instant.now()));;
		dev1.setIdPago("0SriPoMBBaPdYAQHMKxW");
		dev1.setMontoTransaccion("0SriPoMBBaPdYAQHMKxW");
		dev1.setNumeroOperacion("957402");
		dev1.setSucursal("313");
		dev1.setTarjeta("376702XXXXX3025");
		dev1.setTipoDevolucion("AUTOMATICA");
		dev1.setTipoTarjeta("VISA");
		devolucionesList.add(dev1);
		respuesta.setDevolucionesList(devolucionesList);
		
		when(devolucionesService.consultarDevoluciones(any(RequestDevolucionesDTO.class))).thenReturn(respuesta);
		
	       this.mockMvc.perform(post(ENDPOINT_BASE)
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(this.objectMapper.writeValueAsString(requestDev)))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(result -> {
               Response response = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
               Assertions.assertNotNull(response);
               Assertions.assertEquals(Constants.MSG_EXITOSO, response.getMessage());
               DevolucionPaginadoDTO devResult = objectMapper.convertValue(response.getObject(), DevolucionPaginadoDTO.class);
               Assertions.assertFalse(devResult.getDevolucionesList().isEmpty());
           });
    }
    
    
    @Test
    void actualizarTest() throws Exception {
    	ActualizarDevolucionDTO request = new ActualizarDevolucionDTO();
    	request.setCorresponsal("MIT AMEX");
    	request.setProcesoAutomatico(false);
    	
    	List<MovDevolucionDTO> listaDevs= new ArrayList<>();
    	MovDevolucionDTO liquida = new MovDevolucionDTO();
    	liquida.setFechaCargoBancario(Date.from(Instant.now()));;
    	liquida.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	liquida.setLiquidar(true);
    	listaDevs.add(liquida);
    	request.setDevolucionesLiquidar(listaDevs);
    	

    	List<ResponseDevolucionesDTO> devolucionesList= new ArrayList<>();
		ResponseDevolucionesDTO dev1 = new ResponseDevolucionesDTO();
		dev1.setAfiliacion("0");
		dev1.setAutorizacion("12365");
		dev1.setCorresponsal("MIT AMEX");
		dev1.setEntidad(null);
		dev1.setEstatus("LIQUIDADA");
		dev1.setFechaTransaccion(Date.from(Instant.now()));;
		dev1.setIdPago("0SriPoMBBaPdYAQHMKxW");
		dev1.setMontoTransaccion("0SriPoMBBaPdYAQHMKxW");
		dev1.setNumeroOperacion("957402");
		dev1.setSucursal("313");
		dev1.setTarjeta("376702XXXXX3025");
		dev1.setTipoDevolucion("ADMINISTRATIVA");
		dev1.setTipoTarjeta("VISA");
		devolucionesList.add(dev1);


		when(devolucionesService.actualizarEstatusLiquidar(any(ActualizarDevolucionDTO.class))).thenReturn(devolucionesList);
		
	       this.mockMvc.perform(post(ENDPOINT_BASE+"/actualizar")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(this.objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(result -> {
               Response response = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
               Assertions.assertNotNull(response);
               Assertions.assertEquals(Constants.MSG_EXITOSO, response.getMessage());
               @SuppressWarnings("unchecked")
               List<ResponseDevolucionesDTO> devResult = objectMapper.convertValue(response.getObject(), List.class);
               Assertions.assertFalse(devResult.isEmpty());
           });
    }    
    
    
    @Test
    void consultarIndiceAMEXTest() throws Exception {
    	DevolucionAMEXDTO request = new DevolucionAMEXDTO();
    	request.setTipoDevolucion("ADMINISTRATIVA");
		
    	List<ResponseDevolucionesDTO> resultado = new ArrayList<>();
    	ResponseDevolucionesDTO dev1 = new ResponseDevolucionesDTO(); 
		dev1.setAfiliacion("0");
		dev1.setAutorizacion("12365");
		dev1.setCorresponsal("MIT AMEX");
		dev1.setEntidad(null);
		dev1.setEstatus("LIQUIDADA");
		dev1.setFechaTransaccion(Date.from(Instant.now()));;
		dev1.setIdPago("0SriPoMBBaPdYAQHMKxW");
		dev1.setMontoTransaccion("0SriPoMBBaPdYAQHMKxW");
		dev1.setNumeroOperacion("957402");
		dev1.setSucursal("313");
		dev1.setTarjeta("376702XXXXX3025");
		dev1.setTipoDevolucion("ADMINISTRATIVA");
		dev1.setTipoTarjeta("VISA");
		resultado.add(dev1);

		
		when(devolucionesService.consultarDevolucionesEPA(any(DevolucionAMEXDTO.class))).thenReturn(resultado);
		
	       this.mockMvc.perform(post(ENDPOINT_BASE+"/consultaamex")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(this.objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(result -> {
               Response response = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
               Assertions.assertNotNull(response);
               Assertions.assertEquals(Constants.MSG_EXITOSO, response.getMessage());
               @SuppressWarnings("unchecked")
			List<ResponseDevolucionesDTO> devResult = objectMapper.convertValue(response.getObject(), List.class);
               Assertions.assertFalse(devResult.isEmpty());
           });
    }
	
}
