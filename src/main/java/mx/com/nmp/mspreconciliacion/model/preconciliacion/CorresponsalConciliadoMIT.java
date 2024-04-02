package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Atributos del corresponsal para el indice mo_pagos_mit_conciliados
 */
public class CorresponsalConciliadoMIT implements Serializable {

    private static final long serialVersionUID = -2944608376274460653L;

    private String afiliacion;
    private String autorizacion;
    private String bancoEmisor;
    private String estado;
    private String estatusDevolucion;
    private Date fechaDeposito;
    private Date fechaLiquidacion;
    private Date fechaDevolucion;
    private Date fechaOperacion;
    private String tipoTarjeta;    
    private String horaOperacion;
    private String tipoPago;    
    private String idTransaccion;
    private BigDecimal importe;
    private String lote;
    private String marcaTarjeta;
    private String moneda;
    private String nombreAfiliacion;
    private String nombreArchivo;
    private String nombreTH;
    private String numeroOperacion;
    private String referencia;
    private String sucursal;
    private Integer tarjeta;
    private String tipoDevolucion;
    private String usrTrx;
    private String usuario;
    private String tipoOperacion;
    private BigDecimal sobretasa;
    private BigDecimal comisionTransaccional;
    private boolean esAMEX;
    
    public Date getFechaLiquidacion() {
    	if (this.fechaLiquidacion!= null) {
			Instant instant = this.fechaLiquidacion.toInstant();
			ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT-6"));
			ZonedDateTime truncatedZonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
			Instant truncatedInstant = truncatedZonedDateTime.toInstant();
			return Date.from(truncatedInstant);
    	}else {
    		return this.fechaLiquidacion;
    	}
    }

    public void setFechaLiquidacion(Date fechaLiquidacion) {
        this.fechaLiquidacion = fechaLiquidacion;
    }
    
    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getBancoEmisor() {
        return bancoEmisor;
    }

    public void setBancoEmisor(String bancoEmisor) {
        this.bancoEmisor = bancoEmisor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }



    public Date getFechaDeposito() {
        return fechaDeposito;
    }

    public void setFechaDeposito(Date fechaDeposito) {
        this.fechaDeposito = fechaDeposito;
    }


    public Date getFechaOperacion() {
		Instant instant = this.fechaOperacion.toInstant();
		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT-6"));
		ZonedDateTime truncatedZonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
		Instant truncatedInstant = truncatedZonedDateTime.toInstant();
		return Date.from(truncatedInstant); 
    }

    public void setFechaOperacion(Date fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public String getNombreTH() {
        return nombreTH;
    }

    public void setNombreTH(String nombreTH) {
        this.nombreTH = nombreTH;
    }    

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getMarcaTarjeta() {
        return marcaTarjeta;
    }

    public void setMarcaTarjeta(String marcaTarjeta) {
        this.marcaTarjeta = marcaTarjeta;
    }

    
    public String getHoraOperacion() {
        return horaOperacion;
    }

    public void setHoraOperacion(String horaOperacion) {
        this.horaOperacion = horaOperacion;
    }
    
    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getNombreAfiliacion() {
        return nombreAfiliacion;
    }

    public void setNombreAfiliacion(String nombreAfiliacion) {
        this.nombreAfiliacion = nombreAfiliacion;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
    
    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }    

    public Integer getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(Integer tarjeta) {
        this.tarjeta = tarjeta;
    }


    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getUsrTrx() {
        return usrTrx;
    }

    public void setUsrTrx(String usrTrx) {
        this.usrTrx = usrTrx;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

	public final Date getFechaDevolucion() {
		return fechaDevolucion;
	}

	public final void setFechaDevolucion(Date fechaDevolucion) {
		this.fechaDevolucion = fechaDevolucion;
	}
	
    public String getTipoDevolucion() {
        return tipoDevolucion;
    }

    public void setTipoDevolucion(String tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
    }	

    public String getAfiliacion() {
        return afiliacion;
    }

    public void setAfiliacion(String afiliacion) {
        this.afiliacion = afiliacion;
    }	
    
    public String getEstatusDevolucion() {
        return estatusDevolucion;
    }

    public void setEstatusDevolucion(String estatusDevolucion) {
        this.estatusDevolucion = estatusDevolucion;
    }    
    
    public String getNumeroOperacion() {
        return numeroOperacion;
    }

    public void setNumeroOperacion(String numeroOperacion) {
        this.numeroOperacion = numeroOperacion;
    }

	public BigDecimal getComisionTransaccional() {
		return comisionTransaccional;
	}

	public void setComisionTransaccional(BigDecimal comisionTransaccional) {
		this.comisionTransaccional = comisionTransaccional;
	}

	public BigDecimal getSobretasa() {
		return sobretasa;
	}

	public void setSobretasa(BigDecimal sobretasa) {
		this.sobretasa = sobretasa;
	}

	public boolean isEsAMEX() {
		return esAMEX;
	}

	public void setEsAMEX(boolean esAMEX) {
		this.esAMEX = esAMEX;
	}    
}
