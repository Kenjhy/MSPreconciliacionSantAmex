package mx.com.nmp.mspreconciliacion.model.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@XmlRootElement
public class MovCorresponsalDTO {

	//campos Santander
	@JacksonXmlProperty(localName = "nb_response")
	private String nbResponse;
	
	@JacksonXmlProperty(localName = "cd_empresa")
	private String empresa;
	/**
	 * Numero de operación del centro de pagos
	 */
	@JacksonXmlProperty(localName = "nu_operaion")
	private String numeroOperacion;
	
	@JacksonXmlProperty(localName = "cd_tipocobro")
	private String tipoCobro;

	@JacksonXmlProperty(localName = "cd_usuario")
	private String usuario;
	
	@JacksonXmlProperty(localName = "nu_sucursal")
	private String sucursal;
	
	@JacksonXmlProperty(localName = "nu_afiliacion")
	private String afiliacion;
	
	/**
	 * Referencia generada por el banco
	 */
	@JacksonXmlProperty(localName = "nb_referencia")
	private String referencia;
	
	@JacksonXmlProperty(localName = "cc_nombre")
	private String nombreTH;
	
	@JacksonXmlProperty(localName = "cd_instrumento")
	private String tipoTarjeta;
	
	@JacksonXmlProperty(localName = "nu_importe")
	private BigDecimal importeBruto;
	
	@JacksonXmlProperty(localName = "cd_tipopago")
	private String tipoPago; //Contado, MSI, MCI
	
	@JacksonXmlProperty(localName = "tp_operacion")
	private String tipoOperacion; //Venta, Cancelacion, Devolucion
	
	@JacksonXmlProperty(localName = "cc_num")
	private String tarjeta;
	
	@JacksonXmlProperty(localName = "nu_auth")
	private String autorizacion;
	
	private Date fechaOperacion;
	
	@JacksonXmlProperty(localName = "fh_registro")
	private String fechaOperacionCadena;
	
	@JacksonXmlProperty(localName = "fh_bank")
	private String fechaDepositoCadena;
	
	private Date fechaDeposito;
	
	@JacksonXmlProperty(localName = "cd_usrtransaccion")
	private String usrTrx;
	
	@JacksonXmlProperty(localName = "nb_currency")
	private String moneda;
	
	private String lote;
	private String nombreArchivo;
	private String nombreAfiliacion;//Santander-sftp.mitec
	
	@JacksonXmlProperty(localName = "cc_tp")
	private String marcaTarjeta;
	
	private String bancoEmisor; //Santander-sftp.mitec
	private String horaOperacion;
	
	@JacksonXmlProperty(localName = "nb_resp")
	private String estado;
	
	@JacksonXmlProperty(localName = "cd_resp")
	private String codigoEstado;
	
	//campos AMEX
	private String establecimiento; //Amex	
	public String getNbResponse() {
		return nbResponse;
	}
	public void setNbResponse(String nbResponse) {
		this.nbResponse = nbResponse;
	}
	private BigDecimal importeNeto;
	private BigDecimal comisionTransaccional;
	private BigDecimal ivaTransaccional;
	 
	public String getTipoCobro() {
		return tipoCobro;
	}
	public void setTipoCobro(String tipoCobro) {
		this.tipoCobro = tipoCobro;
	}
	//mensualidades
	private String idTransaccion;
	private Integer numeroMesesPromocion;
	private BigDecimal sobretasa;//Comision Promocion
	private BigDecimal ivaSobretasa; // IVA Promocion
	private BigDecimal sumaSobretasaIva;
	
	//variables internas para calculos -AMEX-GRRCN
	private BigDecimal comisionPromoServicio;
	private BigDecimal comisionPromoAceleracion;
	
	//Comisión reportada en archivo AMEX-GRRCN- se guarda para indicar tasa con la que se cálcula campo comisionTransaccional,  
	private BigDecimal comisionTransaccionalEPA;
	
	//Devolución
	private String tipoDevolucion;
	private Date fechaLiquidacion;
	private String estatusDevolucion;
	private Integer estatusDevolucionId;
	private BigDecimal importeDevolucion;
	private Date fechaDevolucion;
	private boolean esDevolucion;
	private boolean esSobreCargo;
	
