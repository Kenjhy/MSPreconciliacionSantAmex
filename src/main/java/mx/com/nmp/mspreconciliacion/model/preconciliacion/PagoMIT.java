package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@TypeAlias("PagosMIT")
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{@environment.getProperty('mspreconciliacion.elasticsearch.moPagosMit')}")
public class PagoMIT implements Serializable {

    private static final long serialVersionUID = -3771493680129538887L;

    @Id
    private String id;
    private String autorizacion;
    private String canal;
    private Cliente cliente;
    private String concepto;
    private CorresponsalMIT corresponsal;
    private Devolucion devolucion;
    private Integer estado;
    private Date fechaActualizacion;
    private Date fechaAplicacion;
    private Date fechaCreacion;
    private Date fechaOperacion;
    private String idPago;
    private String idPagoCorresponsal;
    private String idTransaccion;
    private String idTransaccionMIDAS;
    private String metodo;
    private String moneda;
    private BigDecimal montoTotal;
    private List<Partida> partidas;
    private String plataformaDestino;
    private String plataformaOrigen;
    private Integer referencia;
    private Long referenciaLarga;
    private String tiempoVigencia;
    private String tipoReferencia;
    private String tipoTransaccion;
    private boolean vigente;
    private boolean noEncontradoConvertA15;

    public boolean isNoEncontradoConvertA15() {
		return noEncontradoConvertA15;
	}

	public void setNoEncontradoConvertA15(boolean noEncontradoConvertA15) {
		this.noEncontradoConvertA15 = noEncontradoConvertA15;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public CorresponsalMIT getCorresponsal() {
        return corresponsal;
    }

    public void setCorresponsal(CorresponsalMIT corresponsal) {
        this.corresponsal = corresponsal;
    }

    public Devolucion getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(Devolucion devolucion) {
        this.devolucion = devolucion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getIdPagoCorresponsal() {
        return idPagoCorresponsal;
    }

    public void setIdPagoCorresponsal(String idPagoCorresponsal) {
        this.idPagoCorresponsal = idPagoCorresponsal;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getIdTransaccionMIDAS() {
        return idTransaccionMIDAS;
    }

    public void setIdTransaccionMIDAS(String idTransaccionMIDAS) {
        this.idTransaccionMIDAS = idTransaccionMIDAS;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
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

    public List<Partida> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<Partida> partidas) {
        this.partidas = partidas;
    }

    public String getPlataformaDestino() {
        return plataformaDestino;
    }

    public void setPlataformaDestino(String plataformaDestino) {
        this.plataformaDestino = plataformaDestino;
    }

    public String getPlataformaOrigen() {
        return plataformaOrigen;
    }

    public void setPlataformaOrigen(String plataformaOrigen) {
        this.plataformaOrigen = plataformaOrigen;
    }

    public Integer getReferencia() {
        return referencia;
    }

    public void setReferencia(Integer referencia) {
        this.referencia = referencia;
    }

    public Long getReferenciaLarga() {
        return referenciaLarga;
    }

    public void setReferenciaLarga(Long referenciaLarga) {
        this.referenciaLarga = referenciaLarga;
    }

    public String getTiempoVigencia() {
        return tiempoVigencia;
    }

    public void setTiempoVigencia(String tiempoVigencia) {
        this.tiempoVigencia = tiempoVigencia;
    }

    public String getTipoReferencia() {
        return tipoReferencia;
    }

    public void setTipoReferencia(String tipoReferencia) {
        this.tipoReferencia = tipoReferencia;
    }

    public String getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public boolean isVigente() {
        return vigente;
    }

    public void setVigente(boolean vigente) {
        this.vigente = vigente;
    }
}
