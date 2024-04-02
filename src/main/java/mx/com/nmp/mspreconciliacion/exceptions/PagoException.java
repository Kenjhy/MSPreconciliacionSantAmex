package mx.com.nmp.mspreconciliacion.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author Quarksoft
 */
public class PagoException extends BaseException {

    private static final long serialVersionUID = -9110060745335962358L;

    public static final PagoException NOT_FOUND_PAGO =
            new PagoException("No se encuentra el pago.",
                    "02", HttpStatus.NOT_FOUND);

    public static final PagoException CORRESPONSAL_INCORRECTO = new PagoException(
            "El tipo corresponsal es de tipo Incorrecto",
            "03", HttpStatus.BAD_REQUEST);

    public static final PagoException ERROR_SAVE_PAGO =new PagoException("Error al procesar el pago.",
            "04", HttpStatus.INTERNAL_SERVER_ERROR);
    
    public static final PagoException ERROR_WSCENTROPAGOS_IO =new PagoException("Error al conectarse al WS-Centro de Pagos",
            "05", HttpStatus.NOT_FOUND);
    
    public static final PagoException ERROR_WSCENTROPAGOS =new PagoException("Error al conectarse al WS-Centro de Pagos, usuario y/o contraseña Invalidos",
            "06", HttpStatus.NOT_FOUND);
    
    public static final PagoException ERROR_WSCENTROPAGOS_RESPONSE =new PagoException("Error al obtener response de WS-Centro de Pagos.",
            "07", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final PagoException ERROR_WSCENTROPAGOS_RESPONSE_MAPPER =new PagoException("Error al transformar fechas-response de WS-Centro de Pagos.",
            "08", HttpStatus.INTERNAL_SERVER_ERROR);    
    
    public static final PagoException ERROR_WSCENTROPAGOS_REQUEST =new PagoException("Error al generar request al WS-Centro de Pagos.",
            "09", HttpStatus.BAD_REQUEST);    
    
    public static final PagoException ERROR_SANTANDER_SFTP =new PagoException("Error al conectar al servidor SFTP Santander",
            "10", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final PagoException ERROR_SANTANDER_SFTP_DISC =new PagoException("Error al desconectar al servidor SFTP Santander",
            "11", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final PagoException ERROR_AMEX_SFTP =new PagoException("Error al conectar al servidor SFTP AMEX",
            "12", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final PagoException ERROR_AMEX_SFTP_DISC =new PagoException("Error al desconectar al servidor SFTP AMEX",
            "13", HttpStatus.INTERNAL_SERVER_ERROR);
    
    public static final PagoException ERROR_AMEX_SFTP_PROC =new PagoException("La estructura del archivo a procesar es incorrecta",
            "14", HttpStatus.INTERNAL_SERVER_ERROR);
    
    public static final PagoException ERROR_AMEX_SFTP_RESPONSE_MAPPER =new PagoException("Error al transformar fechas-response de SFTP AMEX",
            "15", HttpStatus.INTERNAL_SERVER_ERROR);        
    
    public static final PagoException ERROR_AMEX_SANTANDER_FECHA =new PagoException("No existen archivos para la fecha solicitada o ya fueron procesados",
            "16", HttpStatus.BAD_REQUEST);

    //Devoluciones
    public static final PagoException ERROR_DEVOLUCION_PROCESO =new PagoException("Error al procesar la información de pagos-Devoluciones de Indice mo_pagos_mit",
            "17", HttpStatus.INTERNAL_SERVER_ERROR);
    
    public static final PagoException ERROR_DEVOLUCION_GENERAR_FILTRO =new PagoException("Error al generar los filtros para la consulta Devoluciones",
            "18", HttpStatus.BAD_REQUEST);
    
    public static final PagoException ERROR_DEVOLUCION_MAPPEO =new PagoException("Error al generar el mapeo de la respuesta de la consulta Devoluciones",
            "19", HttpStatus.INTERNAL_SERVER_ERROR);
    
    public static final PagoException ERROR_DEVOLUCION_TIPO =new PagoException("Error el tipo de Devolución para consulta Devoluciones es incorrecto",
            "20", HttpStatus.BAD_REQUEST);
    
    public static final PagoException ERROR_DEVOLUCION_CORRESPONSAL =new PagoException("Error el Corresponsal para consulta Devoluciones es incorrecto",
            "21", HttpStatus.BAD_REQUEST);    
    
    public static final PagoException ERROR_DEVOLUCION_PAGINADO =new PagoException("Error es necesario indicar información para páginado",
            "22", HttpStatus.BAD_REQUEST);
    
    public static final PagoException ERROR_AMEX_BANCO =new PagoException("Error el banco es incorrecto",
            "23", HttpStatus.BAD_REQUEST);         

    public static final PagoException ERROR_CONCILIACION_FECHA =new PagoException("Error el campo fecha es requerido",
            "24", HttpStatus.BAD_REQUEST);   
    public static final PagoException ERROR_REPORTE_COMISIONES =new PagoException("Error al generar el reporte de comisiones MIT",
            "25", HttpStatus.INTERNAL_SERVER_ERROR);       
    
    public static final PagoException ERROR_DEVOLUCION_ACT_MAPEO =new PagoException("Error al generar mapeo para actualizar Devolucion",
            "26", HttpStatus.INTERNAL_SERVER_ERROR);       
    
    public static final PagoException ERROR_WS_OAG_MIDAS_COMISIONES =new PagoException("Ocurrio un error al consultar el WS Comisiones Bancarias desde Midas",
            "27", HttpStatus.INTERNAL_SERVER_ERROR);
    
    
    public PagoException(String descripcion, String estado, HttpStatus status) {
        super(descripcion, estado, status);
    }

    @Override
    public String toString() {
        return "PagoException{" +
                "id='" + id + '\'' +
                ", estado='" + estado + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", status=" + status +
                '}';
    }
}
