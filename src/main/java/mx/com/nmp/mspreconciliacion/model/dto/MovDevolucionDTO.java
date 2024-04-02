package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.Date;

public class MovDevolucionDTO {

	private String idPago; //indice idPago
	private Date fechaCargoBancario;
	private Boolean liquidar;
	
	

	public final String getIdPago() {
		return idPago;
	}
	public final void setIdPago(String idPago) {
		this.idPago = idPago;
	}
	public Boolean getLiquidar() {
		return liquidar;
	}
	public void setLiquidar(Boolean liquidar) {
		this.liquidar = liquidar;
	}
	public final Date getFechaCargoBancario() {
		return fechaCargoBancario;
	}
	public final void setFechaCargoBancario(Date fechaCargoBancario) {
		this.fechaCargoBancario = fechaCargoBancario;
	}
}
