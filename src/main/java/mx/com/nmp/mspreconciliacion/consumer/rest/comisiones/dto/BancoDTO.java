package mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto;

import java.math.BigDecimal;

public class BancoDTO {

	private String banco;
	private BigDecimal sobretasa;
	
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	public BigDecimal getSobretasa() {
		return sobretasa;
	}
	public void setSobretasa(BigDecimal sobretasa) {
		this.sobretasa = sobretasa;
	}
	
}
