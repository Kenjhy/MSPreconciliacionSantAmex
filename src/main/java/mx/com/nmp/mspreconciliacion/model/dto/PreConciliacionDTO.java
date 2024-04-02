package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.Date;

public class PreConciliacionDTO {

	private Date fecha;
	private Long idProcesoCron;
	private int numeroReintento;
	private boolean actualizarProcesoCron;
	private boolean reprocesamiento;

	public final Date getFecha() {
		return fecha;
	}
	public final void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public final Long getIdProcesoCron() {
		return idProcesoCron;
	}
	public final void setIdProcesoCron(Long idProcesoCron) {
		this.idProcesoCron = idProcesoCron;
	}
	public final int getNumeroReintento() {
		return numeroReintento;
	}
	public final void setNumeroReintento(int numeroReintento) {
		this.numeroReintento = numeroReintento;
	}
	public boolean isActualizarProcesoCron() {
		return actualizarProcesoCron;
	}
	public void setActualizarProcesoCron(boolean actualizarProcesoCron) {
		this.actualizarProcesoCron = actualizarProcesoCron;
	}
	public boolean isReprocesamiento() {
		return reprocesamiento;
	}
	public void setReprocesamiento(boolean reprocesamiento) {
		this.reprocesamiento = reprocesamiento;
	}
}

