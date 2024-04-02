package mx.com.nmp.mspreconciliacion.consumer.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusRestTokenReponseDTO{


	@JsonProperty("expires_in")	
	private String expiresIn;
	
	@JsonProperty("token_type")
	private String tokenType;
	
	@JsonProperty("refresh_token")
	private String refreshToken;
	
	@JsonProperty("access_token")
	private String accessToken;
	

	public BusRestTokenReponseDTO() {
		super();
	}
	

	public BusRestTokenReponseDTO(String expiresIn, String tokenType, String refreshToken, String accessToken) {
		super();
		this.expiresIn = expiresIn;
		this.tokenType = tokenType;
		this.refreshToken = refreshToken;
		this.accessToken = accessToken;
	}



	public String getExpiresIn() {
		return expiresIn;
	}


	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getTokenType() {
		return tokenType;
	}


	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}


	public String getRefreshToken() {
		return refreshToken;
	}


	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}


	public String getAccessToken() {
		return accessToken;
	}


	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public String toString() {
		return "BusRestTokenReponseDTO [expires_in=" + expiresIn + ", token_type=" + tokenType + ", refresh_token="
				+ refreshToken + ", access_token=" + accessToken + "]";
	}

}
