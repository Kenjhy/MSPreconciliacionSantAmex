package mx.com.nmp.mspreconciliacion.model.dto;

import mx.com.nmp.mspreconciliacion.model.enums.TipoTarjetaEnum;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Clase con la información pata el reporte de Comisiones Bancarias Santander
 */
public class ComisionesBancariasDTO implements Serializable {

    private static final long serialVersionUID = 5133940995351269580L;

    private String sucursal;

    private String fecha;

    private BigDecimal importe;

    private String emisor;

    private String banco;

    private TipoTarjetaEnum tipoTarjeta;

    private String tipoPago;

    private BigDecimal comisionTransaccional;

    private BigDecimal ivaTransaccionalidad;

    private BigDecimal sobreTasa;

    private BigDecimal ivaSobreTasa;

    //getters and setters

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public TipoTarjetaEnum getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(TipoTarjetaEnum tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public BigDecimal getComisionTransaccional() {
        return comisionTransaccional;
    }

    public void setComisionTransaccional(BigDecimal comisionTransaccional) {
        this.comisionTransaccional = comisionTransaccional;
    }

    public BigDecimal getIvaTransaccionalidad() {
        return ivaTransaccionalidad;
    }

    public void setIvaTransaccionalidad(BigDecimal ivaTransaccionalidad) {
        this.ivaTransaccionalidad = ivaTransaccionalidad;
    }

    public BigDecimal getSobreTasa() {
        return sobreTasa;
    }

    public void setSobreTasa(BigDecimal sobreTasa) {
        this.sobreTasa = sobreTasa;
    }

    public BigDecimal getIvaSobreTasa() {
        return ivaSobreTasa;
    }

    public void setIvaSobreTasa(BigDecimal ivaSobreTasa) {
        this.ivaSobreTasa = ivaSobreTasa;
    }

}
