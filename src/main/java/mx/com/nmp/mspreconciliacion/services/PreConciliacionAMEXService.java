package mx.com.nmp.mspreconciliacion.services;


import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ResponsePreConciliacionDTO;



/**
 *
 * @name PreConciliacionAMEXService
 * @description Interfaz que contiene los métodos para movimientos corresponsal AMEX 
 * @author QuarkSoft
 *
 */



public interface PreConciliacionAMEXService {

	public ResponsePreConciliacionDTO consultarTransacciones(PreConciliacionDTO preConciliacionDTO) throws PagoException, PreconciliacionExcetion;
}
