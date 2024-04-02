package mx.com.nmp.mspreconciliacion.consumer.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.RequestComisionesMIDAsDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.ResponseComisionesMIDAsDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.dto.BusRestAuthDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.dto.BusRestHeaderDTO;
import mx.com.nmp.mspreconciliacion.exceptions.SistemaException;

/**
 *
 * @name ComisionesMIDAsService
 * @description Clase que consume el servicio de OAG-Comisiones MIDAS
 * @author QuarkSoft
 */
@Component("comisionesMIDAsService")
public class ComisionesMIDAsService extends AuthRestService {

	@Value(value = "${mspreconciliacion.variables.comisionesMIDAS.url}")
	public String url;

	@Value(value = "${mspreconciliacion.variables.auth.url}")
	public String ulrtAuth;

	@Value(value = "${mspreconciliacion.variables.auth.usuario}")
	public String user;

	@Value(value = "${mspreconciliacion.variables.auth.pwd}")
	public String pwd;

	public static final Logger LOGGER = LoggerFactory.getLogger(ComisionesMIDAsService.class);


	public ResponseComisionesMIDAsDTO consultarComisionesMIDAS(RequestComisionesMIDAsDTO filtro) throws SistemaException {
		ResponseComisionesMIDAsDTO respuesta = new ResponseComisionesMIDAsDTO();
		RestTemplate restTemplate= null;
		ResponseEntity<ResponseComisionesMIDAsDTO> responseMIDAS = null;

		try {
			BusRestAuthDTO auth = new BusRestAuthDTO(user, pwd);
	
			String bearerToken = postForGetToken(auth, ulrtAuth);
			LOGGER.info("token para WS-OAG-MIDAS {}", bearerToken);
			//se usa el token para consumir WSMidas publicado en OAG
			BusRestHeaderDTO header = new BusRestHeaderDTO(bearerToken);
			HttpHeaders headers = createHeadersPostTo(auth, header);
			HttpEntity<?> entity = new HttpEntity<>(filtro, headers);
		
			restTemplate = new RestTemplate();
			responseMIDAS = restTemplate.exchange(url, HttpMethod.POST, entity, ResponseComisionesMIDAsDTO.class);
			respuesta= responseMIDAS.getBody();
			String respuestaJ= new Gson().toJson(respuesta);
			LOGGER.info("Resultado WS-OAG-MIDAS {}",respuestaJ);

		}catch (SistemaException ex) {
			LOGGER.info("ERROR WS-OAG-MIDAS: {}", ex.getMessage()+ ex.getCause());
			LOGGER.error(ex.getMessage());
		}
		return respuesta;
	}


	protected HttpHeaders createHeadersPostTo(BusRestAuthDTO auth, BusRestHeaderDTO header) {
		String base64Creds = buildBase64Hash(auth);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.add("Content-Type", "application/json");
		headers.add("idConsumidor", idConsumidor);
		headers.add("idDestino", idDestino);
		headers.add("usuario", "usuario");
		headers.add("oauth.bearer", header.getBearerToken());
		return headers;
	}

}
