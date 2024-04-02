package mx.com.nmp.mspreconciliacion.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.Query;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.model.enums.EstadoPagosEnum;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoMIT;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoEPARepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoMITRepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoMITRepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoRepository;
import mx.com.nmp.mspreconciliacion.services.impl.PreConciliacion;
import mx.com.nmp.mspreconciliacion.util.Util;

@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PreconciliacionTest {

	
    @MockBean
    private ElasticsearchRestTemplate elasticTemplate;
    
    @MockBean
    private IPagoConciliadoMITRepository pagoConciliadoMITRepository;
    
    @MockBean
    private IPagoConciliadoEPARepository pagoConciliadoEPARepository;

    @MockBean
    private IPagoMITRepository pagoMITRepository;

    @MockBean
    private IPagoRepository pagoRepository;	
    
    @MockBean
	@Qualifier("pagoPrendarioRestService")
	private PagoPrendarioRestService pagoPrendarioRestService;

    
    @Autowired
	private PreConciliacion preConciliacion;

	
    @BeforeEach
    void setUp(){
    	reset(elasticTemplate, pagoPrendarioRestService, pagoConciliadoMITRepository, pagoConciliadoEPARepository, pagoMITRepository, pagoRepository);
    } 
    
	@Test
	void procesoAsincronoSantanderActualizaCronTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(163L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(true);
		
		when(pagoMITRepository.findByCorresponsalRspOperationNumberAndEstadoIn(any(), anyList())).thenReturn(Util.buscarPagosMIT());
		when(pagoRepository.findById(any())).thenReturn(Util.encontrarPagoPreconciliacion());
		when(pagoConciliadoMITRepository.findByIdPago(any())).thenReturn(Util.generarDevolucionesConciliadosMIT().get(0));
		when(pagoConciliadoEPARepository.findByIdPago(any())).thenReturn(Util.generarPagosEPA().get(0));
		when(pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(any(), any(),any())).thenReturn(Util.respuestaActualizaCron());
		
		boolean exito = preConciliacion.procesoAsincrono(Util.generarMovsLecturaArchivo(), datosPrecon, CorresponsalEnum.SANTANDER);
		assertTrue(exito);
    }
	
	@Test
	void procesoAsincronoSantanderSinMOPagoMITTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(163L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(true);
		
		when(pagoMITRepository.findByCorresponsalRspOperationNumberAndEstadoIn(any(), anyList())).thenReturn(new ArrayList<PagoMIT>() );
		when(pagoRepository.save(any())).thenReturn(Util.generarPagoGuardado());
		when(pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(any(), any(),any())).thenReturn(Util.respuestaActualizaCron());
		
		boolean exito = preConciliacion.procesoAsincrono(Util.generarMovsLecturaArchivo(), datosPrecon, CorresponsalEnum.SANTANDER);
		assertTrue(exito);
    }
	

	@Test
	void procesoAsincronoSantanderDevolucionExistente() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(0L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(false);
		
		when(pagoMITRepository.findByCorresponsalRspOperationNumberAndEstadoIn(any(), anyList())).thenReturn(Util.buscarPagosMIT());
		when(pagoRepository.findById(any())).thenReturn(Util.encontrarDevPreconciliacion());
		when(pagoConciliadoMITRepository.findByIdPago(any())).thenReturn(null);
		when(pagoConciliadoEPARepository.findByIdPago(any())).thenReturn(null);
		when(pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(any(), any(),any())).thenReturn(Util.respuestaActualizaCronError());
		
		boolean exito =preConciliacion.procesoAsincrono(Util.generarMovsLecturaArchivo(), datosPrecon, CorresponsalEnum.SANTANDER);
		assertTrue(exito);
    } 	
	
	@Test
	void procesoAsincronoSantanderTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(0L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(false);
		
		when(pagoMITRepository.findByCorresponsalRspOperationNumberAndEstadoIn(any(), anyList())).thenReturn(Util.buscarPagosMIT());
		when(pagoRepository.findById(any())).thenReturn(Util.encontrarPagoPreconciliacion());
		when(pagoConciliadoMITRepository.findByIdPago(any())).thenReturn(Util.generarDevolucionesConciliadosMIT().get(0));
		when(pagoConciliadoEPARepository.findByIdPago(any())).thenReturn(Util.generarPagosEPA().get(0));
		when(pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(any(), any(),any())).thenReturn(Util.respuestaActualizaCron());
		
		boolean exito =preConciliacion.procesoAsincrono(Util.generarMovsLecturaArchivo(), datosPrecon, CorresponsalEnum.SANTANDER);
		assertTrue(exito);
    } 	
	
	@Test
	void procesoAsincronoAMEXTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(0L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(false);
		
		when(pagoMITRepository.findByCorresponsalRspOperationNumberAndEstadoIn(any(), anyList())).thenReturn(Util.buscarPagosMIT());
		when(pagoRepository.findById(any())).thenReturn(Util.encontrarPagoPreconciliacion());
		when(pagoConciliadoMITRepository.findByIdPago(any())).thenReturn(Util.generarDevolucionesConciliadosMIT().get(0));
		when(pagoConciliadoEPARepository.findByIdPago(any())).thenReturn(Util.generarPagosEPA().get(0));
		when(pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(any(), any(),any())).thenReturn(Util.respuestaActualizaCron());
		
		boolean exito =preConciliacion.procesoAsincrono(Util.generarMovsLecturaArchivo(), datosPrecon, CorresponsalEnum.AMEX);
		assertTrue(exito);
    }
	
	@SuppressWarnings("unchecked")
	@Test
	void procesoAsincronoAMEXDevolucionesTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(0L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(false);
		
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHints(Util.generaDevMIT()));
		when(pagoRepository.findById(any())).thenReturn(Util.encontrarPagoPreconciliacion());
		when(pagoConciliadoMITRepository.findByIdPago(any())).thenReturn(Util.generarDevolucionesConciliadosMIT().get(0));
		when(pagoConciliadoEPARepository.findByIdPago(any())).thenReturn(Util.generarPagosEPA().get(0));
		when(pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(any(), any(),any())).thenReturn(Util.respuestaActualizaCron());
		
		boolean exito =preConciliacion.procesoAsincrono(Util.generarMovsLecturaAMEXArchivoDevs(), datosPrecon, CorresponsalEnum.AMEX);
		assertTrue(exito);
    }
	
	@Test
	void procesoAsincronoAMEXActualizaCronTest() throws PagoException, InterruptedException, PreconciliacionExcetion {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(164L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(true);
		
		when(pagoMITRepository.findByCorresponsalRspOperationNumberAndEstadoIn(any(), anyList())).thenReturn(Util.buscarPagosMIT());
		when(pagoRepository.findById(any())).thenReturn(Util.encontrarPagoPreconciliacion());
		when(pagoConciliadoMITRepository.findByIdPago(any())).thenReturn(Util.generarDevolucionesConciliadosMIT().get(0));
		when(pagoConciliadoEPARepository.findByIdPago(any())).thenReturn(Util.generarPagosEPA().get(0));
		when(pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(any(), any(),any())).thenReturn(Util.respuestaActualizaCron());
		
		boolean exito = preConciliacion.procesoAsincrono(Util.generarMovsLecturaArchivo(), datosPrecon, CorresponsalEnum.AMEX);
		assertTrue(exito);
    } 	
}
