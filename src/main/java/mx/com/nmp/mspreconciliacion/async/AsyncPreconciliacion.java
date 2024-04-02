package mx.com.nmp.mspreconciliacion.async;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.services.impl.PreConciliacion;


@Component
public class AsyncPreconciliacion {

	private static final Logger LOG = LoggerFactory.getLogger(AsyncPreconciliacion.class);


	@Autowired
	private PreConciliacion preConciliacion;

	/***
	 * Método asincrono que realiza la inserción en Indices de acuerdo a lista de movimientos encontrados
	 * @param resultado
	 * @param preConciliacionDTO
	 * @throws PreconciliacionExcetion
	 */
	@Async("preconciliacionAsyncExecutor")
	public void ejecutarProcesoAsincrono(List<MovCorresponsalDTO> resultado, PreConciliacionDTO preConciliacionDTO, CorresponsalEnum corresponsalEnum) throws PreconciliacionExcetion {
		LOG.info("AsyncPreconciliacion-Entra proceso asincrono");

		//Proceso asincrono- inserción en indices.
		preConciliacion.procesoAsincrono(resultado, preConciliacionDTO, corresponsalEnum);
	}

}
