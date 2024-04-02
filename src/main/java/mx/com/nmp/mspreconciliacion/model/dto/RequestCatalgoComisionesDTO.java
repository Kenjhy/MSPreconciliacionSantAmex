package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RequestCatalgoComisionesDTO {
	
	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date fechaOperacion;

	public Date getFechaOperacion() {
		return fechaOperacion;
	}

	public void setFechaOperacion(Date fechaOperacion) {
		this.fechaOperacion = fechaOperacion;
	}
	
	
}
