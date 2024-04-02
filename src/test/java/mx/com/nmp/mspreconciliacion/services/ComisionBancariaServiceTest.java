package mx.com.nmp.mspreconciliacion.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

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
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.Query;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.consumer.rest.ComisionesMIDAsService;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.CatalogoComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesPaginadoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RequestComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoEPARepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoMITRepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoMITRepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoRepository;
import mx.com.nmp.mspreconciliacion.util.Util;

@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ComisionBancariaServiceTest {
	
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
    @Qualifier("comisionesMIDAsService")
    private ComisionesMIDAsService comisionesMIDAsService;
    
    @Autowired
    @Qualifier("comisionBancariaAMEXServiceImpl")    
	private ComsionBancariaService comisionBancariaAMEXServiceImpl;

    @Autowired
    @Qualifier("comisionBancariaSantanderServiceImpl")    
	private ComsionBancariaService comisionBancariaSantanderServiceImpl;
    
    @BeforeEach
    void setUp(){
    	reset(elasticTemplate, pagoConciliadoMITRepository, pagoConciliadoEPARepository, pagoMITRepository, pagoRepository, comisionesMIDAsService);
    }
    
    @SuppressWarnings("unchecked")
	@Test
    void consultarComisionesAMEX() throws Exception {
    	SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    	String fecha= "2022-09-14";
		RequestComisionesDTO comisionesDTO= new RequestComisionesDTO();
		comisionesDTO.setCorresponsal(CorresponsalEnum.AMEX.getNombre());
		comisionesDTO.setFecha(formato.parse(fecha));
		comisionesDTO.setNumeroPagina(0);
		comisionesDTO.setNumeroRegistros(10);
		comisionesDTO.setRequierePaginado(true);
		comisionesDTO.setSucursal("0");
		comisionesDTO.setTipoPago("6 MSI");
		comisionesDTO.setTipoTarjeta("CREDITO");
		
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHintsEPA(Util.generarPagosEPA()));
		when(comisionesMIDAsService.consultarComisionesMIDAS(any())).thenReturn(Util.respuestaWSComisiones());
		
		ComisionesPaginadoDTO resultado = comisionBancariaAMEXServiceImpl.consultarComisiones(comisionesDTO);
		Assertions.assertNotNull(resultado);
    }
    
    @SuppressWarnings("unchecked")
	@Test
    void consultarComisionesAMEXIncbanco() throws Exception {
    	SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    	String fecha= "2022-09-14";
		RequestComisionesDTO comisionesDTO= new RequestComisionesDTO();
		comisionesDTO.setCorresponsal(CorresponsalEnum.AMEX.getNombre());
		comisionesDTO.setFecha(formato.parse(fecha));
		comisionesDTO.setNumeroPagina(0);
		comisionesDTO.setNumeroRegistros(10);
		comisionesDTO.setRequierePaginado(true);
		comisionesDTO.setSucursal("0");
		comisionesDTO.setTipoPago("6 MSI");
		comisionesDTO.setTipoTarjeta("CREDITO");
		comisionesDTO.setBanco("AMEXtest");
		
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHintsEPA(Util.generarPagosEPA()));
		when(comisionesMIDAsService.consultarComisionesMIDAS(any())).thenReturn(Util.respuestaWSComisiones());
		
		try{
			comisionBancariaAMEXServiceImpl.consultarComisiones(comisionesDTO);
    	}catch(Exception ex) {
			Assertions.assertTrue(ex instanceof PagoException);
		}
    }
    
    @SuppressWarnings("unchecked")
	@Test
    void consultarComisionesAMEXFechaVacia() throws Exception {
		RequestComisionesDTO comisionesDTO= new RequestComisionesDTO();
		comisionesDTO.setCorresponsal(CorresponsalEnum.AMEX.getNombre());
		comisionesDTO.setFecha(null);
		comisionesDTO.setNumeroPagina(0);
		comisionesDTO.setNumeroRegistros(10);
		comisionesDTO.setRequierePaginado(true);
		comisionesDTO.setSucursal("0");
		comisionesDTO.setTipoPago("6 MSI");
		comisionesDTO.setTipoTarjeta("CREDITO");
		comisionesDTO.setBanco("AMEX");
		
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHintsEPA(Util.generarPagosEPA()));
		when(comisionesMIDAsService.consultarComisionesMIDAS(any())).thenReturn(Util.respuestaWSComisiones());
		
		try{
			comisionBancariaAMEXServiceImpl.consultarComisiones(comisionesDTO);
    	}catch(Exception ex) {
			Assertions.assertTrue(ex instanceof PagoException);
		}
    } 
    
    @SuppressWarnings("unchecked")
	@Test
    void consultarComisionesSantander() throws Exception {
    	SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    	String fecha= "2022-09-14";
		RequestComisionesDTO comisionesDTO= new RequestComisionesDTO();
		comisionesDTO.setCorresponsal(CorresponsalEnum.SANTANDER.getNombre());
		comisionesDTO.setFecha(formato.parse(fecha));
		comisionesDTO.setNumeroPagina(0);
		comisionesDTO.setNumeroRegistros(10);
		comisionesDTO.setRequierePaginado(true);
		comisionesDTO.setSucursal("0");
		comisionesDTO.setTipoPago("3 MSI");
		comisionesDTO.setTipoTarjeta("CREDITO");
		comisionesDTO.setBanco("BANAMEX");
		
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHintsConciliadoMIT(Util.generarPagosConciliadosMIT()));
		when(comisionesMIDAsService.consultarComisionesMIDAS(any())).thenReturn(Util.respuestaWSComisionesSantander());
		
		ComisionesPaginadoDTO resultado = comisionBancariaSantanderServiceImpl.consultarComisiones(comisionesDTO);
		Assertions.assertNotNull(resultado);
    }
    
    @SuppressWarnings("unchecked")
	@Test
    void reporteComisionesAMEX() throws Exception {
    	SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    	String fecha= "2022-09-14";
		RequestComisionesDTO comisionesDTO= new RequestComisionesDTO();
		comisionesDTO.setCorresponsal(CorresponsalEnum.AMEX.getNombre());
		comisionesDTO.setFecha(formato.parse(fecha));
		comisionesDTO.setNumeroPagina(0);
		comisionesDTO.setNumeroRegistros(10);
		comisionesDTO.setRequierePaginado(true);
		comisionesDTO.setSucursal("0");
		comisionesDTO.setTipoPago("3 MSI");
		comisionesDTO.setTipoTarjeta("CREDITO");
		
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHintsEPA(Util.generarPagosEPA()));
		when(comisionesMIDAsService.consultarComisionesMIDAS(any())).thenReturn(Util.respuestaWSComisiones());		
		String resultado = comisionBancariaAMEXServiceImpl.reporteComisiones(comisionesDTO);
		Assertions.assertNotNull(resultado);
    }    

    @SuppressWarnings("unchecked")
	@Test
    void reporteComisionesSantander() throws Exception {
    	SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    	String fecha= "2022-09-14";
		RequestComisionesDTO comisionesDTO= new RequestComisionesDTO();
		comisionesDTO.setCorresponsal(CorresponsalEnum.SANTANDER.getNombre());
		comisionesDTO.setFecha(formato.parse(fecha));
		comisionesDTO.setNumeroPagina(0);
		comisionesDTO.setNumeroRegistros(10);
		comisionesDTO.setRequierePaginado(true);
		comisionesDTO.setSucursal("0");
		comisionesDTO.setTipoPago("6 MSI");
		comisionesDTO.setTipoTarjeta("CREDITO");
		comisionesDTO.setBanco("BANAMEX");
		
		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHintsConciliadoMIT(Util.generarPagosConciliadosMIT()));
		when(comisionesMIDAsService.consultarComisionesMIDAS(any())).thenReturn(Util.respuestaWSComisionesSantander());
		String resultado = comisionBancariaSantanderServiceImpl.reporteComisiones(comisionesDTO);
		Assertions.assertNotNull(resultado);
    }
    
    @Test
    void consultarCatalogoComisionesAMEX() {
    	
    	when(pagoConciliadoEPARepository.findByCorresponsalFechaOperacion(any())).thenReturn(Util.generarPagosEPA());
    	CatalogoComisionesDTO resultado = comisionBancariaAMEXServiceImpl.consultarCatalogoComisiones(Date.from(Instant.now()));
    	Assertions.assertNotNull(resultado);
    }
    
    @Test
    void consultarCatalogoComisionesSantander() {

		when(elasticTemplate.search(any(Query.class), any(Class.class))).thenReturn(Util.getSimpleSearchHintsConciliadoMIT(Util.generarPagosConciliadosMIT()));
    	CatalogoComisionesDTO resultado = comisionBancariaSantanderServiceImpl.consultarCatalogoComisiones(Date.from(Instant.now()));
    	Assertions.assertNotNull(resultado);
    }
}
