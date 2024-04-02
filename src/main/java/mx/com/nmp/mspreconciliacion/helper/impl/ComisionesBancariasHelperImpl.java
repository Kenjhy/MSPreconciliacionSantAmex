package mx.com.nmp.mspreconciliacion.helper.impl;

import mx.com.nmp.mspreconciliacion.helper.ComisionesBancariasHelper;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesPaginadoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.MontosComisionesDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @name ComisionesBancariasHelperImpl
 * @description Clase helper con metodos comunes usados sumar los montos de las
 *  *              comisiones para el corresposal santader y amex
 *
 * @author Quarksoft
 * @creationDate 13/12/2022
 * @version 0.1
 */
@Service("comisionesBancariasHelper")
public class ComisionesBancariasHelperImpl implements ComisionesBancariasHelper {

    /**
     * {@inheritDoc}
     */
    public void sumarTotalesComisiones(ComisionesPaginadoDTO comisiones, List<ComisionesDTO> resultadoTotal, Boolean paginado){
        comisiones.setComisionPaguinado(sumarTotales(comisiones.getComisionesList()));
        if(Boolean.TRUE.equals(paginado)) {
            comisiones.setComisionTotal(sumarTotales(resultadoTotal));
        }else{
            comisiones.setComisionTotal(comisiones.getComisionPaguinado());
        }
    }

    /**
     * Método que nos permite sumar los montos de una lista de registros
     * @param resultado
     * @return
     */
    private MontosComisionesDTO sumarTotales(List<ComisionesDTO> resultado){
        MontosComisionesDTO monto = new MontosComisionesDTO();
        for(ComisionesDTO comision : resultado){
            monto.setImporteNeto(monto.getImporteNeto().add(comision.getImporteNeto() != null ? comision.getImporteNeto() : BigDecimal.ZERO));
            monto.setComisionTransaccional(monto.getComisionTransaccional().add(comision.getComisionTransaccional() != null ? comision.getComisionTransaccional() : BigDecimal.ZERO));
            monto.setIvaTransaccional(monto.getIvaTransaccional().add(comision.getIvaTransaccional() != null ? comision.getIvaTransaccional() : BigDecimal.ZERO));
            monto.setSobretasa(monto.getSobretasa().add(comision.getSobretasa() != null ? comision.getSobretasa() : BigDecimal.ZERO));
            monto.setIvaSobretasa(monto.getIvaSobretasa().add(comision.getIvaSobretasa() != null ? comision.getIvaSobretasa() : BigDecimal.ZERO));
            monto.setMontoTotal(monto.getMontoTotal().add(comision.getMontoTotal() != null ? comision.getMontoTotal() : BigDecimal.ZERO));
        }
        return monto;
    }
}
