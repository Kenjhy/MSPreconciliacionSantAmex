package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Atributos del corresponsal para el indice mm_pagos_conciliados_mit_epa
 */
public class CorresponsalConciliadoEPA implements Serializable {

    private static final long serialVersionUID = -5822581164811958695L;

    private String afiliacion;
    private String autorizacion;
    private String bancoEmisor;
    private String tipoDevolucion;    
    private BigDecimal comisionTransaccional;
    private BigDecimal comisionTransaccionalEPA;
    private String establecimiento;
    private String estado;
    private String estatusDevolucion;
    private Date fechaDeposito;
    private Date fechaLiquidacion;
    private Date fechaOperacion;
    private Date fechaDevolucion;
    private String horaOperacion;
    private String idTransaccion;
    private String tipoPago;    
    private BigDecimal importe;
    private BigDecimal importeBruto;
    private String tipoOperacion;
    private BigDecimal importeNeto;
    private BigDecimal importeDevolucion;
    private float ivaSobretasa;
    private float ivaTransaccional;
    private String lote;
    private String marcaTarjeta;
    private String moneda;
    private String nombreAfiliacion;
    private String nombreArchivo;
    private String nombreTH;
    private String numeroMesesPromocion;
    private String numeroOperacion;
    private String referencia;
    private float sobretasa;
    private String sucursal;
    private float sumaSobretasa;
    private String tarjeta;
    private String tipoTarjeta;
    private String usrTrx;
    private String usuario;
    private Boolean esSobreCargo;

    public String getHoraOperacion() {
        return horaOperacion;
    }

    public void setHoraOperacion(String horaOperacion) {
        this.horaOperacion = horaOperacion;
    }
    
    public String getAfiliacion() {
        return afiliacion;
    }

    public void setAfiliacion(String afiliacion) {
        this.afiliacion = afiliacion;
    }
    
    public Date getFechaDeposito() {
        return fechaDeposito;
    }

    public void setFechaDeposito(Date fechaDeposito) {
        this.fechaDeposito = fechaDeposito;
    }
    


    public BigDecimal getComisionTransaccional() {
        return comisionTransaccional;
    }

    public void setComisionTransaccional(BigDecimal comisionTransaccional) {
        this.comisionTransaccional = comisionTransaccional;
    }

    public String getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(String establecimiento) {
        this.establecimiento = establecimiento;
    }


    public String getEstatusDevolucion() {
        return estatusDevolucion;
    }

    public void setEstatusDevolucion(String estatusDevolucion) {
        this.estatusDevolucion = estatusDevolucion;
    }

    public BigDecimal getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
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


    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }


    public BigDecimal getImporteBruto() {
        return importeBruto;
    }

    public void setImporteBruto(BigDecimal importeBruto) {
        this.importeBruto = importeBruto;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }    

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    
    public float getIvaSobretasa() {
        return ivaSobretasa;
    }

    public void setIvaSobretasa(float ivaSobretasa) {
        this.ivaSobretasa = ivaSobretasa;
    }

    public float getIvaTransaccional() {
        return ivaTransaccional;
    }

    public void setIvaTransaccional(float ivaTransaccional) {
        this.ivaTransaccional = ivaTransaccional;
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
    
    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }    

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreTH() {
        return nombreTH;
    }

    public void setNombreTH(String nombreTH) {
        this.nombreTH = nombreTH;
    }

    public String getNumeroMesesPromocion() {
        return numeroMesesPromocion;
    }

    public void setNumeroMesesPromocion(String numeroMesesPromocion) {
        this.numeroMesesPromocion = numeroMesesPromocion;
    }

    public String getNumeroOperacion() {
        return numeroOperacion;
    }

    public void setNumeroOperacion(String numeroOperacion) {
        this.numeroOperacion = numeroOperacion;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public float getSobretasa() {
        return sobretasa;
    }

    public void setSobretasa(float sobretasa) {
        this.sobretasa = sobretasa;
    }

    public String getTipoDevolucion() {
        return tipoDevolucion;
    }

    public void setTipoDevolucion(String tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public float getSumaSobretasa() {
        return sumaSobretasa;
    }

    public void setSumaSobretasa(float sumaSobretasa) {
        this.sumaSobretasa = sumaSobretasa;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }


    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
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

    public String getBancoEmisor() {
        return bancoEmisor;
    }

    public void setBancoEmisor(String bancoEmisor) {
        this.bancoEmisor = bancoEmisor;
    }

    public String getNombreAfiliacion() {
        return nombreAfiliacion;
    }

    public void setNombreAfiliacion(String nombreAfiliacion) {
        this.nombreAfiliacion = nombreAfiliacion;
    }
    
    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }    

	public Date getFechaDevolucion() {
		return fechaDevolucion;
	}

	public void setFechaDevolucion(Date fechaDevolucion) {
		this.fechaDevolucion = fechaDevolucion;
	}

	public BigDecimal getImporteDevolucion() {
		return importeDevolucion;
	}

	public void setImporteDevolucion(BigDecimal importeDevolucion) {
		this.importeDevolucion = importeDevolucion;
	}

	public BigDecimal getComisionTransaccionalEPA() {
		return comisionTransaccionalEPA;
	}

	public void setComisionTransaccionalEPA(BigDecimal comisionTransaccionalEPA) {
		this.comisionTransaccionalEPA = comisionTransaccionalEPA;
	}
	
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }	
    
    public Date getFechaLiquidacion() {
    	if (this.fechaLiquidacion != null) {
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
    
    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

	public Boolean getEsSobreCargo() {
		return esSobreCargo;
	}

	public void setEsSobreCargo(Boolean esSobreCargo) {
		this.esSobreCargo = esSobreCargo;
	}    
}
