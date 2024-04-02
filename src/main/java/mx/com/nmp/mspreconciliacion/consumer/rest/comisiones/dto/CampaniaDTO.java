package mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CampaniaDTO {

	private ListaBancoDTO bancos;
	private String campania;
	private BigDecimal montoMinimo;
	private String plazo;
	private String ramo;
	private String sucursal;

	public String getCampania() {
		return campania;
	}
	public void setCampania(String campania) {
		this.campania = campania;
	}
	public BigDecimal getMontoMinimo() {
		return montoMinimo;
	}
	public void setMontoMinimo(BigDecimal montoMinimo) {
		this.montoMinimo = montoMinimo;
	}
	public String getPlazo() {
		return plazo;
	}
	public void setPlazo(String plazo) {
		this.plazo = plazo;
	}
	public String getRamo() {
		return ramo;
	}
	public void setRamo(String ramo) {
		this.ramo = ramo;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public ListaBancoDTO getBancos() {
		return bancos;
	}
	public void setBancos(ListaBancoDTO bancos) {
		this.bancos = bancos;
	}

	@JsonIgnore
	public List<BancoDTO> getListBancos(){
		return bancos != null && bancos.getBanco() != null ? bancos.getBanco() : new ArrayList<>() ;
	}
}
