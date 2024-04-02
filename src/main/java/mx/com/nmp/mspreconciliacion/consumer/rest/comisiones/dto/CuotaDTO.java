package mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto;

import java.math.BigDecimal;

public class CuotaDTO {

	private BigDecimal comision;
	private String tipo;
	private String tarjeta;
	
	public BigDecimal getComision() {
		return comision;
	}
	public void setComision(BigDecimal comision) {
		this.comision = comision;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getTarjeta() {
		return tarjeta;
	}
	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}

}
