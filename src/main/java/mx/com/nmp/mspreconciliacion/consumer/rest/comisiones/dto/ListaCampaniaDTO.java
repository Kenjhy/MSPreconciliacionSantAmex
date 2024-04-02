package mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto;

import java.util.List;


public class ListaCampaniaDTO {
	
	private List<CampaniaDTO> campania;

	public List<CampaniaDTO> getCampania() {
		return campania;
	}

	public void setCampania(List<CampaniaDTO> campania) {
		this.campania = campania;
	}
}
