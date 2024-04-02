package mx.com.nmp.mspreconciliacion.services;

import java.util.Date;

import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.exceptions.SistemaException;
import mx.com.nmp.mspreconciliacion.model.dto.CatalogoComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesPaginadoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RequestComisionesDTO;

/***
 * @name ComsionBancariaService
 * @description Interfaz que contiene los métodos de las comisiones de corresponsal AMEX/ Santander 
 * @author QuarkSoft
 *
 */
public interface ComsionBancariaService {
	
	/**
	 * Método que consulta las comisiones de los corresponsales Santander/AMEX
	 * @param comisionesDTO
	 * @return
	 * @throws SistemaException
	 */
	public ComisionesPaginadoDTO consultarComisiones(RequestComisionesDTO comisionesDTO) throws SistemaException, PagoException;
	
	
	/**
     * Genera el reporte de comisiones bancarias Santander/ AMEX
     * @param fecha fecha de lo pagos
     * @return bytes del reporte
     */
    public String reporteComisiones(RequestComisionesDTO comisionesDTO) throws SistemaException, PagoException;

    
	public CatalogoComisionesDTO consultarCatalogoComisiones(Date fechaOperacion);
}
