package mx.com.nmp.mspreconciliacion.model.dto;

import java.math.BigDecimal;
import java.util.Date;


public class ComisionesDTO extends MontosComisionesDTO {

	private String banco;
	private String sucursal;
	private Date fechaLiquidacion;
	private Date fechaVenta;
	private String tipoTarjeta;
	private String tipoPago;
	private BigDecimal comisionTransaccionalMIDAS; //Comision registrada en MIDAS y se obtiene por WSMidas
	private BigDecimal sobretasaMIDAS; //SobreTasa registrada en MIDAS y se obtiene por WSMidas
	private BigDecimal comisionTransaccionalEPA; //Se incluye campo, ya que es diferente a la de MIDAS
	private String plazo;
	private String idPago;

	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
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
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	public Date getFechaLiquidacion() {
		return fechaLiquidacion;
	}
	public void setFechaLiquidacion(Date fechaLiquidacion) {
		this.fechaLiquidacion = fechaLiquidacion;
	}
	public Date getFechaVenta() {
		return fechaVenta;
	}
	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	public BigDecimal getComisionTransaccionalMIDAS() {
		return comisionTransaccionalMIDAS;
	}
	public void setComisionTransaccionalMIDAS(BigDecimal comisionTransaccionalMIDAS) {
		this.comisionTransaccionalMIDAS = comisionTransaccionalMIDAS;
	}
	public BigDecimal getSobretasaMIDAS() {
		return sobretasaMIDAS;
	}
	public void setSobretasaMIDAS(BigDecimal sobretasaMIDAS) {
		this.sobretasaMIDAS = sobretasaMIDAS;
	}
	public String getPlazo() {
		return plazo;
	}
	public void setPlazo(String plazo) {
		this.plazo = plazo;
	}
	public BigDecimal getComisionTransaccionalEPA() {
		return comisionTransaccionalEPA;
	}
	public void setComisionTransaccionalEPA(BigDecimal comisionTransaccionalEPA) {
		this.comisionTransaccionalEPA = comisionTransaccionalEPA;
	}
	public String getIdPago() {
		return idPago;
	}
	public void setIdPago(String idPago) {
		this.idPago = idPago;
	}
}
