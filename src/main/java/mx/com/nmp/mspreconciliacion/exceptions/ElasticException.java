package mx.com.nmp.mspreconciliacion.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Quarksoft
 */
public class ElasticException extends BaseException {

    private static final long serialVersionUID = -1681708771407273251L;

    public static final ElasticException INTERNAL_SERVER_ERROR_ELASTICSEARCH =
            new ElasticException("Servicio Nacional Monte de Piedad no disponible.",
                    "01", HttpStatus.INTERNAL_SERVER_ERROR);

    public ElasticException(String descripcion, String estado, HttpStatus status) {
        super(descripcion, estado, status);
    }
}
