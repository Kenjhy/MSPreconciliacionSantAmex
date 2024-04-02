package mx.com.nmp.mspreconciliacion.model.error;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ModelError {
    private String id;
    private String estado;
    private String descripcion;

    public ModelError(String id, String estado, String descripcion) {
        this.id = id;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
