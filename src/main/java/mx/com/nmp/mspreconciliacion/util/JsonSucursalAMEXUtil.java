package mx.com.nmp.mspreconciliacion.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.model.dto.SucursalAMEXDTO;

/**
 * Clase que obtiene el número de sucursal de acuerdo al número de establecimiento
 * que AMEX maneja en el archivo GRCCN
 * Se utiliza catálogo utilizado por Negocio, depósitado en amexEstablecimientos.json
 * @author QuarkSoft
 *
 */
public class JsonSucursalAMEXUtil {
	
	 
	 private static List<SucursalAMEXDTO> establecimientoList;
	
	 private static final Logger LOG = LoggerFactory.getLogger(JsonSucursalAMEXUtil.class);
	 
	 private JsonSucursalAMEXUtil() {}
	
	 public static List<SucursalAMEXDTO> cargarJSONEstablecimientoAMEX() {
	
		if (establecimientoList == null) {
		    try {
		    	ObjectMapper objectMapper = new ObjectMapper();
				ClassPathResource resource = new ClassPathResource("/amexEstablecimientos.json", SucursalAMEXDTO.class);
				InputStream is=  resource.getInputStream();
				SucursalAMEXDTO[] establecimiento= objectMapper.readValue(is, SucursalAMEXDTO[].class);
				establecimientoList= Arrays.asList(establecimiento);

		    } catch (IOException e) {
		    	LOG.error(Constants.GENERIC_EXCEPTION_INITIAL_MESSAGE, e);
			}
		}
		return establecimientoList;
	}
	
	public static String consultarSucursal(String establecimiento) {
		String sucursal= null;
		if (establecimientoList != null && establecimiento != null) {
			Optional<SucursalAMEXDTO> sucursalValor =establecimientoList.stream().filter(est-> est.getEstablecimiento().equals(establecimiento)).findFirst();
			if (sucursalValor.isPresent()) {
				sucursal = sucursalValor.get().getSucursal();
			}
		}
		return sucursal;
	}

}
