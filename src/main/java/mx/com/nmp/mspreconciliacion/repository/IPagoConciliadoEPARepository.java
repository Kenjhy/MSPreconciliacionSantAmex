package mx.com.nmp.mspreconciliacion.repository;

import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoEPA;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para las consultas al indice de Pagos Conciliados EPA
 */
@Repository
public interface IPagoConciliadoEPARepository extends ElasticsearchRepository<PagoConciliadoEPA,String> {

    @Query("{\"bool\" : {\"must\" : [{\"range\" : { \"corresponsal.fechaOperacion\": { \"gte\" : \"?0\", \"lte\" : \"?1\", 	\"time_zone\" : \"GMT-06:00\" } }}]}}")
    List<PagoConciliadoEPA> findByFechaOperacion(Long fechaOperacion, Long fechaOperacion2);
    
	List<PagoConciliadoEPA> findByCorresponsalNombreArchivo(String archivo);

	List<PagoConciliadoEPA> findByCorresponsalFechaOperacion(Long fechaOperacion);
	
	List<PagoConciliadoEPA> findByCorresponsalFechaOperacionAndCorresponsalTipoDevolucion(Long fechaOperacion, String tipoDevolucion);
	
	List<PagoConciliadoEPA> findByCorresponsalTipoDevolucion(String tipoDevolucion);
	
	List<PagoConciliadoEPA> findByCorresponsalTipoOperacion(String tipoOperacion);
	
	List<PagoConciliadoEPA> findByIdPagoIn(List<String> ids);
	
	PagoConciliadoEPA findByIdPago(String idPago);
	
	List<PagoConciliadoEPA> findByCorresponsalImporteBruto(BigDecimal importeBruto);
	

	
	
}
