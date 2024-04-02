package mx.com.nmp.mspreconciliacion.helper;

import mx.com.nmp.mspreconciliacion.model.dto.ComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesPaginadoDTO;

import java.util.List;

/**
 * @name ComisionesBancariasHelper
 * @description Interface que expone los metodos comunes usados sumar los montos de las
 *              comisiones para el corresposal santader y amex
 *
 * @author Quarksoft
 * @creationDate 13/12/2022
 * @version 0.1
 */

public interface ComisionesBancariasHelper {

    /**
     * Método que nos ayuda a obtener el monto total de los registros encotrados para el paguinado como para el total de elementos
     * @param comisiones
     * @param resultadoTotal
     * @param paginado
     */
    public void sumarTotalesComisiones(ComisionesPaginadoDTO comisiones, List<ComisionesDTO> resultadoTotal, Boolean paginado);

}
