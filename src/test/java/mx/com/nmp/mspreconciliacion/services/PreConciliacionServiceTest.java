package mx.com.nmp.mspreconciliacion.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.async.AsyncPreconciliacion;
import mx.com.nmp.mspreconciliacion.centropagos.consumer.CentroPagosSoapService;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ResponsePreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.services.impl.SFTPServiceAMEXImpl;
import mx.com.nmp.mspreconciliacion.services.impl.SFTPServiceSantanderImpl;
import mx.com.nmp.mspreconciliacion.util.Util;


@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PreConciliacionServiceTest {

    @MockBean
    @Qualifier("centroPagosSoapService")
	private CentroPagosSoapService wSCentroPagos;
    
    @MockBean
	private SFTPServiceSantanderImpl sFTPServiceSantanderImpl;
    
    @MockBean
	private SFTPServiceAMEXImpl sFTPServiceAMEXImpl;
    
   
    @Autowired
    @Qualifier("preConciliacionSantanderServiceImpl")
    private PreConciliacionSantanderService preConciliacionSantanderService;
    
    
    @Autowired
    @Qualifier("preConciliacionAMEXServiceImpl")
    private PreConciliacionAMEXService preConciliacionAMEXService;
    
    @MockBean
	private AsyncPreconciliacion asyncPreconciliacion;

	
    @BeforeEach
    void setUp(){
    	reset(wSCentroPagos, sFTPServiceSantanderImpl, sFTPServiceAMEXImpl, asyncPreconciliacion);// pagoPrendarioRestService, pagoConciliadoMITRepository, pagoConciliadoEPARepository, pagoMITRepository, pagoRepository);
    }    
    
	@Test
	void consultarTransaccionesSantanderTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(0L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(false);
		
		when(wSCentroPagos.consultarTransacciones(any(), any())).thenReturn(Util.generarMovsCentroPagos());
		when(sFTPServiceSantanderImpl.leerArchivo(any(), any())).thenReturn(Util.generarMovsLecturaArchivo());
		
		ResponsePreConciliacionDTO resultado = preConciliacionSantanderService.consultarTransacciones(datosPrecon);
		Assertions.assertNotNull(resultado);
    } 
	
	@Test
	void consultarTransaccionesAMEXTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(0L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(false);
		
		when(wSCentroPagos.consultarTransacciones(any(), any())).thenReturn(Util.generarMovsCentroPagos());
		when(sFTPServiceAMEXImpl.leerArchivo(any(), any())).thenReturn(Util.generarMovsLecturaArchivo());
		
		ResponsePreConciliacionDTO resultado = preConciliacionAMEXService.consultarTransacciones(datosPrecon);
		Assertions.assertNotNull(resultado);
    }	
	
	@Test
	void consultarTransaccionesSantanderSinDatosTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(163L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(true);
		
		List<MovCorresponsalDTO> lista =new ArrayList<>();
		
		when(wSCentroPagos.consultarTransacciones(any(), any())).thenReturn(lista);
		when(sFTPServiceSantanderImpl.leerArchivo(any(), any())).thenReturn(lista);
		
		ResponsePreConciliacionDTO resultado = preConciliacionSantanderService.consultarTransacciones(datosPrecon);
		Assertions.assertNotNull(resultado);
    }    
	
	@Test
	void consultarTransaccionesAMEXSinDatosTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(164L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(true);
		
		List<MovCorresponsalDTO> lista =new ArrayList<>();
		
		when(wSCentroPagos.consultarTransacciones(any(), any())).thenReturn(lista);
		when(sFTPServiceAMEXImpl.leerArchivo(any(), any())).thenReturn(lista);
		
		ResponsePreConciliacionDTO resultado = preConciliacionAMEXService.consultarTransacciones(datosPrecon);
		Assertions.assertNotNull(resultado);
    } 	
 
}
