package mx.com.nmp.mspreconciliacion.consumer.rest;


import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import mx.com.nmp.mspreconciliacion.config.ApplicationProperties;
import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.consumer.rest.dto.BusRestAuthDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.dto.BusRestHeaderDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.dto.BusRestTokenReponseDTO;
import mx.com.nmp.mspreconciliacion.exceptions.SistemaException;

/**
 * 
 * @name AuthRestService
 * @description Clase que consume el servicio de Autorización para generar token para OAG
 * @author QuarkSoft
 */
public abstract class AuthRestService {


	protected static final Logger LOG = LoggerFactory.getLogger(AuthRestService.class);

	@Value(value = "${mspreconciliacion.variables.auth.headerIdConsumidor}")
	public String idConsumidor;
	
	@Value(value = "${mspreconciliacion.variables.auth.headerIdDestino}")
	public String idDestino;
	
	@Autowired
	protected ApplicationProperties applicationProperties;

	@Autowired
	protected RetryTemplate retryTemplate;

	protected AuthRestService() {
		super();
	}

	/**
	 * Consume servicio de endpoint para obtencion de token
	 * 
	 * @param auth
	 * @return
	 */
	public String postForGetToken(final BusRestAuthDTO auth, String url) throws SistemaException {

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "client_credentials");
		map.add("scope", "UserProfile.me");

		HttpHeaders headers = createHeadersToken(auth);
		HttpEntity<MultiValueMap<String, String>> request2 = new HttpEntity<>(map,
				headers);

		// Retries
		BusRestTokenReponseDTO obj = retryTemplate
				.execute(new RetryCallback<BusRestTokenReponseDTO, SistemaException>() {
					public BusRestTokenReponseDTO doWithRetry(RetryContext context) throws SistemaException {
						RestTemplate restTemplate = new RestTemplate();
						ResponseEntity<BusRestTokenReponseDTO> response = null;
						LOG.info("postForGetToken: {}, intento: #{}", url, context.getRetryCount());
						try {
							response = restTemplate.exchange(url, HttpMethod.POST, request2,
									BusRestTokenReponseDTO.class);
						} catch (Exception ex) {
							LOG.error(Constants.GENERIC_EXCEPTION_INITIAL_MESSAGE, ex);
							throw SistemaException.ERROR_TOKEN_COMISIONESOAGMIDAS_REQUEST;
						}
						return response.getBody();
					}
				});

		String bearerToken = null != obj && null != obj.getAccessToken() ? obj.getAccessToken() : null;

		if (null == bearerToken || "".equals(bearerToken))
			throw SistemaException.ERROR_TOKEN_COMISIONESOAGMIDAS_REQUEST;

		return bearerToken;
	}


	/**
	 * Construye los headers para el endpoint de obtencion del token
	 * 
	 * @param auth
	 * @return
	 */
	private HttpHeaders createHeadersToken(BusRestAuthDTO auth) {
		String base64Creds = buildBase64Hash(auth);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		headers.add("idConsumidor", idConsumidor);
		headers.add("idDestino", idDestino);
		headers.add("usuario", "usuario");
		return headers;
	}

	/**
	 * Construye los headers para el consumo del enpoint
	 * 
	 * @param auth
	 * @return
	 */
	protected abstract HttpHeaders createHeadersPostTo(BusRestAuthDTO auth, BusRestHeaderDTO header);

	/**
	 * Build base 64 hash para autenticacion basic
	 * 
	 * @param auth
	 * @return
	 */
	protected String buildBase64Hash(BusRestAuthDTO auth) {
		String plainCreds = auth.getUser().concat(":").concat(auth.getPassword());
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		return new String(base64CredsBytes);
	}


}
