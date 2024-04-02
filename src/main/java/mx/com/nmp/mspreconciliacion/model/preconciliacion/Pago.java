package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

@TypeAlias("Pagos")
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{@environment.getProperty('mspreconciliacion.elasticsearch.moPagos')}")
public class Pago implements Serializable {

    private static final long serialVersionUID = -8498793830319685199L;

    @Id
    private String id;
    private String tipoReferencia;
    private String autorizacion;
    private String canal;
    private Long referenciaLarga;    
    private Cliente cliente;
    private String concepto;
    private Corresponsal corresponsal;
    private String plataformaOrigen;    
    private String idTransaccionMIDAS;
    private int estado;
    private String tipoTransaccion;    
    private String estadoMIT;
    private Date fechaActualizacion;
    private Date fechaAplicacion;
    private Date fechaCreacion;
    private Date fechaOperacion;
    private String idPagoCorresponsal;
    private String metodo;
    private String moneda;
    private BigDecimal montoTotal;
    private List<Partida> partidas;
    private String plataformaDestino;
    private Integer referencia;
    private String tiempoVigencia;
    private boolean vigente;
    private String idTransaccion;
    private boolean noEncontradoConvertA15;



    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaOperacion() {
        return fechaOperacion;
    }
    public void setFechaOperacion(Date fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getPlataformaDestino() {
        return plataformaDestino;
    }

    public void setPlataformaDestino(String plataformaDestino) {
        this.plataformaDestino = plataformaDestino;
    }

    public String getIdPagoCorresponsal() {
        return idPagoCorresponsal;
    }

    public void setIdPagoCorresponsal(String idPagoCorresponsal) {
        this.idPagoCorresponsal = idPagoCorresponsal;
    }

    public String getTipoReferencia() {
        return tipoReferencia;
    }

    public void setTipoReferencia(String tipoReferencia) {
        this.tipoReferencia = tipoReferencia;
    }

    public String getIdTransaccionMIDAS() {
        return idTransaccionMIDAS;
    }

    public void setIdTransaccionMIDAS(String idTransaccionMIDAS) {
        this.idTransaccionMIDAS = idTransaccionMIDAS;
    }

    public String getTiempoVigencia() {
        return tiempoVigencia;
    }

    public void setTiempoVigencia(String tiempoVigencia) {
        this.tiempoVigencia = tiempoVigencia;
    }

    public boolean isVigente() {
        return vigente;
    }

    public void setVigente(boolean vigente) {
        this.vigente = vigente;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Corresponsal getCorresponsal() {
        return corresponsal;
    }

    public void setCorresponsal(Corresponsal corresponsal) {
        this.corresponsal = corresponsal;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<Partida> partidas) {
        this.partidas = partidas;
    }

    public String getPlataformaOrigen() {
        return plataformaOrigen;
    }

    public void setPlataformaOrigen(String plataformaOrigen) {
        this.plataformaOrigen = plataformaOrigen;
    }

    public Long getReferenciaLarga() {
        return referenciaLarga;
    }

    public void setReferenciaLarga(Long referenciaLarga) {
        this.referenciaLarga = referenciaLarga;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public Integer getReferencia() {
        return referencia;
    }
    public void setReferencia(Integer referencia) {
        this.referencia = referencia;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getEstadoMIT() {
        return estadoMIT;
    }

    public void setEstadoMIT(String estadoMIT) {
        this.estadoMIT = estadoMIT;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public boolean isNoEncontradoConvertA15() {
		return noEncontradoConvertA15;
	}

	public void setNoEncontradoConvertA15(boolean noEncontradoConvertA15) {
		this.noEncontradoConvertA15 = noEncontradoConvertA15;
	}    
}
