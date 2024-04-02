package mx.com.nmp.mspreconciliacion.consumer.rest.dto;


public class BusRestAuthDTO {

	private String user;
	private String password;


	public BusRestAuthDTO(String user, String password) {
		this.user = user;
		this.password = password;
	}


	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
