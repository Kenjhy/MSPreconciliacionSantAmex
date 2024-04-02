package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.List;

public class CatalogoComisionesDTO {

	
	private List<String> banco ;
    private List<String> sucursal;
    
	public List<String> getBanco() {
		return banco;
	}
	public void setBanco(List<String> banco) {
		this.banco = banco;
	}
	public List<String> getSucursal() {
		return sucursal;
	}
	public void setSucursal(List<String> sucursal) {
		this.sucursal = sucursal;
	}
    
    
}
