package mx.com.nmp.mspreconciliacion.model.dto;

import java.math.BigDecimal;

public class CorresponsalDTO {

    private Long afiliacion;
    private Long autorizacion;
    private BigDecimal importe;
    private Long numeroOperacion;
    private String sucursal;
    private Long tarjeta;
    private String tipoTarjeta;
    private Long fechaOperacion;
    private String bancoEmisor;
    private String estado;

    public Long getAfiliacion() {
        return afiliacion;
    }

    public void setAfiliacion(Long afiliacion) {
        this.afiliacion = afiliacion;
    }

    public Long getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(Long autorizacion) {
        this.autorizacion = autorizacion;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Long getNumeroOperacion() {
        return numeroOperacion;
    }

    public void setNumeroOperacion(Long numeroOperacion) {
        this.numeroOperacion = numeroOperacion;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Long getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(Long tarjeta) {
        this.tarjeta = tarjeta;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getBancoEmisor() {
        return bancoEmisor;
    }

    public void setBancoEmisor(String bancoEmisor) {
        this.bancoEmisor = bancoEmisor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(Long fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }
}
