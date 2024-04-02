package mx.com.nmp.mspreconciliacion.exceptions;

import mx.com.nmp.mspreconciliacion.model.error.ModelError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;

import static mx.com.nmp.mspreconciliacion.exceptions.BaseException.GENERIC_CODE_ERROR;
import static mx.com.nmp.mspreconciliacion.exceptions.BaseException.LABEL_ERROR_ID;

/**
 * @author Quarksoft
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({ SistemaException.class, ElasticException.class, PagoException.class, PreconciliacionExcetion.class })
    public ResponseEntity<ModelError> handleBusinessException(HttpServletResponse respuesta,
                                                              Exception excepcion) {
        BaseException baseException = (BaseException) excepcion;
        respuesta.setStatus(baseException.getStatus().value());
        return new ResponseEntity<>(
                new ModelError(baseException.getId(),
                        baseException.getEstado(),
                        baseException.getDescripcion()),
                baseException.getStatus());

    }

    @ExceptionHandler({DataAccessResourceFailureException.class})
    public ResponseEntity<ModelError> handleElasticConnectionException() {
        return new ResponseEntity<>(
                new ModelError(LABEL_ERROR_ID, GENERIC_CODE_ERROR,
                        "Error al conectarse a la base de datos"),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ModelError> handleAllUncaughtException(
            Exception exception){
        LOG.error("Unknown error occurred", exception);
        return new ResponseEntity<>(
                new ModelError(LABEL_ERROR_ID, GENERIC_CODE_ERROR,
                        "Error desconocido. Por favor, notifique al " +
                                "administrador"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Class<?> requiredType = ex.getRequiredType();

        String error = ex.getParameter().getParameterName() + " debe ser del tipo " + ( (requiredType != null) ? requiredType.getName() : "");

        return new ResponseEntity<>(
                new ModelError(LABEL_ERROR_ID, GENERIC_CODE_ERROR,
                        error),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder errors = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.append(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.append(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        return new ResponseEntity<>(
                new ModelError(LABEL_ERROR_ID, GENERIC_CODE_ERROR,
                        errors.toString()),
                status);

    }


}
