package mx.com.nmp.mspreconciliacion.model.preconciliacion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Date;

@TypeAlias("PagosConciliadoEPA")
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{@environment.getProperty('mspreconciliacion.elasticsearch.moPagosMitConciliadosEPA')}")
public class PagoConciliadoEPA implements Serializable {

    private static final long serialVersionUID = 1382039333169695188L;

    @Id
    private String id;

    private String idPago;

    private String idConciliacion;

    private Date fechaCreacion;
    
    private Integer estado;
    
    private CoreConciliadoMIT core;

    CorresponsalConciliadoEPA corresponsal;
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }
    
    public String getIdPago() {
        return idPago;
    }
    
    public CoreConciliadoMIT getCore() {
        return core;
    }

    public void setCore(CoreConciliadoMIT core) {
        this.core = core;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CorresponsalConciliadoEPA getCorresponsal() {
        return corresponsal;
    }

    public void setCorresponsal(CorresponsalConciliadoEPA corresponsal) {
        this.corresponsal = corresponsal;
    }


    public String getIdConciliacion() {
        return idConciliacion;
    }

    public void setIdConciliacion(String idConciliacion) {
        this.idConciliacion = idConciliacion;
    }

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}




}
