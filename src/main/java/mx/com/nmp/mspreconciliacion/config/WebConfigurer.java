package mx.com.nmp.mspreconciliacion.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Configuración de la aplicación web con APIs de Servlet 3.0
 *
 * @author Javier Hernandez
 */


@Configuration
public class WebConfigurer  implements WebMvcConfigurer, ServletContextInitializer, WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;


    /**
     * Inyección de dependencias por constructor
     *
     * @param env
     */
    public WebConfigurer(Environment env) {
        this.env = env;
    }

    /**
     * Configure the given {@link ServletContext} with any servlets, filters, listeners
     * context-params and attributes necessary for initialization.
     *
     * @param servletContext the {@code ServletContext} to initialize
     * @throws ServletException if any call against the given {@code ServletContext}
     *                          throws a {@code ServletException}
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (env.getActiveProfiles().length != 0) {
            log.info("Configuracion de la aplicacion Web, utilizando profiles: {}", (Object[]) env.getActiveProfiles());
        }
        log.info("Aplicacion Web configurada");
    }

    /**
     * Customize the specified {@link WebServerFactoryCustomizer}.
     *
     * @param container the container to customize
     */
    @Override
    public void customize(ConfigurableServletWebServerFactory container) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
        mappings.add("html", "text/html;charset=utf-8");
        // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
        mappings.add("json", "text/html;charset=utf-8");
        container.setMimeMappings(mappings);
    }



    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	 registry.addMapping("/**");
    }
}
