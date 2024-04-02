package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Clase con los atributos del corresponsal dentro del indice de mo_pagos, mo_pagos_mit
 */
public class Corresponsal implements Serializable {

    private static final long serialVersionUID = -7545556281690156896L;

    private String account;
    private Date adminDate;
    private BigDecimal amount;
    private String auth;
    private String cashMachine;
    private String cashierld;
    private String client;
    private String code;
    private String descripcion;
    private String entryMode;
    private String errrDesc;
    private String folio;
    private String messageTicket;
    private String nombre;
    private String partial;
    private String store;
    private String ticket;
    private String token;
    private Date tranDate;

	private String tipoTarjeta;
    private String tipoPago;
    private String bancoEmisor;
    private BigDecimal comisionTransaccional;
    private BigDecimal sobreTasa;
    private String regionSucursalOperacion;
    
    public String getTipoTarjeta() {
		return tipoTarjeta;
	}

	public void setTipoTarjeta(String tipoTarjeta) {
		this.tipoTarjeta = tipoTarjeta;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}

	public String getBancoEmisor() {
		return bancoEmisor;
	}

	public void setBancoEmisor(String bancoEmisor) {
		this.bancoEmisor = bancoEmisor;
	}

	public BigDecimal getComisionTransaccional() {
		return comisionTransaccional;
	}

	public void setComisionTransaccional(BigDecimal comisionTransaccional) {
		this.comisionTransaccional = comisionTransaccional;
	}

	public BigDecimal getSobreTasa() {
		return sobreTasa;
	}

	public void setSobreTasa(BigDecimal sobreTasa) {
		this.sobreTasa = sobreTasa;
	}

	public String getRegionSucursalOperacion() {
		return regionSucursalOperacion;
	}

	public void setRegionSucursalOperacion(String regionSucursalOperacion) {
		this.regionSucursalOperacion = regionSucursalOperacion;
	}

    
    public Corresponsal() {
    }

    public Corresponsal(Date adminDate, BigDecimal amount, String auth) {
        this.adminDate = adminDate;
        this.amount = amount;
        this.auth = auth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Date getTranDate() {
		Instant instant = this.tranDate.toInstant();
		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT-6"));
		ZonedDateTime truncatedZonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
		Instant truncatedInstant = truncatedZonedDateTime.toInstant();
		return Date.from(truncatedInstant); 
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getCashMachine() {
        return cashMachine;
    }

    public void setCashMachine(String cashMachine) {
        this.cashMachine = cashMachine;
    }

    public String getEntryMode() {
        return entryMode;
    }

    public void setEntryMode(String entryMode) {
        this.entryMode = entryMode;
    }

    public String getCashierld() {
        return cashierld;
    }

    public void setCashierld(String cashierld) {
        this.cashierld = cashierld;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Date getAdminDate() {
        return adminDate;
    }

    public void setAdminDate(Date adminDate) {
        this.adminDate = adminDate;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getPartial() {
        return partial;
    }

    public void setPartial(String partial) {
        this.partial = partial;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMessageTicket() {
        return messageTicket;
    }

    public void setMessageTicket(String messageTicket) {
        this.messageTicket = messageTicket;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrrDesc() {
        return errrDesc;
    }

    public void setErrrDesc(String errrDesc) {
        this.errrDesc = errrDesc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
