package mx.com.nmp.mspreconciliacion.model.dto;


import java.math.BigDecimal;
import java.util.Date;

public class ResponseDevolucionesDTO {

    private String sucursal;
    private String tipoDevolucion;
    private Date fechaTransaccion;
    private String entidad;
    private String tipoTarjeta;
    private String afiliacion;
    private String tarjeta;
    private String autorizacion;    
    private String numeroOperacion;
    private String montoTransaccion;
    private BigDecimal importeDevolucion;
    private String estatus;
    private Date fechaCargoBancario;
    private Date fechaDevolucion;
    private String corresponsal;
    
    private String idPago; //id Indice
    private Integer estado;
    
    public String getAfiliacion() {
        return afiliacion;
    }

    public void setAfiliacion(String afiliacion) {
        this.afiliacion = afiliacion;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getNumeroOperacion() {
        return numeroOperacion;
    }

    public void setNumeroOperacion(String numeroOperacion) {
        this.numeroOperacion = numeroOperacion;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getTipoDevolucion() {
        return tipoDevolucion;
    }

    public void setTipoDevolucion(String tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
    }

    public Date getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(Date fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getMontoTransaccion() {
        return montoTransaccion;
    }

    public void setMontoTransaccion(String montoTransaccion) {
        this.montoTransaccion = montoTransaccion;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

	public BigDecimal getImporteDevolucion() {
		return importeDevolucion;
	}

	public void setImporteDevolucion(BigDecimal importeDevolucion) {
		this.importeDevolucion = importeDevolucion;
	}

	public Date getFechaCargoBancario() {
		return fechaCargoBancario;
	}

	public void setFechaCargoBancario(Date fechaCargoBancario) {
		this.fechaCargoBancario = fechaCargoBancario;
	}

	public String getCorresponsal() {
		return corresponsal;
	}

	public void setCorresponsal(String corresponsal) {
		this.corresponsal = corresponsal;
	}

	public String getIdPago() {
		return idPago;
	}

	public void setIdPago(String idPago) {
		this.idPago = idPago;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}


}
