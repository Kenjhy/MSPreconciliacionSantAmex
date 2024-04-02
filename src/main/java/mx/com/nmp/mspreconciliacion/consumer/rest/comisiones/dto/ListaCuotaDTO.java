package mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto;

import java.util.List;

public class ListaCuotaDTO {

	private List<CuotaDTO> cuota;

	public List<CuotaDTO> getCuota() {
		return cuota;
	}

	public void setCuota(List<CuotaDTO> cuota) {
		this.cuota = cuota;
	} 
}
