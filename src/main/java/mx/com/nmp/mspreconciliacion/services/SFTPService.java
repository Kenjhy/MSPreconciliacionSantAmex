package mx.com.nmp.mspreconciliacion.services;

import java.util.List;

import com.jcraft.jsch.JSch;

import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;



public interface SFTPService {

	
	public List<MovCorresponsalDTO> leerArchivo(PreConciliacionDTO preConciliacionDTO, JSch sftpCanal) throws PagoException;
}
