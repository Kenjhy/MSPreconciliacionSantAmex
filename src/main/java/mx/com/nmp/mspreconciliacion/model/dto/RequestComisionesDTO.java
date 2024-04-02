package mx.com.nmp.mspreconciliacion.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class RequestComisionesDTO {
	
	private String sucursal;
	private String banco; // AMEX /VISA/MASTERCARD
	private String tipoTarjeta; //CREDITO/DEBITO 
	private String tipoPago;//MSI/CONTADO

	@NotEmpty(message = "El corresponsal es requerido")
	private String corresponsal; //MIT AMEX/ MIT Santander

	@NotNull(message = "La fecha es requerida")
	private Date fecha;
	
    //Paginado
	private Boolean requierePaginado;
    private int numeroPagina;
    private int numeroRegistros;
    

	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
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
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public int getNumeroPagina() {
		return numeroPagina;
	}
	public void setNumeroPagina(int numeroPagina) {
		this.numeroPagina = numeroPagina;
	}
	public int getNumeroRegistros() {
		return numeroRegistros;
	}
	public void setNumeroRegistros(int numeroRegistros) {
		this.numeroRegistros = numeroRegistros;
	}
	public String getCorresponsal() {
		return corresponsal;
	}
	public void setCorresponsal(String corresponsal) {
		this.corresponsal = corresponsal;
	}
	public Boolean getRequierePaginado() {
		return requierePaginado;
	}
	public void setRequierePaginado(Boolean requierePaginado) {
		this.requierePaginado = requierePaginado;
	}

	
}
