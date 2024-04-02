package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.Date;

public class RequestTotalDevolucionesDTO {

    private String corresponsal;
    private Date fechaDesde;
    private Date fechaHasta;

    public String getCorresponsal() {
        return corresponsal;
    }

    public void setCorresponsal(String corresponsal) {
        this.corresponsal = corresponsal;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
}
