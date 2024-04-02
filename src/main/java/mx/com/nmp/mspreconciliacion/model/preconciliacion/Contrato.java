package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import java.io.Serializable;

/**
 * Atributos del contrato de la clase partida para el indice mo_pagos
 */
public class Contrato implements Serializable {

    private static final long serialVersionUID = -6070740625644683416L;

    private String codigo;
    private String descripcion;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
