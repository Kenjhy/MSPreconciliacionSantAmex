package mx.com.nmp.mspreconciliacion.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.Query;

import com.fasterxml.jackson.datatype.jdk8.OptionalDoubleSerializer;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.ActualizarDevolucionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.DevolucionAMEXDTO;
import mx.com.nmp.mspreconciliacion.model.dto.DevolucionPaginadoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.MovDevolucionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RequestDevolucionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ResponseDevolucionesDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.Pago;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoEPARepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoMITRepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoMITRepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoRepository;
import mx.com.nmp.mspreconciliacion.util.Util;

@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DevolucionServiceTest {

    
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

    @Autowired
    @Qualifier("devolucionesImpl")
    private DevolucionesService devolucionesService;    
    
    @BeforeEach
    void setUp(){
    	reset(elasticTemplate, pagoConciliadoMITRepository, pagoConciliadoEPARepository, pagoMITRepository, pagoRepository);
    }    


    @SuppressWarnings("unchecked")
	@Test
	void consultarDevolucionesSantanderTest() throws Exception {
		SimpleDateFormat formato= new SimpleDateFormat("yyyy-MM-dd");
		RequestDevolucionesDTO requestDev = new RequestDevolucionesDTO();
		requestDev.setCorresponsal(CorresponsalEnum.SANTANDER.getNombre());
		String fechaIni= "2022-08-10";
		String fechaFin= "2022-08-15";
		
		requestDev.setNumeroPagina(0);
		requestDev.setNumeroRegistros(10);
		requestDev.setTipoDevolucion(Constants.TIPO_DEVOLUCION_ADMVA);
		requestDev.setSucursal("11");
		requestDev.setAfiliacion("0,100");
		requestDev.setEstatus(Constants.ESTATUS_DEVOLUCION_LIQUIDADA);
		requestDev.setFechaDesde(formato.parse(fechaIni));
		requestDev.setFechaHasta(formato.parse(fechaFin));
	  
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHints(Util.generaDevolucionesMIT(false)));
		
		DevolucionPaginadoDTO respuesta= devolucionesService.consultarDevoluciones(requestDev);
		Assertions.assertNotNull(respuesta);
		
		verify(elasticTemplate,atLeastOnce()).search(any(Query.class), any(Class.class));
    }
    
	@SuppressWarnings("unchecked")
	@Test
	void consultarDevolucionesSantanderAutTest() throws Exception {
		SimpleDateFormat formato= new SimpleDateFormat("yyyy-MM-dd");
		RequestDevolucionesDTO requestDev = new RequestDevolucionesDTO();
		requestDev.setCorresponsal(CorresponsalEnum.SANTANDER.getNombre());
		String fechaIni= "2022-08-10";
		String fechaFin= "2022-08-15";
		
		requestDev.setNumeroPagina(0);
		requestDev.setNumeroRegistros(10);
		requestDev.setTipoDevolucion(Constants.TIPO_DEVOLUCION_AUTOMATICA);
		requestDev.setSucursal("11");
		requestDev.setAfiliacion("0,100");
		requestDev.setEstatus(Constants.ESTATUS_DEVOLUCION_SOLICITADA);
		requestDev.setFechaDesde(formato.parse(fechaIni));
		requestDev.setFechaHasta(formato.parse(fechaFin));
	  
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHints(Util.generaDevolucionesMIT(true)));
		
		DevolucionPaginadoDTO respuesta= devolucionesService.consultarDevoluciones(requestDev);
		Assertions.assertNotNull(respuesta);
		
		verify(elasticTemplate,atLeastOnce()).search(any(Query.class), any(Class.class));
    }
    
    @SuppressWarnings("unchecked")
	@Test
	void consultarDevolucionesAMEXTest() throws Exception {
		RequestDevolucionesDTO requestDev = new RequestDevolucionesDTO();
		requestDev.setCorresponsal(CorresponsalEnum.AMEX.getNombre());
		requestDev.setNumeroPagina(0);
		requestDev.setNumeroRegistros(10);
	  
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHints(Util.generaDevolucionesMIT(true)));
		
		DevolucionPaginadoDTO respuesta= devolucionesService.consultarDevoluciones(requestDev);
		Assertions.assertNotNull(respuesta);
		
		verify(elasticTemplate,atLeastOnce()).search(any(Query.class), any(Class.class));
    }
    
    
    @SuppressWarnings("unchecked")
	@Test
    void consultarDevolucionesTestSinPaginadoError(){
		RequestDevolucionesDTO requestDev = new RequestDevolucionesDTO();
		requestDev.setCorresponsal(CorresponsalEnum.AMEX.getNombre());
		
		try {
			when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHints(Util.generaDevolucionesMIT(true)));
			devolucionesService.consultarDevoluciones(requestDev);
			
		}catch(Exception ex) {
			Assertions.assertTrue(ex instanceof PagoException);
			Assertions.assertEquals(((PagoException)ex).getEstado(), PagoException.ERROR_DEVOLUCION_PAGINADO.getEstado());
		}
    }

    @SuppressWarnings("unchecked")
	@Test
    void consultarDevolucionesTestDevIncorError(){
		RequestDevolucionesDTO requestDev = new RequestDevolucionesDTO();
		requestDev.setCorresponsal(CorresponsalEnum.AMEX.getNombre());
		requestDev.setNumeroPagina(0);
		requestDev.setNumeroRegistros(10);
		requestDev.setTipoDevolucion("INCORRECTO");		
		
		try {
			when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHints(Util.generaDevolucionesMIT(false)));
			devolucionesService.consultarDevoluciones(requestDev);
			
		}catch(Exception ex) {
			Assertions.assertTrue(ex instanceof PagoException);
			Assertions.assertEquals(((PagoException)ex).getEstado(), PagoException.ERROR_DEVOLUCION_TIPO.getEstado());
			
		}
    }
    
	@SuppressWarnings("unchecked")
	@Test
    void consultarIndiceAMEXTest() throws Exception{
    	DevolucionAMEXDTO request = new DevolucionAMEXDTO();
    	request.setTipoDevolucion("ADMINISTRATIVA");
		SimpleDateFormat formato= new SimpleDateFormat("yyyy-MM-dd");
		String fecha= "2022-08-10";

		request.setFechaOperacion(formato.parse(fecha));    	
    	List<ResponseDevolucionesDTO> respuesta = null;
		
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHintsEPA(Util.generarDevolucionesEPA()));
		respuesta= devolucionesService.consultarDevolucionesEPA(request);
		Assertions.assertNotNull(respuesta);
    }
	
	@Test
	void actualizarAMEXTest() throws Exception{
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
    	
    	when(pagoMITRepository.findByIdPagoIn(anyList())).thenReturn(Util.generaDevolucionesSolicitadosMIT(true));
    	when(pagoRepository.findFirstById(anyString())).thenReturn(Util.generarDev());
    	when(pagoConciliadoEPARepository.findByIdPagoIn(anyList())).thenReturn(Util.generarDevolucionesEPA());
    	List<ResponseDevolucionesDTO> respuesta =devolucionesService.actualizarEstatusLiquidar(request);
    	Assertions.assertNotNull(respuesta);
	}
	
	@Test
	void actualizarAMEXAutTest() throws Exception{
	  	ActualizarDevolucionDTO request = new ActualizarDevolucionDTO();
    	request.setCorresponsal("MIT AMEX");
    	request.setProcesoAutomatico(true);
    	
    	List<MovDevolucionDTO> listaDevs= new ArrayList<>();
    	MovDevolucionDTO liquida = new MovDevolucionDTO();
    	liquida.setFechaCargoBancario(Date.from(Instant.now()));;
    	liquida.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	liquida.setLiquidar(true);
    	listaDevs.add(liquida);
    	request.setDevolucionesLiquidar(listaDevs);
    	
    	when(pagoMITRepository.findByIdPagoIn(anyList())).thenReturn(Util.generaDevolucionesSolicitadosMIT(false));
    	when(pagoRepository.findFirstById(anyString())).thenReturn(Util.generarDev());
    	when(pagoConciliadoEPARepository.findByIdPagoIn(anyList())).thenReturn(Util.generarDevolucionesEPA());
    	List<ResponseDevolucionesDTO> respuesta =devolucionesService.actualizarEstatusLiquidar(request);
    	Assertions.assertNotNull(respuesta);
	}

	@Test
	void actualizarSantanderTest() throws Exception{
	  	ActualizarDevolucionDTO request = new ActualizarDevolucionDTO();
    	request.setCorresponsal("MIT Santander");
    	request.setProcesoAutomatico(false);
    	
    	List<MovDevolucionDTO> listaDevs= new ArrayList<>();
    	MovDevolucionDTO liquida = new MovDevolucionDTO();
    	liquida.setFechaCargoBancario(Date.from(Instant.now()));;
    	liquida.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	liquida.setLiquidar(true);
    	listaDevs.add(liquida);
    	request.setDevolucionesLiquidar(listaDevs);
    	
    	when(pagoMITRepository.findByIdPagoIn(anyList())).thenReturn(Util.generaDevolucionesSolicitadosMIT(false));
    	when(pagoRepository.findById(anyString())).thenReturn(Optional.of(Util.generarDev()));    	
    	when(pagoConciliadoMITRepository.findByIdPagoIn(anyList())).thenReturn(Util.generarDevolucionesConciliadosMIT());
    	List<ResponseDevolucionesDTO> respuesta =devolucionesService.actualizarEstatusLiquidar(request);
    	Assertions.assertNotNull(respuesta);
	}
	
	@Test
	void actualizarSantanderAutTest() throws Exception{
	  	ActualizarDevolucionDTO request = new ActualizarDevolucionDTO();
    	request.setCorresponsal("MIT Santander");
    	request.setProcesoAutomatico(true);
    	
    	List<MovDevolucionDTO> listaDevs= new ArrayList<>();
    	MovDevolucionDTO liquida = new MovDevolucionDTO();
    	liquida.setFechaCargoBancario(Date.from(Instant.now()));;
    	liquida.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	liquida.setLiquidar(true);
    	listaDevs.add(liquida);
    	request.setDevolucionesLiquidar(listaDevs);
    	
    	when(pagoMITRepository.findByIdPagoIn(anyList())).thenReturn(Util.generaDevolucionesSolicitadosMIT(true));
    	when(pagoRepository.findById(anyString())).thenReturn(Optional.of(Util.generarDev()));
    	
    	when(pagoConciliadoMITRepository.findByIdPagoIn(anyList())).thenReturn(Util.generarDevolucionesConciliadosMIT());
    	List<ResponseDevolucionesDTO> respuesta =devolucionesService.actualizarEstatusLiquidar(request);
    	Assertions.assertNotNull(respuesta);
	}
}
