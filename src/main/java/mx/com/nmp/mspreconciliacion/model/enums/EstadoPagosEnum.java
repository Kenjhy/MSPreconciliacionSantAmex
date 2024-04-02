package mx.com.nmp.mspreconciliacion.model.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Estados por los cuales puede pasar un pago
 */
public enum EstadoPagosEnum {
    PAGO_RECIBIDO(1),
    PAGO_ENVIADO(2),
    PAGO_APLICADO(3),
    PAGO_RECHAZADO(4),
    PAGO_POR_PROCESAR(5),
    PAGO_POR_RESERVAR(6),
    PAGO_RESERVADO_POR_CORRESPONSAL(7),
    PAGO_RESERVADO_NO_APLICADO_EN_CORE(8),
    PAGO_PENDIENTE_DE_RESERVAR(9),
    PAGO_CONCILIADO(10),
    PAGO_PENDIENTE_BONIFICAR(11),
    PAGO_CANCELADO_CORE(12),
    PAGO_DEVUELTO_CORE(13),
    PAGO_POR_DEVOLVER_CORE(14),
    PAGO_ENVIADO_CONCILIAR(15),
    PAGO_ENVIADO_CONCILIAR_SIN_CARGO_RECONOCIDO(16),
	PAGO_POR_DEVOLVER_LIQUIDADO(17), //Estado para las devoluciones Admvas liquidadas
	PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO(18), //Estado para devolucion Automatica No Reconocida- Solo Santander
	PAGO_DEVUELTO_CORE_LIQUIDADO(19), //Estado para las devoluciones Automaticas liquidadas
	
	PAGO_DEVUELTO_CORE_LIQUIDADO_PROCESADO_A_CONCILIACION(20), //Estado para las devoluciones Automaticas liquidadas-Procesadas a conciliacion
	PAGO_POR_DEVOLVER_LIQUIDADO_PROCESADO_A_CONCILIACION(21), //Estado para las devoluciones Admvas liquidadas-Procesadas a conciliacion
	
	PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO(22), //Estado para devolucion Administrativa No Reconocida- Solo AMEX
	
	//Estatus para controlar liquidacion en devoluciones no encontradas 18, 22
	PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_LIQUIDADO(23), //Estado Dev Automatica No Reconocido- Liquidada
	PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_LIQUIDADO(24), //Estado Dev Admva No Reconocido- Liquidada
	
	PAGO_DEVUELTO_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION(26), //Estado Dev Automatica No Reconocido- Procesada a conciliacion
	PAGO_POR_DEVOLVER_SIN_CARGO_RECONOCIDO_PROCESADO_A_CONCILIACION(27); //Estado Dev Admva No Reconocido- Procesada a conciliacion
	

    private final Integer estado;
    private EstadoPagosEnum(Integer estado) {
        this.estado=estado;
    }

    public Integer getEstadoPagos(){
        return this.estado;
    }

    public static List<EstadoPagosEnum> toList(){
        return Stream.of(values().clone()).collect(Collectors.toList());
    }

}
