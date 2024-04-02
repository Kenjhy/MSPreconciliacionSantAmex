package mx.com.nmp.mspreconciliacion.config;

import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Propiedades de configuración de la aplicación
 *
 * @author Javier Hernandez
 */
@Configuration
@ConfigurationProperties(prefix = "", ignoreUnknownFields = true)
public class ApplicationProperties {

	private final CorsConfiguration cors = new CorsConfiguration();


	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
		return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
	}

	public CorsConfiguration getCors() {
		return cors;
	}

	private MsPreconciliacion mspreconciliacion;

	public MsPreconciliacion getMsPreconciliacion() {
		return mspreconciliacion;
	}

	public void setMsPreconciliacion(MsPreconciliacion mspreconciliacion) {
		this.mspreconciliacion = mspreconciliacion;
	}

	public static class MsPreconciliacion {

		private Variables variables;
		private ElasticSearch elasticsearch;

		
		public ElasticSearch getElasticsearch() {
			return elasticsearch;
		}

		public void setElasticsearch(ElasticSearch elasticsearch) {
			this.elasticsearch = elasticsearch;
		}

		public Variables getVariables() {
			return variables;
		}

		public void setVariables(Variables variables) {
			this.variables = variables;
		}

		public static class ElasticSearch{
			private String url;
			private String user;
			private String pwd;
			
			public String getUrl() {
				return url;
			}
			public void setUrl(String url) {
				this.url = url;
			}
			public String getUser() {
				return user;
			}
			public void setUser(String user) {
				this.user = user;
			}
			public String getPwd() {
				return pwd;
			}
			public void setPwd(String pwd) {
				this.pwd = pwd;
			}
		}
		
		public static class Variables {
			private CentroPagosWSInfo centroPagosWSInfo;
			private DatoSftp santanderSftp;	
			private DatoSftp amexSftp;
			private String headerApiKeyPagosPrendarios;
			private String apiKeyPagosPrendarios;
			private Auth auth;
			private ComisionesMIDAS comisionesMIDAS;
			private Integer numeroDiasRestaWSOnlinePre;
			private Boolean habilitarPrePagoNoEncontradoA15;

			public Integer getNumeroDiasRestaWSOnlinePre() {
				return numeroDiasRestaWSOnlinePre;
			}

			public void setNumeroDiasRestaWSOnlinePre(Integer numeroDiasRestaWSOnlinePre) {
				this.numeroDiasRestaWSOnlinePre = numeroDiasRestaWSOnlinePre;
			}

			public DatoSftp getSantanderSftp() {
				return santanderSftp;
			}

			public void setSantanderSftp(DatoSftp santanderSftp) {
				this.santanderSftp = santanderSftp;
			}

			public DatoSftp getAmexSftp() {
				return amexSftp;
			}

			public void setAmexSftp(DatoSftp amexSftp) {
				this.amexSftp = amexSftp;
			}

			public CentroPagosWSInfo getCentroPagosWSInfo() {
				return centroPagosWSInfo;
			}

			public void setCentroPagosWSInfo(CentroPagosWSInfo centroPagosWSInfo) {
				this.centroPagosWSInfo = centroPagosWSInfo;
			}

			public static class DatoSftp {
				private String host;
				private int port;
				private String user;
				private String pwd;
				private String folder;
				private String folderReintento;
				
				public String getHost() {
					return host;
				}
				public void setHost(String host) {
					this.host = host;
				}
				public int getPort() {
					return port;
				}
				public void setPort(int port) {
					this.port = port;
				}
				public String getUser() {
					return user;
				}
				public void setUser(String user) {
					this.user = user;
				}
				public String getPwd() {
					return pwd;
				}
				public void setPwd(String pwd) {
					this.pwd = pwd;
				}
				public String getFolder() {
					return folder;
				}
				public void setFolder(String folder) {
					this.folder = folder;
				}
				public String getFolderReintento() {
					return folderReintento;
				}
				public void setFolderReintento(String folderReintento) {
					this.folderReintento = folderReintento;
				}
				
			}
			
		
			public static class CentroPagosWSInfo {
				
				private String user;
				private String pwd;
				private String company;
				private String url;
				private String llaveNMP;
				private String datoFijoNMP;
				private Boolean habilitarCentroPagos;
				
				public String getUrl() {
					return url;
				}
				public void setUrl(String url) {
					this.url = url;
				}
				public String getLlaveNMP() {
					return llaveNMP;
				}
				public void setLlaveNMP(String llaveNMP) {
					this.llaveNMP = llaveNMP;
				}
				public String getDatoFijoNMP() {
					return datoFijoNMP;
				}
				public void setDatoFijoNMP(String datoFijoNMP) {
					this.datoFijoNMP = datoFijoNMP;
				}
				public String getUser() {
					return user;
				}
				public void setUser(String user) {
					this.user = user;
				}
				public String getPwd() {
					return pwd;
				}
				public void setPwd(String pwd) {
					this.pwd = pwd;
				}
				public String getCompany() {
					return company;
				}
				public void setCompany(String company) {
					this.company = company;
				}
				public Boolean isHabilitarCentroPagos() {
					return habilitarCentroPagos;
				}
				public void setHabilitarCentroPagos(Boolean habilitarCentroPagos) {
					this.habilitarCentroPagos = habilitarCentroPagos;
				}
			}
			
			public static class Auth {
				private String usuario;
				private String pwd;
				private String url;
				private String headerIdConsumidor;
				private String headerIdDestino;
				private int maxAttempt;
				private int retryTimeInterval;
				
				public String getUsuario() {
					return usuario;
				}
				public void setUsuario(String usuario) {
					this.usuario = usuario;
				}

				public String getPwd() {
					return pwd;
				}
				public void setPwd(String pwd) {
					this.pwd = pwd;
				}
				public String getUrl() {
					return url;
				}
				public void setUrl(String url) {
					this.url = url;
				}
				public String getHeaderIdConsumidor() {
					return headerIdConsumidor;
				}
				public void setHeaderIdConsumidor(String headerIdConsumidor) {
					this.headerIdConsumidor = headerIdConsumidor;
				}
				public String getHeaderIdDestino() {
					return headerIdDestino;
				}
				public void setHeaderIdDestino(String headerIdDestino) {
					this.headerIdDestino = headerIdDestino;
				}
				public int getMaxAttempt() {
					return maxAttempt;
				}
				public void setMaxAttempt(int maxAttempt) {
					this.maxAttempt = maxAttempt;
				}
				public int getRetryTimeInterval() {
					return retryTimeInterval;
				}
				public void setRetryTimeInterval(int retryTimeInterval) {
					this.retryTimeInterval = retryTimeInterval;
				}
			}
			
			public static class ComisionesMIDAS {
				private String url;

				public String getUrl() {
					return url;
				}

				public void setUrl(String url) {
					this.url = url;
				}
			}

			public String getApiKeyPagosPrendarios() {
				return apiKeyPagosPrendarios;
			}

			public void setApiKeyPagosPrendarios(String apiKeyPagosPrendarios) {
				this.apiKeyPagosPrendarios = apiKeyPagosPrendarios;
			}

			public String getHeaderApiKeyPagosPrendarios() {
				return headerApiKeyPagosPrendarios;
			}

			public void setHeaderApiKeyPagosPrendarios(String headerApiKeyPagosPrendarios) {
				this.headerApiKeyPagosPrendarios = headerApiKeyPagosPrendarios;
			}

			public Auth getAuth() {
				return auth;
			}

			public void setAuth(Auth auth) {
				this.auth = auth;
			}

			public ComisionesMIDAS getComisionesMIDAS() {
				return comisionesMIDAS;
			}

			public void setComisionesMIDAS(ComisionesMIDAS comisionesMIDAS) {
				this.comisionesMIDAS = comisionesMIDAS;
			}

			public Boolean getHabilitarPrePagoNoEncontradoA15() {
				return habilitarPrePagoNoEncontradoA15;
			}

			public void setHabilitarPrePagoNoEncontradoA15(Boolean habilitarPrePagoNoEncontradoA15) {
				this.habilitarPrePagoNoEncontradoA15 = habilitarPrePagoNoEncontradoA15;
			}
		}

	}

}
