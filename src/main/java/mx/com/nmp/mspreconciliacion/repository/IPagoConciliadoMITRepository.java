package mx.com.nmp.mspreconciliacion.repository;

import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoMIT;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio para las consultas al indice de Pagos Conciliados MIT
 */
@Repository
public interface IPagoConciliadoMITRepository extends ElasticsearchRepository<PagoConciliadoMIT,String>{

    /**
     * Consulta para obtener los pagos del proveedor por fecha de operación
     * @param fechaOperacion fecha de operacion del pago
     * @return PagoConciliadoList
     */
    @Query("{\"bool\" : {\"must\" : [{\"range\" : { \"corresponsal.fechaOperacion\": { \"gte\" : \"?0\", \"lte\" : \"?1\", 	\"time_zone\" : \"GMT-06:00\" } }}]}}")
    List<PagoConciliadoMIT> findByFechaOperacion(Long fechaOperacion, Long fechaOperacion2);


    //@Query("{\"bool\" : {\"must\" : [{\"range\" : { \"corresponsal.fechaOperacion\": { \"gte\" : \"?0\", \"lte\" : \"?0\" } }}]}}")
    List<PagoConciliadoMIT> findByCorresponsalFechaOperacion(Long fechaOperacion);

    List<PagoConciliadoMIT> findByIdIn(List<String> ids);

    List<PagoConciliadoMIT> findByIdPagoIn(List<String> ids);

    List<PagoConciliadoMIT> findByCorresponsalNumeroOperacion(String numeroOperacion);

    PagoConciliadoMIT findByIdPago(String idPago);

    PagoConciliadoMIT findByCorresponsalTarjetaAndCorresponsalImporte(String tarjeta, BigDecimal importe);

    List<PagoConciliadoMIT> findByCorresponsalTipoOperacion(String tipoOperacion);

    List<PagoConciliadoMIT> findByCorresponsalNombreArchivo(String nombreArchivo);
    
    List<PagoConciliadoMIT> findByCorresponsalEsAMEX(boolean esAMEX);
    
    List<PagoConciliadoMIT> findByCorresponsalEsAMEXAndCorresponsalNumeroOperacionAndEstado(boolean esAMEX, String numeroOperacion, int estado);
}
