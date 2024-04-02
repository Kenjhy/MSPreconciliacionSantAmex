package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Clase con los atributos del cliente dentro del indice de mo_pagos, mo_pagos_mit
 */
public class Cliente implements Serializable {

    private static final long serialVersionUID = 8612629699477037876L;

    private String clabe;
    private Date fechaUltimaTrasaccion;
    private String idCliente;
    private String nombreCliente;
    private BigDecimal saldoPendiente;
    private String tarjetaMonte;
    private Integer ultimaOperacion;

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTarjetaMonte() {
        return tarjetaMonte;
    }

    public void setTarjetaMonte(String tarjetaMonte) {
        this.tarjetaMonte = tarjetaMonte;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public Date getFechaUltimaTrasaccion() {
        return fechaUltimaTrasaccion;
    }

    public void setFechaUltimaTrasaccion(Date fechaUltimaTrasaccion) {
        this.fechaUltimaTrasaccion = fechaUltimaTrasaccion;
    }

    public Integer getUltimaOperacion() {
        return ultimaOperacion;
    }

    public void setUltimaOperacion(Integer ultimaOperacion) {
        this.ultimaOperacion = ultimaOperacion;
    }

    public String getClabe() {
        return clabe;
    }

    public void setClabe(String clabe) {
        this.clabe = clabe;
    }
}
