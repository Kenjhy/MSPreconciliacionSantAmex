package mx.com.nmp.mspreconciliacion.services.impl;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.JSch;

import mx.com.nmp.mspreconciliacion.async.AsyncPreconciliacion;
import mx.com.nmp.mspreconciliacion.centropagos.consumer.CentroPagosSoapService;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ResponsePreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.services.PreConciliacionSantanderService;
import mx.com.nmp.mspreconciliacion.util.ArchivoSFTPUtil;
import mx.com.nmp.mspreconciliacion.util.FechaUtil;

/**
 *
 * @author QuarkSoft
 *
 */
@Service
public class PreConciliacionSantanderServiceImpl  implements PreConciliacionSantanderService{

	@Autowired
	private CentroPagosSoapService centroPagosSoapService;

	@Autowired
	private SFTPServiceSantanderImpl sFTPServiceSantanderImpl;

	@Autowired
	private AsyncPreconciliacion asyncPreconciliacion;

	private static final Logger LOG = LoggerFactory.getLogger(PreConciliacionSantanderServiceImpl.class);

	@Value(value = "${mspreconciliacion.variables.centroPagosWSInfo.habilitarCentroPagos}")
	public Boolean habilitarCentroPagos;

	@Value(value = "${mspreconciliacion.variables.numeroDiasRestaWSOnlinePre}")
	public Integer numeroDiasRestaWSOnlinePre;
	
	/**
	 * {@inheritDoc}
	 * @throws PreconciliacionExcetion
	 * @throws Exception
	 */
	@Override
	public ResponsePreConciliacionDTO consultarTransacciones(PreConciliacionDTO preConciliacionDTO) throws PagoException, PreconciliacionExcetion{
		ResponsePreConciliacionDTO respuesta = new ResponsePreConciliacionDTO();
		LOG.info("Entra a método de PreConciliacionSantanderServiceImpl, se procesa fecha= {}", preConciliacionDTO.getFecha());
		List<MovCorresponsalDTO> resultado = new ArrayList<>();

		try {
			if (habilitarCentroPagos != null &&  habilitarCentroPagos) {
				LOG.info("Se ejecutara Centro de pagos, bandera prendida");
				Date fechaWSCentroPagos= FechaUtil.obtenerFechaMenosXDiasHabil(preConciliacionDTO.getFecha(), numeroDiasRestaWSOnlinePre);
				
				resultado.addAll(centroPagosSoapService.consultarTransacciones(CorresponsalEnum.SANTANDER, fechaWSCentroPagos));
				resultado=  ArchivoSFTPUtil.actualizarNombreSFTP(resultado, ArchivoSFTPUtil.obtenerNombreArchivo(preConciliacionDTO.getFecha(), CorresponsalEnum.SANTANDER));
			}else{
				LOG.info("No se ejecuta Centro de pagos, bandera apagada o nulla");
			}
			LOG.info("Movimientos obtenidos de Centro d pagos {}", resultado.size());

			resultado.addAll(sFTPServiceSantanderImpl.leerArchivo(preConciliacionDTO, new JSch()));

			if (!resultado.isEmpty()) {
				LOG.info("PreConciliacionSantanderServiceImpl-lecturaSantander- Ejecución exitosa- resultado 200, lectura correcta, se inicia proceso asincrono");
				LOG.info("PreConciliacionSantanderServiceImpl-lecturaSantander-Se procesarán {} transacciones a guardar en indices", resultado.size());

				//Proceso asincrono- inserción en indices.
				asyncPreconciliacion.ejecutarProcesoAsincrono(resultado, preConciliacionDTO, CorresponsalEnum.SANTANDER);

			}else {
				LOG.info("PreConciliacionSantanderServiceImpl-lectura- Ejecución exitosa- resultado 200, lectura correcta, Pero no existen registros a procesar");
			}
			respuesta.setExito(true);
			respuesta.setMensaje("Ejecucion exitosa, se inicia pre conciliacion");

		}catch(PagoException ex) {
			LOG.info("Error al consultarTransacciones-Santander, {}", ex.getDescripcion());
			//Manejo de excepciones para envoltorio
			respuesta.setCodigo(ex.getEstado());
			respuesta.setMensaje(ex.getDescripcion());
			respuesta.setExito(false);
		}

		return respuesta;
	}


}