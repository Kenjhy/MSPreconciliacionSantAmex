package mx.com.nmp.mspreconciliacion.services.impl;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PreconciliacionExcetion;
import mx.com.nmp.mspreconciliacion.model.dto.RequestEjecucionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RestPreconcilacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.EstadoEjecucionEnum;
import mx.com.nmp.mspreconciliacion.services.PagoPrendarioRestService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.RetryContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.RetryCallback;

@Component("pagoPrendarioRestService")
public class PagoPrendarioRestServiceImpl implements PagoPrendarioRestService {

    private static final Logger LOG = LoggerFactory.getLogger(PagoPrendarioRestServiceImpl.class);

    public PagoPrendarioRestServiceImpl() {
        super();
    }

    @Value(value = "${mspreconciliacion.variables.ms.pagos_predarios.url}")
    public String url;

    
    @Value(value = "${mspreconciliacion.variables.apiKeyPagosPrendarios}")
    public String apiKeyPagosPrendarios;
    

    @Value(value = "${mspreconciliacion.variables.headerApiKeyPagosPrendarios}")
    public String headerApiKeyPagosPrendarios;
    
    @Autowired
    protected RetryTemplate retryTemplate;
    
    @Autowired
    protected RestTemplate restTemplate;
    


    @Override
    public RestPreconcilacionDTO updateEstatusEjecucionPreconciliacion(Long idEjecucion, EstadoEjecucionEnum estado, String updatedBy) throws PreconciliacionExcetion{
        RestPreconcilacionDTO response = null;
        response = postForObject(idEjecucion, estado, updatedBy, url);
        return response;
    }

    public RestPreconcilacionDTO postForObject(Long idEjecucionProceso, EstadoEjecucionEnum estadoEjecucion,String updatedBy, String url) throws PreconciliacionExcetion{

        RequestEjecucionDTO ejecucionDTO = new RequestEjecucionDTO(idEjecucionProceso, estadoEjecucion);

        HttpHeaders headers = createHeadersPostTo(updatedBy);
        HttpEntity<RequestEjecucionDTO> request = new HttpEntity<>(ejecucionDTO, headers);

        return retryTemplate.execute(new RetryCallback<RestPreconcilacionDTO, PreconciliacionExcetion>() {
            public RestPreconcilacionDTO doWithRetry(RetryContext context) throws PreconciliacionExcetion {
                ResponseEntity<RestPreconcilacionDTO> resp = null;
                LOG.info("postForObject: {}, intento: #{}", url, context.getRetryCount());
                try {
                    resp = restTemplate.exchange(url, HttpMethod.POST, request, RestPreconcilacionDTO.class);
                } catch (RestClientException ex) {
                	LOG.error(Constants.GENERIC_EXCEPTION_INITIAL_MESSAGE, ex);
                }
                return resp!= null? resp.getBody(): null;
            }
        });
    }

    public HttpHeaders createHeadersPostTo(String updatedBy) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("requestUser", updatedBy);
		headers.add(headerApiKeyPagosPrendarios,apiKeyPagosPrendarios);        
        return headers;
    }

}
