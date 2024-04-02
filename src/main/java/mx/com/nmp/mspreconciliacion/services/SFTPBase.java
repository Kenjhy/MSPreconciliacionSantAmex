package mx.com.nmp.mspreconciliacion.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.CharMatcher;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.util.ArchivoSFTPUtil;

public class SFTPBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SFTPBase.class);

	@Value(value = "${mspreconciliacion.variables.santanderSftp.host}")
	public String hostSantander;

	@Value(value = "${mspreconciliacion.variables.santanderSftp.port}")
	public int portSantander;

	@Value(value = "${mspreconciliacion.variables.santanderSftp.user}")
	public String userSantander;

	@Value(value = "${mspreconciliacion.variables.santanderSftp.pwd}")
	public String pwdSantander;

	@Value(value = "${mspreconciliacion.variables.santanderSftp.folder}")
	public String folderSantander;

	@Value(value = "${mspreconciliacion.variables.santanderSftp.folderReintento}")
	public String folderReintentoSantander;

	@Value(value = "${mspreconciliacion.variables.amexSftp.host}")
	public String hostAMEX;

	@Value(value = "${mspreconciliacion.variables.amexSftp.port}")
	public int portAMEX;

	@Value(value = "${mspreconciliacion.variables.amexSftp.user}")
	public String userAMEX;

	@Value(value = "${mspreconciliacion.variables.amexSftp.pwd}")
	public String pwdAMEX;

	@Value(value = "${mspreconciliacion.variables.amexSftp.folder}")
	public String folderAMEX;

	@Value(value = "${mspreconciliacion.variables.amexSftp.folderReintento}")
	public String folderReintentoAMEX;


	public ChannelSftp crearCanalSFTP(String user, String host, String pwd, int port, CorresponsalEnum corresponsal, JSch sftpCanal) throws PagoException {
		ChannelSftp canalSFTP= null;
		Properties config;

		LOGGER.info("Entra a método de crearCanalSFTP-SFTPBase para conexion a SFTP");
		try {

			Session session= null;
			session = sftpCanal.getSession(user, host, port);
			LOGGER.info("conexión sftp corresponsal={}, usuario={}",  corresponsal.getNombre(), user);
			LOGGER.info("conexión sftp host={}, port={}",  host, port);

			config = new Properties();
			config.setProperty("StrictHostKeyChecking", "no");
			config.setProperty("PreferredAuthentications", "password");

			session.setConfig(config);
			session.setPassword(pwd);
			session.connect(19000);
			Channel canal = session.openChannel("sftp");
			canal.connect(19000);
			canalSFTP= (ChannelSftp) canal;
			LOGGER.info("conexion SFTP exitosa");

		} catch(JSchException ex) {
			if (corresponsal.equals(CorresponsalEnum.SANTANDER)) {
				LOGGER.info("Entra a método de crearCanalSFTP-SFTPBase para conexion a SFTP-Error en conexión  Santander {}",ex.getMessage());
				throw PagoException.ERROR_SANTANDER_SFTP;
			}
			else {
				LOGGER.info("Entra a método de crearCanalSFTP-SFTPBase para conexion a SFTP-Error en conexión  AMEX {}", ex.getMessage());
				throw PagoException.ERROR_AMEX_SFTP;
			}
		}

		return canalSFTP;
	}



	protected void desconectarSFTP(ChannelSftp canalSFTP, CorresponsalEnum corresponsal) throws PagoException{
		try {
			LOGGER.info("Entra a SFTPBse- Desconexión Servidor- SFTP");
			if( canalSFTP == null)
				return;

			if(canalSFTP.isConnected())
				canalSFTP.disconnect();

			if(canalSFTP.getSession() != null)
				canalSFTP.getSession().disconnect();

		} catch(JSchException ex) {
			LOGGER.error("Error al desconectar SFTP de {}", corresponsal);

			if (corresponsal.equals(CorresponsalEnum.SANTANDER))
				throw PagoException.ERROR_SANTANDER_SFTP_DISC;
			else
				throw PagoException.ERROR_AMEX_SFTP_DISC;
		}
	}


	protected List<Stream<String>> obtenerArchivoSFTP(String nombreArchivo, ChannelSftp canalSFTP, int numeroReintento, String folder, String folderReintento) throws SftpException {
		List<Stream<String>> listaBr= null;
		
		//folder-outbox
		//folderReintento-sent
		canalSFTP.cd("/".concat(folder));

		@SuppressWarnings("unchecked")
		List<LsEntry> archivos = canalSFTP.ls("*.*");
		listaBr= buscarArchivo(archivos,nombreArchivo, canalSFTP);
		
		//cambio por quitar reintentos
		if (listaBr.isEmpty()) {
			///reintentar en carpeta de reintento, ya que AMEX mueve archivos despues de su primera ejecucion
			canalSFTP.cd("/".concat(folderReintento));
			List<LsEntry> archivosReintento = canalSFTP.ls("*.*");
			listaBr= buscarArchivo(archivosReintento,nombreArchivo, canalSFTP);
		}
		return listaBr;
	}

	private List<Stream<String>> buscarArchivo(List<LsEntry> archivos, String nombreArchivo, ChannelSftp canalSFTP) throws SftpException {
		List<Stream<String>> listaResult= new ArrayList<>();
		BufferedReader br= null;
		if (!archivos.isEmpty()) {
			for (LsEntry archivo : archivos)
			{
				if (archivo.getFilename().startsWith(nombreArchivo)){
					String nombreArchivoServ = archivo.getFilename();

					if (!nombreArchivoServ.endsWith("df_")) {
						InputStream is= canalSFTP.get(nombreArchivoServ);
						if (is != null && convierteBytesAkiloBytes(archivo.getAttrs().getSize()) > 0.9) {
							//Revisar si esta vacio
							br = new BufferedReader(new InputStreamReader(is));
							listaResult.add(br.lines());
						}
					}
				}
			}
		}
		return listaResult;
	}
	
	private double convierteBytesAkiloBytes(long bytes) {
		double tamKB; 
		
		//1 Bytes = 0.001 Kilobytes
		tamKB= bytes * 0.001;
		return tamKB;
	}
	

	protected String obtenerArchivoEjecutado(Date fecha, CorresponsalEnum corresponsal) {
		return ArchivoSFTPUtil.obtenerNombreArchivo(fecha, corresponsal);
	}

	protected String obtenerCadenaSinCerosIzquierda(String cadena) {
		String cadenaNumerica= null;

		if (cadena.startsWith("0")) {
			cadenaNumerica= CharMatcher.is('0').trimLeadingFrom(cadena);
		}else {
			cadenaNumerica= cadena;
		}
		return cadenaNumerica;

	}

}

