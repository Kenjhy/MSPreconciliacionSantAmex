package mx.com.nmp.mspreconciliacion.services;

import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.RestPreconcilacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.EstadoEjecucionEnum;

/**
 * @name PagoPrendarioRestService
 * @description Clase para consumir servicios rest relacionados con el proceso de
 * prenconciliacion dentro del MS Pagos Prendarios
 *
 * @author Quarksoft
 * @version 1.0
 * @createdDate 28/06/2022
 */
public interface PagoPrendarioRestService {

    public RestPreconcilacionDTO updateEstatusEjecucionPreconciliacion(Long idEjecucion, EstadoEjecucionEnum estado, String updatedBy)throws PreconciliacionExcetion;

}
