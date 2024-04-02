package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;



@XmlRootElement(name = "transacciones")
public class MovCentroPagosDTO {
	
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<MovCorresponsalDTO>  transaccion = new ArrayList<>();

	public List<MovCorresponsalDTO> getTransaccion() {
		return transaccion;
	}

	public void setTransaccion(List<MovCorresponsalDTO> transaccion) {
		this.transaccion = transaccion;
	}
	

}
