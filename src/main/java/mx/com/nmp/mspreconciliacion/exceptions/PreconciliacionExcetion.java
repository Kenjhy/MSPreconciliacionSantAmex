package mx.com.nmp.mspreconciliacion.exceptions;

import org.springframework.http.HttpStatus;

public class PreconciliacionExcetion extends BaseException{

    private static final long serialVersionUID = -1681708771407273251L;

    public static final PreconciliacionExcetion ERROR_MS_PAGOS_PRENDARIOS =
            new PreconciliacionExcetion("Error al enviar la peteción del actualización de estatus.",
                    "01", HttpStatus.INTERNAL_SERVER_ERROR);

    public PreconciliacionExcetion(String descripcion, String estado, HttpStatus status) {
        super(descripcion, estado, status);
    }

    @Override
    public String toString() {
        return "PreconciliacionExcetion{" +
                "id='" + id + '\'' +
                ", estado='" + estado + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", status=" + status +
                '}';
    }
}
