package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Atributos del core para el indice mo_pagos_mit_conciliados
 */
public class CoreConciliadoMIT implements Serializable {

    private static final long serialVersionUID = -7038750881477172436L;

    private Integer cliente;
    private Integer contrato;
    private Date fechaAplicacion;
    private Date fechaOperacion;
    private String idTransaccion;
    private String moneda;
    private BigDecimal montoTotal;

    public CoreConciliadoMIT() {
    }

    public CoreConciliadoMIT(Cliente cliente, Integer contrato, Date fechaAplicacion, Date fechaOperacion, String idTransaccion, String moneda, BigDecimal montoTotal) {
        this.cliente = cliente != null && StringUtils.hasText(cliente.getIdCliente()) ? Integer.parseInt(cliente.getIdCliente()) : 0 ;
        this.contrato = contrato;
        this.fechaAplicacion = fechaAplicacion;
        this.fechaOperacion = fechaOperacion;
        this.idTransaccion = idTransaccion;
        this.moneda = moneda;
        this.montoTotal = montoTotal;
    }

    public Integer getCliente() {
        return cliente;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }

    public Integer getContrato() {
        return contrato;
    }

    public void setContrato(Integer contrato) {
        this.contrato = contrato;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public Date getFechaOperacion() {
        return fechaOperacion;
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

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}
