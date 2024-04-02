package mx.com.nmp.mspreconciliacion.model.preconciliacion;


import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

@TypeAlias("PagosConciliadoMIT")
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{@environment.getProperty('mspreconciliacion.elasticsearch.moPagosMitConciliados')}")
public class PagoConciliadoMIT implements Serializable{

    private static final long serialVersionUID = -5402765039421302465L;

    @Id
    private String id;
    private String idPago;
    private String idConciliacion;
    private Date fechaCreacion;
    private Integer estado;
    private CoreConciliadoMIT core;
    private CorresponsalConciliadoMIT corresponsal;

    public CorresponsalConciliadoMIT getCorresponsal() {
        return corresponsal;
    }

    public void setCorresponsal(CorresponsalConciliadoMIT corresponsal) {
        this.corresponsal = corresponsal;
    }    
    
    public String getIdConciliacion() {
        return idConciliacion;
    }

    public void setIdConciliacion(String idConciliacion) {
        this.idConciliacion = idConciliacion;
    }    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CoreConciliadoMIT getCore() {
        return core;
    }

    public void setCore(CoreConciliadoMIT core) {
        this.core = core;
    }

    public String getIdPago() {
        return idPago;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}    
}
