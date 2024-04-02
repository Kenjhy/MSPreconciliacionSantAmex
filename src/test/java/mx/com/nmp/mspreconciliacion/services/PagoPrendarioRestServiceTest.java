package mx.com.nmp.mspreconciliacion.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.RestPreconcilacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.EstadoEjecucionEnum;
import mx.com.nmp.mspreconciliacion.util.Util;

@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PagoPrendarioRestServiceTest {

	@MockBean
	private RetryTemplate retryTemplate;
	
	@MockBean
	private RestTemplate restTemplate;

	@Autowired
	@Qualifier("pagoPrendarioRestService")
	private PagoPrendarioRestService pagoPrendarioRestService;
	
	@BeforeEach
	void setUp(){
		reset(retryTemplate, restTemplate);
	}  
	
	@SuppressWarnings("unchecked")
	@Test
	void updateEstatusEjecucionPreconciliacionTest() throws Throwable {
		String url= "http://localhost:8081/mimonte/ejecucionpreconciliacion/actualizar";
		ResponseEntity<RestPreconcilacionDTO> resp= null;
		resp= new ResponseEntity<RestPreconcilacionDTO>(Util.respuestaActualizaCron(), HttpStatus.OK);
		
		when(retryTemplate.execute(any(),any(),any())).thenAnswer(invocation -> {
			RetryCallback<RestPreconcilacionDTO, PreconciliacionExcetion> retry = invocation.getArgument(0);
	        return retry.doWithRetry((RetryContext) Util.respuestaActualizaCron());
		});		

		when(restTemplate.exchange(ArgumentMatchers.eq(url), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(resp);
		RestPreconcilacionDTO respuesta= pagoPrendarioRestService.updateEstatusEjecucionPreconciliacion(150L, EstadoEjecucionEnum.DESCARGACORRECTA, "test");
		Assertions.assertNotNull(respuesta);
	}
	
}


