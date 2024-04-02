package mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class ResponseComisionesMIDAsDTO {

	private ListaCampaniaDTO campanias;
	private ListaCuotaDTO cuotas;
	private String fecha;



	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public ListaCampaniaDTO getCampanias() {
		return campanias;
	}
	public void setCampanias(ListaCampaniaDTO campanias) {
		this.campanias = campanias;
	}
	public ListaCuotaDTO getCuotas() {
		return cuotas;
	}
	public void setCuotas(ListaCuotaDTO cuotas) {
		this.cuotas = cuotas;
	}

	@JsonIgnore
	public List<CuotaDTO> getListCuotas(){
		return cuotas != null && cuotas.getCuota() != null ? cuotas.getCuota() : new ArrayList<>();
	}

	@JsonIgnore
	public List<CampaniaDTO> getListCampanias(){
		return campanias != null && campanias.getCampania() != null ? campanias.getCampania() : new ArrayList<>();
	}

}
