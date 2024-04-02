package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Clase con los atributos de la partida dentro del indice de mo_pagos
 */
public class Partida implements Serializable {

    private static final long serialVersionUID = 8982623810816998792L;

    private String concepto;
    private Contrato contrato;
    private Integer idOperacion;
    private Integer idPartida;
    private BigDecimal importe;
    private BigDecimal importeMaximo;
    private BigDecimal importeMinimo;
    private BigDecimal importeTope;
    private int numeroSucursalPartida;
    private String operacion;
    private String tipoContrato;
    
    private String sucursalOperacion;
    private String operacionAbr;
    private String estadoPrenda;
    private String estadoCaja;
    private String ramo;
    private String subramo;
    private Date fechaIngresoDeposito;
    private Date fechaComercializacion;
    
    

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Integer getIdOperacion() {
        return idOperacion;
    }

    public void setIdOperacion(Integer idOperacion) {
        this.idOperacion = idOperacion;
    }

    public Integer getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(Integer idPartida) {
        this.idPartida = idPartida;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getImporteMaximo() {
        return importeMaximo;
    }

    public void setImporteMaximo(BigDecimal importeMaximo) {
        this.importeMaximo = importeMaximo;
    }

    public BigDecimal getImporteMinimo() {
        return importeMinimo;
    }

    public void setImporteMinimo(BigDecimal importeMinimo) {
        this.importeMinimo = importeMinimo;
    }

    public BigDecimal getImporteTope() {
        return importeTope;
    }

    public void setImporteTope(BigDecimal importeTope) {
        this.importeTope = importeTope;
    }

    public int getNumeroSucursalPartida() {
        return numeroSucursalPartida;
    }

    public void setNumeroSucursalPartida(int numeroSucursalPartida) {
        this.numeroSucursalPartida = numeroSucursalPartida;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

	public String getSucursalOperacion() {
		return sucursalOperacion;
	}

	public void setSucursalOperacion(String sucursalOperacion) {
		this.sucursalOperacion = sucursalOperacion;
	}

	public String getOperacionAbr() {
		return operacionAbr;
	}

	public void setOperacionAbr(String operacionAbr) {
		this.operacionAbr = operacionAbr;
	}

	public String getEstadoPrenda() {
		return estadoPrenda;
	}

	public void setEstadoPrenda(String estadoPrenda) {
		this.estadoPrenda = estadoPrenda;
	}

	public String getEstadoCaja() {
		return estadoCaja;
	}

	public void setEstadoCaja(String estadoCaja) {
		this.estadoCaja = estadoCaja;
	}

	public String getRamo() {
		return ramo;
	}

	public void setRamo(String ramo) {
		this.ramo = ramo;
	}

	public String getSubramo() {
		return subramo;
	}

	public void setSubramo(String subramo) {
		this.subramo = subramo;
	}

	public Date getFechaIngresoDeposito() {
		return fechaIngresoDeposito;
	}

	public void setFechaIngresoDeposito(Date fechaIngresoDeposito) {
		this.fechaIngresoDeposito = fechaIngresoDeposito;
	}

	public Date getFechaComercializacion() {
		return fechaComercializacion;
	}

	public void setFechaComercializacion(Date fechaComercializacion) {
		this.fechaComercializacion = fechaComercializacion;
	}
}
