package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.List;

public class DevolucionPaginadoDTO {

	private int totalRegistros;
	private int numeroPagina;
	private int numeroRegistros;
	private boolean tieneMasPaginas;
	private List<ResponseDevolucionesDTO> devolucionesList;
	
	public int getNumeroRegistros() {
		return numeroRegistros;
	}
	public void setNumeroRegistros(int numeroRegistros) {
		this.numeroRegistros = numeroRegistros;
	}
	
	public List<ResponseDevolucionesDTO> getDevolucionesList() {
		return devolucionesList;
	}
	public void setDevolucionesList(List<ResponseDevolucionesDTO> devolucionesList) {
		this.devolucionesList = devolucionesList;
	}
	
	public boolean isTieneMasPaginas() {
		return tieneMasPaginas;
	}
	public void setTieneMasPaginas(boolean tieneMasPaginas) {
		this.tieneMasPaginas = tieneMasPaginas;
	}
	
	public int getTotalRegistros() {
		return totalRegistros;
	}
	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
	public int getNumeroPagina() {
		return numeroPagina;
	}
	public void setNumeroPagina(int numeroPagina) {
		this.numeroPagina = numeroPagina;
	}

}
