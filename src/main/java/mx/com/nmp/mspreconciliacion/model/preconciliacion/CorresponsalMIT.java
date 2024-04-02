package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Field;



/**
 * Clase con los atributos del corresponsal dentro del indice mo_pagos_mit
 */
public class CorresponsalMIT implements Serializable {

	private static final long serialVersionUID = -2787292364095358839L;
	
	
	@Field(name = "getCc_Number")
	private String ccNumber;
	
	@Field(name = "getCc_Type")
    private String ccType;
	
	@Field(name = "getRspAuth")
	private String rspAuth;
	
	@Field(name = "getRspCdResponse")
	private String rspCdResponse;
	
	@Field(name = "getRspDate")
	private Date rspDate;
	
	@Field(name = "getRspDsAdress")
	private String rspDsAdress;
	
	@Field(name = "getRspDsCompany")
	private String rspDsCompany;
	
	@Field(name = "getRspDsMerchant")
    private String rspDsMerchant;
	
	@Field(name = "getRspDsOperationType")
	private String rspDsOperationType;
	
	@Field(name = "getRspDsResponse")
	private String rspDsResponse;
	
	@Field(name = "getRspOperationNumber")
	private String rspOperationNumber;
	
	@Field(name = "getRspTime")
    private String rspTime;
	
	@Field(name = "getRspVoucher")
	private String rspVoucher;

	@Field(name = "getRspVoucherCliente")
	private String rspVoucherCliente;
	
	@Field(name = "getRspVoucherComercio")
	private String rspVoucherComercio;
	
	@Field(name = "getRspXML")
	private String rspXML;
	
	@Field(name = "getTx_Amount")
	private String txAmount;

	@Field(name = "getTx_Reference")
	private String txReference;


	public String getCcNumber() {
		return ccNumber;
	}

	public void setCcNumber(String ccNumber) {
		this.ccNumber = ccNumber;
	}

	public String getCcType() {
		return ccType;
	}

	public void setCcType(String ccType) {
		this.ccType = ccType;
	}

	public String getRspAuth() {
		return rspAuth;
	}

	public void setRspAuth(String rspAuth) {
		this.rspAuth = rspAuth;
	}

	public String getRspCdResponse() {
		return rspCdResponse;
	}

	public void setRspCdResponse(String rspCdResponse) {
		this.rspCdResponse = rspCdResponse;
	}

	public Date getRspDate() {
		Instant instant = this.rspDate.toInstant();
		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT-6"));
		ZonedDateTime truncatedZonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
		Instant truncatedInstant = truncatedZonedDateTime.toInstant();
		return Date.from(truncatedInstant); 		
	}

	public void setRspDate(Date rspDate) {
		this.rspDate = rspDate;
	}

	public String getRspDsAdress() {
		return rspDsAdress;
	}

	public void setRspDsAdress(String rspDsAdress) {
		this.rspDsAdress = rspDsAdress;
	}

	public String getRspDsCompany() {
		return rspDsCompany;
	}

	public void setRspDsCompany(String rspDsCompany) {
		this.rspDsCompany = rspDsCompany;
	}

	public String getRspDsMerchant() {
		return rspDsMerchant;
	}

	public void setRspDsMerchant(String rspDsMerchant) {
		this.rspDsMerchant = rspDsMerchant;
	}

	public String getRspDsOperationType() {
		return rspDsOperationType;
	}

	public void setRspDsOperationType(String rspDsOperationType) {
		this.rspDsOperationType = rspDsOperationType;
	}

	public String getRspDsResponse() {
		return rspDsResponse;
	}

	public void setRspDsResponse(String rspDsResponse) {
		this.rspDsResponse = rspDsResponse;
	}

	public String getRspOperationNumber() {
		return rspOperationNumber;
	}

	public void setRspOperationNumber(String rspOperationNumber) {
		this.rspOperationNumber = rspOperationNumber;
	}

	public String getRspTime() {
		return rspTime;
	}

	public void setRspTime(String rspTime) {
		this.rspTime = rspTime;
	}

	public String getRspVoucher() {
		return rspVoucher;
	}

	public void setRspVoucher(String rspVoucher) {
		this.rspVoucher = rspVoucher;
	}

	public String getRspVoucherCliente() {
		return rspVoucherCliente;
	}

	public void setRspVoucherCliente(String rspVoucherCliente) {
		this.rspVoucherCliente = rspVoucherCliente;
	}

	public String getRspVoucherComercio() {
		return rspVoucherComercio;
	}

	public void setRspVoucherComercio(String rspVoucherComercio) {
		this.rspVoucherComercio = rspVoucherComercio;
	}

	public String getRspXML() {
		return rspXML;
	}

	public void setRspXML(String rspXML) {
		this.rspXML = rspXML;
	}

	public String getTxAmount() {
		return txAmount;
	}

	public void setTxAmount(String txAmount) {
		this.txAmount = txAmount;
	}

	public String getTxReference() {
		return txReference;
	}

	public void setTxReference(String txReference) {
		this.txReference = txReference;
	}
}
