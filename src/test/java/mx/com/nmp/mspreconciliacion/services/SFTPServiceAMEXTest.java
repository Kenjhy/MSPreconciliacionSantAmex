package mx.com.nmp.mspreconciliacion.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.given;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;

import mx.com.nmp.mspreconciliacion.MSPreConciliacionApplication;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.services.impl.SFTPServiceAMEXImpl;
import mx.com.nmp.mspreconciliacion.util.Util;

@SpringBootTest(classes = MSPreConciliacionApplication.class, properties="")
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SFTPServiceAMEXTest {

    private String usuarioSFTP;
    private String host;
    private int port;
    
    private final String LINEA_HEADER = "HEADER,20230207,21155,100,GRRCN,Nal Monte de Piedad,2.01,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
    private final String LINEA_SUMMARY = "SUMMARY,9352807862,2,511,20230210,MXN,,57400,70000,,0,0,0,0,,,2180700832559330,002      000000,,,,,,,,,,,,,,,,,,,,,,,";
    private final String LINEA_TRANSACTN = "TRANSACTN,9352807862,2,511,20230210,MXN,9352807862,20230207,20230207,0,MXN,NAL MONTE PIEDA,1234,,3001,,70000,70000,20230207,123425,100339460,440060,92490018,8398,,P63MLRM0050F3V0000000Z0,,,,,,,   ,0,0,3,3,1234,0,0,";
    private final String LINEA_TRANSACTN_ERROR = "TRANSACTN,9352807862,2,511,1234,MXN,9352807862,1234,1234,0,MXN,NAL MONTE PIEDA,1234,,3001,,70000,70000,1234,123425,100339460,440060,92490018,8398,,P63MLRM0050F3V0000000Z0,,,,,,,   ,0,0,0,0,1234,0,0,";
    private final String LINEA_TXNPRICING1 = "TXNPRICING,9352807862,2,511,20230210,MXN,9352807862,NAL MONTE PIEDA,,1234,,3001,70000,20230207,1A,,0,2000,14000000,,,,,,,,,,,,,,,,,,,,,,";
    private final String LINEA_TXNPRICING2 = "TXNPRICING,9352807862,2,511,20230210,MXN,9352807862,NAL MONTE PIEDA,,1234,,3001,70000,20230207,1A,,0,-016000,112000000,,,,,,,,,,,,,,,,,,,,,,";
    private final String LINEA_SUBMISSION = "SUBMISSION,9352807862,002,1000016726,20230412,MXN,9352809579,20230405,20230405,000000000000000,MXN,,000000100000000, 000000000995700,000000000995700,000000000019914,000000000000000,000000000003186,000000000972600,,,0000001,,6,0,20230412,20230410,0,000000000000000,000000000000000,000000000995700,000000000000000";
    private final String LINEA_SUMARY2 ="SUMMARY,9352808027,2,22,20230305,MXN,,82000,100000,,0,0,0,0,,,2180700832559330,002";
    private final String LINEA_ADJUSTMENT ="ADJUSTMENT,9352807862,002,511,20230412,MXN,9352807862,20230405,,,,,,20230405,000000000000000,MXN,000000000960045,ADJ1090003,AJUSTE ESPECIAL,000000000000000,000000000000000,000000000014709,000000000002353,-000000000017062,,000000,,";
    private final String LINEA_TRANSACTN_DEV = "TRANSACTN,9352807862,2,557,20230305,MXN,9352807862,20230208,20230208,0,MXN,NAL MONTE PIEDA,1234,,3001,,-209663,-209663,20230208,123425,100340934,416155,92490018,8398,,P63MLRM0050F3V0000000Z0,,,,,,,,0,0,0,0,1234,0,0,";
    private final String LINEA_CHARGEBACK ="CHARGEBACK,9352807862,2,559,20230208,MXN,9352807862,20230208,NAL MONTE PIEDA,135275,,3001,,20230208,000000000000000,MXN,100340938,ADJ3190009,BIENES/SERVICIOS NO RECIBIDOS,-400000,000000000000000,000000000000000,000000000000000,000000000000000,,000000,,,";

    
    private JSch jSch = mock(JSch.class);
    private Session session = mock(Session.class);
    private ChannelSftp sftp = mock(ChannelSftp.class);
    
    
	@Autowired
	private SFTPServiceAMEXImpl sFTPServiceAMEXImpl;     
    
    @BeforeEach
    void setUp() throws Exception{
    	
    	usuarioSFTP= sFTPServiceAMEXImpl.userAMEX;
    	host=  sFTPServiceAMEXImpl.hostAMEX;
    	port = sFTPServiceAMEXImpl.portAMEX;
    }
    
    @Test
    void leerArchivoAMEXTest() throws Exception {
		ChannelSftp.LsEntry lsEntry = mock(ChannelSftp.LsEntry.class);
		Vector<LsEntry> archivos = new Vector<>();
		archivos.add(lsEntry);
		
		String[] lineaArray = {LINEA_HEADER, LINEA_SUMMARY, LINEA_TRANSACTN, LINEA_TXNPRICING1, LINEA_TXNPRICING2, LINEA_SUBMISSION, LINEA_TRANSACTN_DEV, LINEA_CHARGEBACK, LINEA_SUMARY2, LINEA_ADJUSTMENT};
		InputStream is = new ByteArrayInputStream(String.join(System.lineSeparator(), Arrays.asList(lineaArray)).getBytes(StandardCharsets.UTF_8));
		
		when(lsEntry.getFilename()).thenReturn("NACIONALMONTEMEXA64608.GRRCN.230325");
		given(jSch.getSession(usuarioSFTP, host, port)).willReturn(session);
        when(session.openChannel("sftp")).thenReturn(sftp);
    	when(sftp.ls(any())).thenReturn( archivos);
    	when(sftp.get(any())).thenReturn(is);
    	
    	List<MovCorresponsalDTO> movs = sFTPServiceAMEXImpl.leerArchivo(Util.requestPreconciliacion(false), jSch );
    	Assertions.assertNotNull(movs);
    }
    
    @Test
    void leerArchivoAMEXVacioTest() throws Exception {
		ChannelSftp.LsEntry lsEntry = mock(ChannelSftp.LsEntry.class);
		Vector<LsEntry> archivos = new Vector<>();
		archivos.add(lsEntry);
		
		InputStream is = null;
		
		when(lsEntry.getFilename()).thenReturn("NACIONALMONTEMEXA64608.GRRCN.230325");
        given(jSch.getSession(usuarioSFTP, host, port)).willReturn(session);
        when(session.openChannel("sftp")).thenReturn(sftp);
    	when(sftp.ls(any())).thenReturn( archivos);
    	when(sftp.get(any())).thenReturn(is);
    	
    	try {
    		sFTPServiceAMEXImpl.leerArchivo(Util.requestPreconciliacion(false), jSch );
    	}catch(Exception ex) {
			Assertions.assertTrue(ex instanceof PagoException);
			Assertions.assertEquals(((PagoException)ex).getEstado(), PagoException.ERROR_AMEX_SANTANDER_FECHA.getEstado());
		}
    } 
    
    
    @Test
    void leerArchivoAMEXMapeoIncTest() throws Exception {
		ChannelSftp.LsEntry lsEntry = mock(ChannelSftp.LsEntry.class);
		Vector<LsEntry> archivos = new Vector<>();
		archivos.add(lsEntry);
		
		String[] lineaArray = {LINEA_HEADER, LINEA_SUMMARY, LINEA_TRANSACTN_ERROR, LINEA_TXNPRICING1, LINEA_TXNPRICING2};
		InputStream is = new ByteArrayInputStream(String.join(System.lineSeparator(), Arrays.asList(lineaArray)).getBytes(StandardCharsets.UTF_8));

		
		when(lsEntry.getFilename()).thenReturn("NACIONALMONTEMEXA64608.GRRCN.230325");
		given(jSch.getSession(usuarioSFTP, host, port)).willReturn(session);
        when(session.openChannel("sftp")).thenReturn(sftp);
    	when(sftp.ls(any())).thenReturn( archivos);
    	when(sftp.get(any())).thenReturn(is);
    	
    	try {
    		sFTPServiceAMEXImpl.leerArchivo(Util.requestPreconciliacion(false), jSch );
    	}catch(Exception ex) {
			Assertions.assertTrue(ex instanceof PagoException);
			Assertions.assertEquals(((PagoException)ex).getEstado(), PagoException.ERROR_AMEX_SFTP_PROC.getEstado());
		}
    }
    
    @Test
    void leerArchivoAMEXConexionIncTest() throws Exception {
		ChannelSftp.LsEntry lsEntry = mock(ChannelSftp.LsEntry.class);
		Vector<LsEntry> archivos = new Vector<>();
		archivos.add(lsEntry);
		
		String[] lineaArray = {LINEA_HEADER, LINEA_SUMMARY, LINEA_TRANSACTN, LINEA_TXNPRICING1, LINEA_TXNPRICING2};
		InputStream is = new ByteArrayInputStream(String.join(System.lineSeparator(), Arrays.asList(lineaArray)).getBytes(StandardCharsets.UTF_8));
		
		when(lsEntry.getFilename()).thenReturn("NACIONALMONTEMEXA64608.GRRCN.230325");
		given(jSch.getSession(usuarioSFTP, host, port)).willReturn(session);
        when(session.openChannel("sftp")).thenReturn(null);
    	when(sftp.ls(any())).thenReturn( archivos);
    	when(sftp.get(any())).thenReturn(is);
    	
    	try {
    		sFTPServiceAMEXImpl.leerArchivo(Util.requestPreconciliacion(false), jSch );
	    }catch(Exception ex) {
			Assertions.assertTrue(ex instanceof PagoException);
			Assertions.assertEquals(((PagoException)ex).getEstado(), PagoException.ERROR_AMEX_SFTP.getEstado());
		}
    }    
}
