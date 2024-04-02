package mx.com.nmp.mspreconciliacion.model.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/***
 * 
 * @author QuarkSoft
 * Clase que se utiliza para mapear el resultado del WS-Centro de Pagos de las transacciones
 * de los corresponsales Santander/Amex.
 */

public class RequestCentroPagosDTO {

	private String user;
	private String pwd;
	
	@JacksonXmlProperty(localName = "id_company")
	private String company;
	private String date;
	
	@JacksonXmlProperty(localName = "id_branch")
	private String branch;
	private String reference;
	
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}

	public final String getCompany() {
		return company;
	}
	public final void setCompany(String company) {
		this.company = company;
	}

	public final String getBranch() {
		return branch;
	}
	public final void setBranch(String branch) {
		this.branch = branch;
	}
	
}

