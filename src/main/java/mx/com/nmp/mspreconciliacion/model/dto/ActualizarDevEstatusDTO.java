package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.List;

public class ActualizarDevEstatusDTO {

	List<ResponseDevolucionesDTO> devoluciones;
	
	public List<ResponseDevolucionesDTO> getDevoluciones() {
		return devoluciones;
	}
	public void setDevoluciones(List<ResponseDevolucionesDTO> devoluciones) {
		this.devoluciones = devoluciones;
	}
	
}
