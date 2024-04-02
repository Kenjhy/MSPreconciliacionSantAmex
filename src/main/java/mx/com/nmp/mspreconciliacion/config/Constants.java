package mx.com.nmp.mspreconciliacion.config;

public final class Constants {

	public static final String SPRING_PROFILE_CLOUD = "cloud";
	public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
	public static final String SPRING_PROFILE_BMX = "bmx";
	public static final String SPRING_PROFILE_SWAGGER = "swagger";
	public static final String RESPONSE_CODE_SUCCESS = "S";
	public static final String RESPONSE_CODE_ERROR = "E";

	public static final String MSG_EXITOSO = "Exitoso";
	public static final String MSG_SUCCESS_WSCENTROPAGOS = "Registros recuperados correctamente del WS-Centro de Pagos";
	public static final String GENERIC_EXCEPTION_INITIAL_MESSAGE = "Error: ";

	public static final String NOMBRE_ARCHIVO_SANTANDER = "prva3FAA";
	public static final String NOMBRE_ARCHIVO_AMEX = "NACIONALMONTEMEXA64608.GRRCN.";
	public static final String ERROR_WSCENTROPAGOS = "Usuario y/o";

	public static final int ESTADO_PAGO_DEVUELTO = 13; //Automático
	public static final int ESTADO_PAGO_POR_DEVOLVER = 14; //Admva
	public static final int ESTADO_PAGO_LIQUIDADO = 17;

	public static final int ESTADO_PAGO_DEVUELTO_LIQUIDADO = 19;

	public static final int ESTADO_PAGO_LIQUIDADO_CONCILIADO = 20; //devoluciones Automaticas liquidadas-Procesadas
	public static final int ESTADO_PAGO_POR_DEVOLVER_LIQUIDADO_CONCILIADO = 21; //Admvas liquidadas-Procesadas a conciliacion

	public static final String TIPO_OPERACION_DEVOLUCION = "DEVOLUCION";
	public static final String TIPO_TARJETA_CREDITO = "CREDITO";
	public static final String TIPO_TARJETA_DEBITO = "DEBITO";

	public static final String TIPO_PAGO_CONTADO = "CONTADO";
	public static final String TIPO_OPERACION_VENTA = "VENTA";
	public static final String TIPO_OPERACION_CANCELA= "CANCELACION";

	public static final String TIPO_DEVOLUCION_AUTOMATICA = "AUTOMATICA";
	public static final String TIPO_DEVOLUCION_ADMVA = "ADMINISTRATIVA";

	public static final String ESTATUS_DEVOLUCION_LIQUIDADA = "LIQUIDADA";
	public static final String ESTATUS_DEVOLUCION_SOLICITADA = "SOLICITADA";
	public static final String ESTATUS_DEVOLUCION_LIQUIDADA_CONCILIADA = "LIQUIDADA_CONCILIADA";

	public static final int ESTATUS_DEVOLUCION_SOLICITADA_ID = 2;
	public static final int ESTATUS_DEVOLUCION_LIQUIDADA_ID = 3;

	public static final String CORRESPONSAL_SANTANDER = "SANTANDER";
	public static final String CORRESPONSAL_AMEX = "AMEX";

	public static final String FILTRO_ESTADO = "estado";
	public static final String FILTRO_FECHA_OPERACION = "fechaOperacion";
	public static final String FILTRO_PLATAFORMA_ORIGEN = "plataformaOrigen";
	public static final String CAMPO_NUMERO_OPERACION= "numeroOperacion";	
	
	public static final String WSPRECONCILIACION = "MS-PreconciliacionSantAMEX";

	public static final String ORIGEN_ARCHIVO = "ARCHIVO";
	public static final String ORIGEN_CENTROPAGOS = "CENTROPAGOS";

	private Constants() {
	}

}
