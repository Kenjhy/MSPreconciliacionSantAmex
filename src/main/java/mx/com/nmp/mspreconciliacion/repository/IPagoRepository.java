package mx.com.nmp.mspreconciliacion.repository;

import mx.com.nmp.mspreconciliacion.model.preconciliacion.Pago;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para las consultas al indice de pagos
 */
@Repository
public interface IPagoRepository extends ElasticsearchRepository<Pago,String> {

    /**
     * Metodo que no obtiene el pago a partir de una referencia
     * @param idPago Identificador del pago
     * @return
     */
    Pago findFirstById(String id);

    @Query("{\"bool\" : {\"must\" : [{\"range\" : { \"fechaOperacion\": { \"gte\" : \"?0\", \"lte\" : \"?1\" } } }, {\"match_bool_prefix\": { \"plataformaOrigen\": \"?2\" } }]}}")
    List<Pago> findByFechaOperacionAndPlataformaOrigen(Long fechaOperacion, Long fechaOperacion2, String plataformaOrigen);

    List<Pago> findFirstByIdIn(List<String> ids);
}
