package mx.com.nmp.mspreconciliacion.repository;

import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoMIT;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para las consultas al indice de pagos mit
 */
@Repository
public interface IPagoMITRepository extends ElasticsearchRepository<PagoMIT,String>{

    /**
     * Método que nos ayuda a buscar los registros dentro del indice de mo_pago_mit por medio de los
     * parametros enviados
     * @param rspOperationNumber Numero de operacion
     * @param rspAuth Autorizacion
     * @param estado Estados del pago deben ser pago aplicado y devuelto
     * @return
     */
    PagoMIT findFirstByCorresponsalRspOperationNumberAndCorresponsalRspAuthAndEstadoIn(String rspOperationNumber, Integer rspAuth , List<Integer> estado);


    /****
     * Consulta las devoluciones por corresponsal y por tipo de Operación ->Devolución
     * @param rspDsOperationType
     * @param corresponsal
     * @return
     */
    List<PagoMIT> findByCorresponsalRspDsOperationTypeAndPlataformaOrigen(String rspDsOperationType, String corresponsal);


    /***
     * Consulta los pagos por estado y por corresponsal
     * @param estado
     * @param corresponsal
     * @return
     */
    List<PagoMIT> findByEstadoAndPlataformaOrigen(Integer estado, String plataformaOrigen);


    @Query("{\"bool\" : {\"must\" : [{\"range\" : { \"fechaOperacion\": { \"gte\" : \"?0\", \"lte\" : \"?1\", \"time_zone\" : \"GMT-06:00\" } } }, {\"match_bool_prefix\": { \"plataformaOrigen\": \"?2\" } }]}}")
    List<PagoMIT> findByFechaOperacionAndPlataformaOrigen(Long fechaOperacion, Long fechaOperacion2, String plataformaOrigen);

    Optional<PagoMIT> findById(String id);

    List<PagoMIT> findByPartidasNumeroSucursalPartida(Integer numeroSucursalPartida);

    List<PagoMIT> findByIdPagoIn(List<String> ids);

    List<PagoMIT> findByCorresponsalRspOperationNumberAndEstadoIn(String rspOperationNumber, List<Integer> estado);

    List<PagoMIT> findByPlataformaOrigenAndEstadoIn(String plataformaOrigen, Integer estado);

}

