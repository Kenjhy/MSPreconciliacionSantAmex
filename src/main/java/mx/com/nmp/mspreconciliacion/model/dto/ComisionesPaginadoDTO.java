package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.List;

public class ComisionesPaginadoDTO {

	private int totalRegistros;
	private int numeroPagina;
	private int numeroRegistros;
	private boolean tieneMasPaginas;
	private List<ComisionesDTO> comisionesList;

	private MontosComisionesDTO comisionPaguinado;

	private MontosComisionesDTO comisionTotal;

	public ComisionesPaginadoDTO(){
		comisionPaguinado = new MontosComisionesDTO();
		comisionTotal = new MontosComisionesDTO();
	}
	

	public int getNumeroPagina() {
		return numeroPagina;
	}
	public void setNumeroPagina(int numeroPagina) {
		this.numeroPagina = numeroPagina;
	}

	public List<ComisionesDTO> getComisionesList() {
		return comisionesList;
	}
	public void setComisionesList(List<ComisionesDTO> comisionesList) {
		this.comisionesList = comisionesList;
	}
	
	public int getTotalRegistros() {
		return totalRegistros;
	}
	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
	

	public MontosComisionesDTO getComisionPaguinado() {
		return comisionPaguinado;
	}

	public void setComisionPaguinado(MontosComisionesDTO comisionPaguinado) {
		this.comisionPaguinado = comisionPaguinado;
	}

	public MontosComisionesDTO getComisionTotal() {
		return comisionTotal;
	}

	public void setComisionTotal(MontosComisionesDTO comisionTotal) {
		this.comisionTotal = comisionTotal;
	}
	
	public int getNumeroRegistros() {
		return numeroRegistros;
	}
	public void setNumeroRegistros(int numeroRegistros) {
		this.numeroRegistros = numeroRegistros;
	}
	
	public boolean isTieneMasPaginas() {
		return tieneMasPaginas;
	}
	public void setTieneMasPaginas(boolean tieneMasPaginas) {
		this.tieneMasPaginas = tieneMasPaginas;
	}	
}
