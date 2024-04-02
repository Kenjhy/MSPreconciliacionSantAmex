package mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto;


import mx.com.nmp.mspreconciliacion.consumer.rest.dto.BusRestBodyDTO;


public class RequestComisionesMIDAsDTO implements BusRestBodyDTO  {

	private String fecha;
	private String corresponsal;
	
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getCorresponsal() {
		return corresponsal;
	}
	public void setCorresponsal(String corresponsal) {
		this.corresponsal = corresponsal;
	}

}
