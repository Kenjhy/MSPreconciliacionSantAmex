package mx.com.nmp.mspreconciliacion.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Quarksoft
 */
public class SistemaException extends BaseException {

    private static final long serialVersionUID = -6901942505886878367L;

    public static final SistemaException INTERNAL_SERVER_ERROR =
            new SistemaException("Ocurrio un error interno en el servidor",
                    "00", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final SistemaException ERROR_DESCONOCIDO =
            new SistemaException("Error desconocido. Por favor, notifique al administrador", "00",
                    HttpStatus.INTERNAL_SERVER_ERROR);

    public static final SistemaException ERROR_WS_COMISIONPAGOS_MIDAS_REQUEST =
            new SistemaException("Error al obtener la información de las comsiones bancarias desde MIDAS.",
            "09", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final SistemaException ERROR_TOKEN_COMISIONESOAGMIDAS_REQUEST =
            new SistemaException("No se puede obtener el token de autorización",
            "09", HttpStatus.INTERNAL_SERVER_ERROR);
    
    public SistemaException(String descripcion, String estado, HttpStatus status) {
        super(descripcion, estado, status);
    }
}
