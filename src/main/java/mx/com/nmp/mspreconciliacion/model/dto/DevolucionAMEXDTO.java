package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.Date;

public class DevolucionAMEXDTO {

	private Date fechaOperacion;
	private String tipoDevolucion;
	
	public Date getFechaOperacion() {
		return fechaOperacion;
	}
	public void setFechaOperacion(Date fechaOperacion) {
		this.fechaOperacion = fechaOperacion;
	}
	public String getTipoDevolucion() {
		return tipoDevolucion;
	}
	public void setTipoDevolucion(String tipoDevolucion) {
		this.tipoDevolucion = tipoDevolucion;
	}
}
