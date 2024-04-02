
package mx.com.nmp.mspreconciliacion.util;


import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;



/**
 * Nombre: Response
 * Descripcion: Clase que sirve de envoltura para las respuestas de los controladores,
 * tiene como finalidad estandarizar las respuestas de los diferentes controladores
 *
 * @author: Javier Hernandez Barraza jhernandez@quarksoft.net
 * @version: 0.1
 */

@Component
@Scope("prototype")
public class Response {

    /**
     * Codigo de respuesta http
     * @see HttpStatus
     */
    private String code;

    /**
     * Mesnaje de la respuesta
     */
    private String message;

    /**
     * Cuerpo de la respuesta
     */
    private Object object;

    /**
     * Constructor de la clase
     */
    public Response() {
        super();
    }

    /**
     * Constructor de la clase
     *
     * @param code Codigo de respuesta http
     * @param message Mesnaje de la respuesta
     * @param object Cuerpo de la respuesta
     */
    public Response(String code, String message, Object object) {
        super();

        this.code = code;
        this.message = message;
        this.object = object;
    }

    /**
     * Recupera el valor de {@code code}
     *
     * @return Valor de {@code code}
     */
    public String getCode() {
        return code;
    }

    /**
     * Establece el valor de {@code code}
     *
     * @param code Valor de {@code code}
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Recupera el valor de {@code message}
     *
     * @return Valor de {@code message}
     */
    public String getMessage() {
        return message;
    }

    /**
     * Establece el valor de {@code message}
     *
     * @param message Valor de {@code message}
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Recupera el valor de {@code object}
     *
     * @return Valor de {@code object}
     */
    public Object getObject() {
        return object;
    }

    /**
     * Establece el valor de {@code object}
     *
     * @param object Valor de {@code object}
     */
    public void setObject(Object object) {
        this.object = object;
    }
}
