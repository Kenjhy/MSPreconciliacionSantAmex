package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.List;

public class ActualizarDevolucionDTO {

	private String corresponsal;
	private Boolean procesoAutomatico;
	private List<MovDevolucionDTO> devolucionesLiquidar;
	
	public String getCorresponsal() {
		return corresponsal;
	}
	public void setCorresponsal(String corresponsal) {
		this.corresponsal = corresponsal;
	}
	public final Boolean getProcesoAutomatico() {
		return procesoAutomatico;
	}
	public final void setProcesoAutomatico(Boolean procesoAutomatico) {
		this.procesoAutomatico = procesoAutomatico;
	}	
	
	public final List<MovDevolucionDTO> getDevolucionesLiquidar() {
		return devolucionesLiquidar;
	}
	public final void setDevolucionesLiquidar(List<MovDevolucionDTO> devolucionesLiquidar) {
		this.devolucionesLiquidar = devolucionesLiquidar;
	}
	
}
