package mx.com.nmp.mspreconciliacion.services;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.*;


/**
 *
 * @name DevolucionesService
 * @description Interfaz que contiene los métodos para consulta de devoluciones
 * @author QuarkSoft
 *
 */

public interface DevolucionesService {

    /**
     * Obtiene listado de devoluciones
     * @param requestDevolucionesDTO
     * @return List<ResponseDevolucionesDTO>
     */
    public DevolucionPaginadoDTO consultarDevoluciones(RequestDevolucionesDTO requestDevolucionesDTO) throws PagoException;


    public List<ResponseDevolucionesDTO> consultarDevolucionesEPA(DevolucionAMEXDTO requestDevolucionAMEXDTO ) throws PagoException;


    public List<ResponseDevolucionesDTO> actualizarEstatusLiquidar(ActualizarDevolucionDTO movimientosLiquidar)throws PagoException;

    public boolean actualizarLiquidadasConciliadas(List<ResponseDevolucionesDTO> devoluciones)throws PagoException;

    public Map<String, Object> consultaTotalDevolucionesLiquidadas(RequestTotalDevolucionesDTO totalDevolucionesDTO) throws PagoException, ParseException;
}
