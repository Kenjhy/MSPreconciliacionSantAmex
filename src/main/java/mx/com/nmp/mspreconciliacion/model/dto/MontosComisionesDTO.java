package mx.com.nmp.mspreconciliacion.model.dto;

import java.math.BigDecimal;

/**
 * @name MontosComisionesDTO
 * @description Clase que encapsula la informacion perteneciente a un
 *              los montos asociados a una comision
 * @author Quarksoft
 * @creationDate 09/12/2022
 * @version 0.1
 */
public class MontosComisionesDTO{

    private BigDecimal importeNeto;

    private BigDecimal comisionTransaccional; // Tasa de descuento

    private BigDecimal ivaTransaccional; // IVA Tasa de descuento

    private BigDecimal sobretasa;//Comision Promocion

    private BigDecimal ivaSobretasa; // IVA Promocion

    private BigDecimal montoTotal;

    public MontosComisionesDTO(){
        importeNeto = BigDecimal.ZERO;
        comisionTransaccional = BigDecimal.ZERO;
        ivaTransaccional = BigDecimal.ZERO;
        sobretasa = BigDecimal.ZERO;
        ivaSobretasa = BigDecimal.ZERO;
        montoTotal = BigDecimal.ZERO;
    }

    public BigDecimal getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
    }

    public BigDecimal getComisionTransaccional() {
        return comisionTransaccional;
    }

    public void setComisionTransaccional(BigDecimal comisionTransaccional) {
        this.comisionTransaccional = comisionTransaccional;
    }

    public BigDecimal getIvaTransaccional() {
        return ivaTransaccional;
    }

    public void setIvaTransaccional(BigDecimal ivaTransaccional) {
        this.ivaTransaccional = ivaTransaccional;
    }

    public BigDecimal getSobretasa() {
        return sobretasa;
    }

    public void setSobretasa(BigDecimal sobretasa) {
        this.sobretasa = sobretasa;
    }

    public BigDecimal getIvaSobretasa() {
        return ivaSobretasa;
    }

    public void setIvaSobretasa(BigDecimal ivaSobretasa) {
        this.ivaSobretasa = ivaSobretasa;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}
