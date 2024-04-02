package mx.com.nmp.mspreconciliacion.model.preconciliacion;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Clase con los atributos de la devolución dentro del indice de mo_pagos_mit
 */
public class Devolucion implements Serializable{

    private static final long serialVersionUID = -3344626456865905128L;

    private String fechaDevolucion;
    private Date fechaCargoBancario;
    private Integer estadoDevolucion; //2-SOLICITADA, 3-LIQUIDADA

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

	public final Date getFechaCargoBancario() {
		if (this.fechaCargoBancario != null) {
			Instant instant = this.fechaCargoBancario.toInstant();
			ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT-6"));
			ZonedDateTime truncatedZonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
			Instant truncatedInstant = truncatedZonedDateTime.toInstant();
			return Date.from(truncatedInstant); 	
		}else
			return this.fechaCargoBancario;
	}

	public final void setFechaCargoBancario(Date fechaCargoBancario) {
		this.fechaCargoBancario = fechaCargoBancario;
	}

	public final Integer getEstadoDevolucion() {
		return estadoDevolucion;
	}

	public final void setEstadoDevolucion(Integer estadoDevolucion) {
		this.estadoDevolucion = estadoDevolucion;
	}
}
