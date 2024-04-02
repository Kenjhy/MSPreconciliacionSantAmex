package mx.com.nmp.mspreconciliacion.services;

import static org.mockito.Mockito.reset;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.async.AsyncPreconciliacion;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.services.impl.PreConciliacion;
import mx.com.nmp.mspreconciliacion.util.Util;

@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AsyncPreconciliacionTest {

    @MockBean
	private PreConciliacion preConciliacion;
    
    @Autowired
    private AsyncPreconciliacion asyncPreconciliacion;
    
    @BeforeEach
    void setUp(){
    	reset(preConciliacion);
    }
 
	@Test
	void ejecutarProcesoAsincronoTest() {
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(Date.from(Instant.now()));
		datosPrecon.setIdProcesoCron(163L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(true);
		
		Assertions.assertTimeout(Duration.ofMillis(90000), () -> 
		asyncPreconciliacion.ejecutarProcesoAsincrono(Util.generarMovsLecturaArchivo(), datosPrecon, CorresponsalEnum.SANTANDER)
				);
	}
}
