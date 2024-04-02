package mx.com.nmp.mspreconciliacion.model.enums;

/**
 * @name EstadoEjecucionEnum
 *
 * @description Enum que contiene los estados de ejecución del proceso de pre-conciliación
 * @author Quarksoft
 * @version 1.0
 * @creationDate 23/11/2022
 */
public enum EstadoEjecucionEnum {

    SOLICITUD (1, "SOLICITUD"),
    DESCARGACORRECTA (2, "DESCARGA CORRECTA"),
    DESCARGAINCORRECTA (3, "DESCARGA INCORRECTA"),;

    private Integer idEstadoEjecucion;
    private String estadoEjecucion;

    EstadoEjecucionEnum(Integer idEstadoEjecucion, String estadoEjecucion) {
        this.idEstadoEjecucion = idEstadoEjecucion;
        this.estadoEjecucion = estadoEjecucion;
    }

    public Integer getIdEstadoEjecucion() {
        return idEstadoEjecucion;
    }

    public String getEstadoEjecucion() {
        return estadoEjecucion;
    }
}
