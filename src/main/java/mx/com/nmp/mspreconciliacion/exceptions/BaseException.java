package mx.com.nmp.mspreconciliacion.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Quarksoft
 */
public abstract class BaseException extends Exception {

    private static final long serialVersionUID = -3700427277866931660L;

    public static final String LABEL_ERROR_ID = "Error";
    public static final String GENERIC_CODE_ERROR = "00";

    protected final String id;
    protected final String estado;
    protected final String descripcion;
    protected final HttpStatus status;

    protected BaseException(String descripcion, String estado, HttpStatus status) {
        this.id = LABEL_ERROR_ID;
        this.estado = estado;
        this.descripcion = descripcion;
        this.status = status;
    }

    public String getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

}
