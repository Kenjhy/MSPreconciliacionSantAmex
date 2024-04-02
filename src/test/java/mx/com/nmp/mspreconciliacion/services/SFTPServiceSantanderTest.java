package mx.com.nmp.mspreconciliacion.services;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.services.impl.SFTPServiceSantanderImpl;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import mx.com.nmp.mspreconciliacion.util.Util;

@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SFTPServiceSantanderTest {

    private String usuarioSFTP;
    private String host;
    private int port;
    private final String LINEA_VENTA_CONTADO = "283102752|0134|1-20230328092745|3FAAXAXU0|mpay|Contado|||1153||749948|8679485|NMP SUCURAL 800|10000.00|MXN|28/03/2023|09:28:15|C|VISA|AZTECA|VENTA|29/03/2023";
    private final String LINEA_DEVOLUCION_MESES = "283370995|0134|1-20230328111848|3FAAXAXU0|mpay|3Meses|||5930|/|056947|8679485|NMP SUCURAL 800|2557.70|MXN|28/03/2023|11:19:20|D|MASTERCARD|BBVA|DEVOLUCION|29/03/2023";
    private final String LINEA_VENTA_FECHA_INCORRECTA = "283102752|0134|1-20230328092745|3FAAXAXU0|mpay|Contado|||1153||749948|8679485|NMP SUCURAL 800|10000.00|MXN|28/03/2023|09:28:15|C|VISA|AZTECA|VENTA|ValorIncorrecto";
    
    private JSch jSch = mock(JSch.class);
    private Session session = mock(Session.class);
    private ChannelSftp sftp = mock(ChannelSftp.class);
    
    
	@Autowired
	private SFTPServiceSantanderImpl sFTPServiceSantanderImpl;


    @BeforeEach
    void setUp() throws Exception{
    	
    	usuarioSFTP= sFTPServiceSantanderImpl.userSantander;
    	host=  sFTPServiceSantanderImpl.hostSantander;
    	port = sFTPServiceSantanderImpl.portSantander;
    }

    @Test
    void leerArchivoSantanderTest() throws Exception {
		ChannelSftp.LsEntry lsEntry = mock(ChannelSftp.LsEntry.class);
		Vector<LsEntry> archivos = new Vector<>();
		archivos.add(lsEntry);
		
		String[] lineaArray = {LINEA_VENTA_CONTADO, LINEA_DEVOLUCION_MESES};
		InputStream is = new ByteArrayInputStream(String.join(System.lineSeparator(), Arrays.asList(lineaArray)).getBytes(StandardCharsets.UTF_8));
		
		when(lsEntry.getFilename()).thenReturn("prva3FAA250323");
        given(jSch.getSession(usuarioSFTP, host, port)).willReturn(session);
        when(session.openChannel("sftp")).thenReturn(sftp);
    	when(sftp.ls(any())).thenReturn( archivos);
    	when(sftp.get(any())).thenReturn(is);
    	
    	List<MovCorresponsalDTO> movs = sFTPServiceSantanderImpl.leerArchivo(Util.requestPreconciliacion(true), jSch );
    	Assertions.assertNotNull(movs);
    }
    
    @Test
    void leerArchivoSantanderVacioTest() throws Exception {
		ChannelSftp.LsEntry lsEntry = mock(ChannelSftp.LsEntry.class);
		Vector<LsEntry> archivos = new Vector<>();
		archivos.add(lsEntry);
		
		InputStream is = null;
		
		when(lsEntry.getFilename()).thenReturn("prva3FAA250323");
		given(jSch.getSession(usuarioSFTP, host, port)).willReturn(session);
        when(session.openChannel("sftp")).thenReturn(sftp);
    	when(sftp.ls(any())).thenReturn( archivos);
    	when(sftp.get(any())).thenReturn(is);
    	
    	try {
    			sFTPServiceSantanderImpl.leerArchivo(Util.requestPreconciliacion(true), jSch );
    	}catch(Exception ex) {
			Assertions.assertTrue(ex instanceof PagoException);
			Assertions.assertEquals(((PagoException)ex).getEstado(), PagoException.ERROR_AMEX_SANTANDER_FECHA.getEstado());
		}
    }
    
    @Test
    void leerArchivoSantanderMapeoIncTest() throws Exception {
		ChannelSftp.LsEntry lsEntry = mock(ChannelSftp.LsEntry.class);
		Vector<LsEntry> archivos = new Vector<>();
		archivos.add(lsEntry);
		
		String[] lineaArray = {LINEA_VENTA_FECHA_INCORRECTA};
		InputStream is = new ByteArrayInputStream(String.join(System.lineSeparator(), Arrays.asList(lineaArray)).getBytes(StandardCharsets.UTF_8));
		
		when(lsEntry.getFilename()).thenReturn("prva3FAA250323");
		given(jSch.getSession(usuarioSFTP, host, port)).willReturn(session);
        when(session.openChannel("sftp")).thenReturn(sftp);
    	when(sftp.ls(any())).thenReturn( archivos);
    	when(sftp.get(any())).thenReturn(is);
    	
    	try {
    		List<MovCorresponsalDTO> movs = sFTPServiceSantanderImpl.leerArchivo(Util.requestPreconciliacion(true), jSch );
    	}catch(Exception ex) {
			Assertions.assertTrue(ex instanceof PagoException);
			Assertions.assertEquals(((PagoException)ex).getEstado(), PagoException.ERROR_AMEX_SFTP_PROC.getEstado());
		}

    }    
    
}
