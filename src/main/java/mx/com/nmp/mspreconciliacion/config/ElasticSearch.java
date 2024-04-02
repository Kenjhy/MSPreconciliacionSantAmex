package mx.com.nmp.mspreconciliacion.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Configuration
public class ElasticSearch extends AbstractFactoryBean<RestHighLevelClient> {

	 private static final Logger log = LoggerFactory.getLogger(ElasticSearch.class);
	    private RestHighLevelClient restHighLevelClient;
	    
	    @Value("${mspreconciliacion.elasticsearch.url}")
	    private String host;
	    
	    @Value("${mspreconciliacion.elasticsearch.user}")
	    private String user;
	    
	    @Value("${mspreconciliacion.elasticsearch.pwd}")
	    private String pwd;

	    @Override
	    public Class<RestHighLevelClient> getObjectType() {
	        return RestHighLevelClient.class;
	    }
	    @Override
	    protected RestHighLevelClient createInstance() {
	        return buildClient();
	    }
	    @Override
	    public boolean isSingleton() {
	        return false;
	    }

	    private RestHighLevelClient buildClient() {
	    	log.info("conectando con elastic");
	        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
	        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pwd));
	        log.info("conectando con elastic-user={}", user);
	        try {
	        	
	        	//Para evitar cambios en variables de entorno, se obtiene puerto y protocolo de url
	    		String[] separada= host.split(":");
	    		String protocol= null;
	    		String url= null;
	    		int port= 0;
	    		for(String s: separada) {
	    			if (s.startsWith("htt")) {
	    				protocol= s;
	    				log.info("conectando con elastic-protocol={}", s);
	    			}else if (s.startsWith("//")) {
	    				url = s.substring(2, s.length());
	    			}else {
	    				port= Integer.valueOf(s);
	    				log.info("conectando con elastic-port={}", port);
	    			}
	    		}
	            restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(url, port, protocol))
	                    .setHttpClientConfigCallback((HttpAsyncClientBuilder httpClientBuilder) -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)));
				log.info("conexion establecida");
	        } catch (IllegalArgumentException e) {
	        	log.error(e.getMessage());
	        }
	        return restHighLevelClient;
	    }
	    @Override
	    public void destroy() {
	        try {
	            if (restHighLevelClient != null) {
	                restHighLevelClient.close();
	            }
	        } catch (final IOException e) { logger.error("Error closing ElasticSearch client: ", e); }
	    }

}