	//Pagos para efectos de comisiones
	private boolean esAMEX;
	
	public BigDecimal getIvaTransaccional() {
		return ivaTransaccional;
	}
	public void setIvaTransaccional(BigDecimal ivaTransaccional) {
		this.ivaTransaccional = ivaTransaccional;
	}	
	

	public String getNumeroOperacion() {
		return numeroOperacion;
	}
	public void setNumeroOperacion(String numeroOperacion) {
		this.numeroOperacion = numeroOperacion;
	}
	
	public BigDecimal getImporteNeto() {
		return importeNeto;
	}
	public void setImporteNeto(BigDecimal importeNeto) {
		this.importeNeto = importeNeto;
	}	
	

	public String getTipoOperacion() {
		return tipoOperacion;
	}
	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	

	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	
	
	public String getAfiliacion() {
		return afiliacion;
	}
	public void setAfiliacion(String afiliacion) {
		this.afiliacion = afiliacion;
	}
	
	
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	

	public String getNombreTH() {
		return nombreTH;
	}
	public void setNombreTH(String nombreTH) {
		this.nombreTH = nombreTH;
	}
	

	public String getTipoTarjeta() {
		return tipoTarjeta;
	}
	public void setTipoTarjeta(String tipoTarjeta) {
		this.tipoTarjeta = tipoTarjeta;
	}
	
	
	public BigDecimal getImporteBruto() {
		return importeBruto;
	}
	public void setImporteBruto(BigDecimal importeBruto) {
		this.importeBruto = importeBruto;
	}
	
	
	public String getTipoPago() {
		return tipoPago;
	}
	
	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}
	

	
	
	public String getTarjeta() {
		return tarjeta;
	}
	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}
	
	
	public String getAutorizacion() {
		return autorizacion;
	}
	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}
	

	public Date getFechaOperacion() {
		return fechaOperacion;
	}
	public boolean isEsSobreCargo() {
		return esSobreCargo;
	}
	public void setEsSobreCargo(boolean esSobreCargo) {
		this.esSobreCargo = esSobreCargo;
	}
	public void setFechaOperacion(Date fechaOperacion) {
		this.fechaOperacion = fechaOperacion;
	}
	public Date getFechaDeposito() {
		return fechaDeposito;
	}
	public void setFechaDeposito(Date fechaDeposito) {
		this.fechaDeposito = fechaDeposito;
	}
	
		
	public String getUsrTrx() {
		return usrTrx;
	}
	public Date getFechaDevolucion() {
		return fechaDevolucion;
	}
	public void setFechaDevolucion(Date fechaDevolucion) {
		this.fechaDevolucion = fechaDevolucion;
	}
	public void setUsrTrx(String usrTrx) {
		this.usrTrx = usrTrx;
	}
	
	
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public String getLote() {
		return lote;
	}
	public void setLote(String lote) {
		this.lote = lote;
	}
	public String getNombreArchivo() {
		return nombreArchivo;
	}
	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	public String getNombreAfiliacion() {
		return nombreAfiliacion;
	}
	public void setNombreAfiliacion(String nombreAfiliacion) {
		this.nombreAfiliacion = nombreAfiliacion;
	}
	
	
	public String getMarcaTarjeta() {
		return marcaTarjeta;
	}
	public void setMarcaTarjeta(String marcaTarjeta) {
		this.marcaTarjeta = marcaTarjeta;
	}
	public String getBancoEmisor() {
		return bancoEmisor;
	}
	public void setBancoEmisor(String bancoEmisor) {
		this.bancoEmisor = bancoEmisor;
	}
	public String getHoraOperacion() {
		return horaOperacion;
	}
	public void setHoraOperacion(String horaOperacion) {
		this.horaOperacion = horaOperacion;
	}
	

	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
	public String getCodigoEstado() {
		return codigoEstado;
	}
	public void setCodigoEstado(String codigoEstado) {
		this.codigoEstado = codigoEstado;
	}
	
	public String getEstablecimiento() {
		return establecimiento;
	}
	public void setEstablecimiento(String establecimiento) {
		this.establecimiento = establecimiento;
	}

	public BigDecimal getComisionTransaccional() {
		return comisionTransaccional;
	}
	public void setComisionTransaccional(BigDecimal comisionTransaccional) {
		this.comisionTransaccional = comisionTransaccional;
	}

	public Integer getNumeroMesesPromocion() {
		return numeroMesesPromocion;
	}
	public void setNumeroMesesPromocion(Integer numeroMesesPromocion) {
		this.numeroMesesPromocion = numeroMesesPromocion;
	}
	public BigDecimal getSobretasa() {
		return sobretasa;
	}
	public void setSobretasa(BigDecimal sobretasa) {
		this.sobretasa = sobretasa;
	}
	public BigDecimal getIvaSobretasa() {
		return ivaSobretasa;
	}
	public void setIvaSobretasa(BigDecimal ivaSobretasa) {
		this.ivaSobretasa = ivaSobretasa;
	}
	
			
	public String getFechaOperacionCadena() {
		return fechaOperacionCadena;
	}
	public void setFechaOperacionCadena(String fechaOperacionCadena) {
		this.fechaOperacionCadena = fechaOperacionCadena;
	}
	
	
	public String getFechaDepositoCadena() {
		return fechaDepositoCadena;
	}
	public void setFechaDepositoCadena(String fechaDepositoCadena) {
		this.fechaDepositoCadena = fechaDepositoCadena;
	}
	public String getIdTransaccion() {
		return idTransaccion;
	}
	public void setIdTransaccion(String idTransaccion) {
		this.idTransaccion = idTransaccion;
	}
	public BigDecimal getComisionPromoServicio() {
		return comisionPromoServicio;
	}
	public void setComisionPromoServicio(BigDecimal comisionPromoServicio) {
		this.comisionPromoServicio = comisionPromoServicio;
	}
	public BigDecimal getComisionPromoAceleracion() {
		return comisionPromoAceleracion;
	}
	public void setComisionPromoAceleracion(BigDecimal comisionPromoAceleracion) {
		this.comisionPromoAceleracion = comisionPromoAceleracion;
	}
	
	public BigDecimal getSumaSobretasaIva() {
		return sumaSobretasaIva;
	}
	public void setSumaSobretasaIva(BigDecimal sumaSobretasaIva) {
		this.sumaSobretasaIva = sumaSobretasaIva;
	}	

	public String getTipoDevolucion() {
		return tipoDevolucion;
	}
	public void setTipoDevolucion(String tipoDevolucion) {
		this.tipoDevolucion = tipoDevolucion;
	}

	public String getEstatusDevolucion() {
		return estatusDevolucion;
	}
	public void setEstatusDevolucion(String estatusDevolucion) {
		this.estatusDevolucion = estatusDevolucion;
	}
	public Date getFechaLiquidacion() {
		return fechaLiquidacion;
	}
	public void setFechaLiquidacion(Date fechaLiquidacion) {
		this.fechaLiquidacion = fechaLiquidacion;
	}
	public BigDecimal getImporteDevolucion() {
		return importeDevolucion;
	}
	public void setImporteDevolucion(BigDecimal importeDevolucion) {
		this.importeDevolucion = importeDevolucion;
	}
	public final Integer getEstatusDevolucionId() {
		return estatusDevolucionId;
	}
	public final void setEstatusDevolucionId(Integer estatusDevolucionId) {
		this.estatusDevolucionId = estatusDevolucionId;
	}
	public BigDecimal getComisionTransaccionalEPA() {
		return comisionTransaccionalEPA;
	}
	public void setComisionTransaccionalEPA(BigDecimal comisionTransaccionalEPA) {
		this.comisionTransaccionalEPA = comisionTransaccionalEPA;
	}
	public boolean isEsDevolucion() {
		return esDevolucion;
	}
	public void setEsDevolucion(boolean esDevolucion) {
		this.esDevolucion = esDevolucion;
	}
	public String getEmpresa() {
		return empresa;
	}
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	public boolean isEsAMEX() {
		return esAMEX;
	}
	public void setEsAMEX(boolean esAMEX) {
		this.esAMEX = esAMEX;
	}

	

}
