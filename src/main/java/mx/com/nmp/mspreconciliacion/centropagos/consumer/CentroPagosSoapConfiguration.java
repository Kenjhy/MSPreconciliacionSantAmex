package mx.com.nmp.mspreconciliacion.centropagos.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class CentroPagosSoapConfiguration {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller= new Jaxb2Marshaller();
		marshaller.setContextPath("mx.com.nmp.mspreconciliacion.centropagos.consumer.soap");
		return marshaller;
	}
	
	@Bean
	public CentroPagosSoapService centroPagosSoapService (Jaxb2Marshaller marshaller, CentroPagosSoapService centroPagosSoapService) {
		centroPagosSoapService.setMarshaller(marshaller);
		centroPagosSoapService.setUnmarshaller(marshaller);
		return centroPagosSoapService;
	}
}
