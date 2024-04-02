package mx.com.nmp.mspreconciliacion.consumer.rest.dto;

public class BusRestHeaderDTO {

	private String bearerToken;

	public BusRestHeaderDTO(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	public String getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

}
