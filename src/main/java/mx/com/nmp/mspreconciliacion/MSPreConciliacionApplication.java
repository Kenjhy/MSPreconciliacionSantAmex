package mx.com.nmp.mspreconciliacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import mx.com.nmp.mspreconciliacion.config.ApplicationProperties;



/**
 * Nombre: MSConciliacionApplication
 * Descripcion: Se toma como plantilla MS-Pagos-Prendarios para éste nuevo MS
 * Clase principal de arranque, utiliza las anotaciones pertinentes para realizar la autoconfiguracion
 * 
 */

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class MSPreConciliacionApplication {


    public static void main(String[] args) {
		SpringApplication.run(MSPreConciliacionApplication.class);
    }

    @Bean
    public RetryTemplate getRetryTemplate() {
        return new RetryTemplate();
    }
    
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
