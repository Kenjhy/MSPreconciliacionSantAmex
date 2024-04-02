package mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto;

import java.util.List;

public class ListaBancoDTO {
	
	private List<BancoDTO> banco;
	

	public List<BancoDTO> getBanco() {
		return banco;
	}

	public void setBanco(List<BancoDTO> banco) {
		this.banco = banco;
	}

}
