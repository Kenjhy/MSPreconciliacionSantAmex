package mx.com.nmp.mspreconciliacion.model.dto;

import mx.com.nmp.mspreconciliacion.model.enums.EstadoEjecucionEnum;

public class RequestEjecucionDTO {

    private Long idEjecucion;

    private EstadoEjecucionEnum estatus;

    public RequestEjecucionDTO() {
        super();
    }

    public RequestEjecucionDTO(Long idEjecucion, EstadoEjecucionEnum estatus) {
        super();
        this.idEjecucion = idEjecucion;
        this.estatus = estatus;
    }

    public Long getIdEjecucion() {
        return idEjecucion;
    }

    public void setIdEjecucion(Long idEjecucion) {
        this.idEjecucion = idEjecucion;
    }

    public EstadoEjecucionEnum getEstatus() {
        return estatus;
    }

    public void setEstatus(EstadoEjecucionEnum estatus) {
        this.estatus = estatus;
    }
}
