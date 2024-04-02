package mx.com.nmp.mspreconciliacion.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
import mx.com.nmp.mspreconciliacion.services.PreConciliacionAMEXService;
import mx.com.nmp.mspreconciliacion.util.ArchivoSFTPUtil;
import mx.com.nmp.mspreconciliacion.util.FechaUtil;

/**
 *
 * @author QuarkSoft
 *
 */
@Service
public class PreConciliacionAMEXServiceImpl implements PreConciliacionAMEXService{

	@Autowired
	private CentroPagosSoapService centroPagosSoapService;

	@Autowired
	private SFTPServiceAMEXImpl sFTPServiceAMEXImpl;

	@Autowired
	private AsyncPreconciliacion asyncPreconciliacion;

	private final Logger logger = LoggerFactory.getLogger(PreConciliacionAMEXServiceImpl.class);

	@Value(value = "${mspreconciliacion.variables.centroPagosWSInfo.habilitarCentroPagos}")
	public Boolean habilitarCentroPagos;
	
	@Value(value = "${mspreconciliacion.variables.numeroDiasRestaWSOnlinePre}")
	public Integer numeroDiasRestaWSOnlinePre;	

	@Override
	public ResponsePreConciliacionDTO consultarTransacciones(PreConciliacionDTO preConciliacionDTO) throws PagoException, PreconciliacionExcetion{
		ResponsePreConciliacionDTO respuesta = new ResponsePreConciliacionDTO();
		logger.info("Entra a método de  PreConciliacionAMEXServiceImpl, se procesa fecha= {}", preConciliacionDTO.getFecha());
		List<MovCorresponsalDTO> resultado = new ArrayList<>();

		try {
			if (habilitarCentroPagos != null &&  habilitarCentroPagos) {
				logger.info("Se ejecutara Centro de pagos, bandera prendida");
				Date fechaWSCentroPagos= FechaUtil.obtenerFechaMenosXDiasHabil(preConciliacionDTO.getFecha(), numeroDiasRestaWSOnlinePre);
				
				resultado.addAll(centroPagosSoapService.consultarTransacciones(CorresponsalEnum.AMEX, fechaWSCentroPagos));
				resultado=  ArchivoSFTPUtil.actualizarNombreSFTP(resultado, ArchivoSFTPUtil.obtenerNombreArchivo(preConciliacionDTO.getFecha(), CorresponsalEnum.AMEX));
			}else{
				logger.info("No se ejecuta Centro de pagos, bandera apagada o nulla");
			}
			logger.info("Movimientos obtenidos de Centro d pagos {}", resultado.size());

			resultado.addAll(sFTPServiceAMEXImpl.leerArchivo(actualizarFolderReproceso(preConciliacionDTO), new JSch()));

			if (!resultado.isEmpty()) {
				logger.info("PreConciliacionAMEXServiceImpl-lecturaAMEX- Ejecución exitosa- resultado 200, lectura correcta, se inicia proceso asincrono");
				logger.info("PreConciliacionAMEXServiceImpl-lecturaAMEX-Se procesarán {} transacciones a guardar en indices", resultado.size());

				//Proceso asincrono- inserción en indices.
				asyncPreconciliacion.ejecutarProcesoAsincrono(resultado, preConciliacionDTO, CorresponsalEnum.AMEX);

				respuesta.setExito(true);
				respuesta.setMensaje("Ejecucion exitosa, se inicia pre conciliacion");

			}else {
				logger.info("PreConciliacionAMEXServiceImpl-lecturaAMEX- Ejecución exitosa- resultado 200, lectura correcta, Pero no existen registros a procesar");
			}
		}catch(PagoException ex) {
			logger.info("Error al consultarTransacciones-AMEX, {0}", ex);
			//Manejo de excepciones para envoltorio
			respuesta.setCodigo(ex.getEstado());
			respuesta.setMensaje(ex.getDescripcion());
			respuesta.setExito(false);
		}
		return respuesta;
	}

	private PreConciliacionDTO actualizarFolderReproceso(PreConciliacionDTO preConciliacionDTO) {
		if (preConciliacionDTO!= null && preConciliacionDTO.isReprocesamiento()) {
			preConciliacionDTO.setNumeroReintento(1);
		}
		return preConciliacionDTO;
	}


}

