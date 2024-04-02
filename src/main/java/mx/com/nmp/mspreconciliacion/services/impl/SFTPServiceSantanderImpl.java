package mx.com.nmp.mspreconciliacion.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpException;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.services.SFTPBase;
import mx.com.nmp.mspreconciliacion.services.SFTPService;
import mx.com.nmp.mspreconciliacion.util.FechaUtil;
import mx.com.nmp.mspreconciliacion.util.Utils;

@Service
public class SFTPServiceSantanderImpl extends SFTPBase implements SFTPService  {


	private static final Logger LOGGER = LoggerFactory.getLogger(SFTPServiceSantanderImpl.class);




	@Override
	public List<MovCorresponsalDTO> leerArchivo(PreConciliacionDTO preConciliacionDTO, JSch sftpCanal) throws PagoException{
		//prva0003270721
		//p   - Pagos
		//r   - Reporte
		//va  - Marca de tarjeta (Visa/MasterCard/Amex) = va
		//0003- Número de empresa asignado por Centro de Pagos
		//27  - Dia generación archivo
		//07  - Mes generación archivo
		//21  - Año generación archivo
		String nombreArchivo = null;
		List<MovCorresponsalDTO> movimientosList= null;

		LOGGER.info("Entra a método de leerArchivo-SFTPServiceSantanderImpl");
		ChannelSftp canalSFTP = crearCanalSFTP(userSantander,hostSantander,pwdSantander, portSantander,CorresponsalEnum.SANTANDER, sftpCanal);

		try {
			nombreArchivo= obtenerArchivoEjecutado(preConciliacionDTO.getFecha(), CorresponsalEnum.SANTANDER);
			List<Stream<String>> lineasArchivo =obtenerArchivoSFTP(nombreArchivo, canalSFTP, preConciliacionDTO.getNumeroReintento(), folderSantander, folderReintentoSantander);
			if (lineasArchivo.isEmpty()) {
				LOGGER.info(PagoException.ERROR_AMEX_SANTANDER_FECHA.getDescripcion());
				throw PagoException.ERROR_AMEX_SANTANDER_FECHA;
			}
			else {
				for(Stream<String> l: lineasArchivo) {
					movimientosList= procesarTransacciones(l, nombreArchivo);
					LOGGER.info("Movimientos obtenidos de lectura Santander {}", movimientosList.size());
				}
			}

		}catch(SftpException e) {
			LOGGER.info("Error en lectura archivos-SFTP, {0}", e);
			throw PagoException.ERROR_AMEX_SFTP;
		}finally {
			desconectarSFTP(canalSFTP, CorresponsalEnum.SANTANDER);
		}

		return movimientosList;
	}


	private List<MovCorresponsalDTO> procesarTransacciones(Stream<String> lineasArchivo, String nombreArchivoServ)throws PagoException {
		List<MovCorresponsalDTO> movimientosList= new ArrayList<>();
		List<MovCorresponsalDTO> movs= new ArrayList<>();
		List<MovCorresponsalDTO> movsFiltrados= new ArrayList<>();
		try {
			lineasArchivo.forEach(line-> {
				String[] datosLinea=  line.split("\\|");
				if (datosLinea.length > 0) {
					MovCorresponsalDTO movDTO = null;
					movDTO = mapearMovimiento(datosLinea);
					movDTO.setNombreArchivo(nombreArchivoServ);

					//Devolución
					if (movDTO.getTipoOperacion().equals(Constants.TIPO_OPERACION_DEVOLUCION)) {
						//Las Devoluciones automaticas se dejan como solicitadas, a espera que usuario liquide por Front
						movDTO.setTipoDevolucion(Constants.TIPO_DEVOLUCION_AUTOMATICA);
						movDTO.setEstatusDevolucion(Constants.ESTATUS_DEVOLUCION_SOLICITADA);
						movDTO.setEstatusDevolucionId(Constants.ESTATUS_DEVOLUCION_SOLICITADA_ID);
						movDTO.setFechaDevolucion(movDTO.getFechaDeposito());
						movDTO.setFechaLiquidacion(movDTO.getFechaDeposito()); //Se establece fecha de liquidación reportada por Santander
						movDTO.setImporteDevolucion(movDTO.getImporteBruto());
					}else if(!movDTO.getTipoOperacion().equals(Constants.TIPO_OPERACION_CANCELA)) {
						
						//Para reconocer pagos de AMEX en corresponsal Santander
						if (movDTO.getMarcaTarjeta().equals(Constants.CORRESPONSAL_AMEX)){
							movDTO.setEsAMEX(true);
						}
						movDTO.setNombreArchivo(nombreArchivoServ);
						movimientosList.add(movDTO);
					}
				}

			});
			
			movsFiltrados.addAll(movimientosList.stream().filter(mov-> !mov.isEsAMEX() ).collect(Collectors.toList()));
			movsFiltrados.addAll(movimientosList.stream().filter(mov-> mov.isEsAMEX() && mov.getTipoOperacion().equals(Constants.TIPO_OPERACION_VENTA)).collect(Collectors.toList()));
			

		}catch(Exception ex) {
			LOGGER.info("La estructura del archivo a procesar es incorrecta {0}", ex);
			throw PagoException.ERROR_AMEX_SFTP_PROC;
		}

		if (!movsFiltrados.isEmpty()) {
			movs= formatearMovimientos(movsFiltrados);
		}

		return movs;
	}

	private MovCorresponsalDTO mapearMovimiento(String[] datosLinea) {
		MovCorresponsalDTO movDTO= null;
		int indice= 0;
		if (datosLinea != null) {
			movDTO = new MovCorresponsalDTO();
			String numeroOp= datosLinea[indice++];
			movDTO.setNumeroOperacion(numeroOp.replaceAll("[^a-zA-Z0-9]", ""));
			movDTO.setSucursal(datosLinea[indice++]);
			movDTO.setReferencia(datosLinea[indice++]);
			movDTO.setUsuario(datosLinea[indice++]);
			movDTO.setUsrTrx(datosLinea[indice++]);
			movDTO.setTipoPago(datosLinea[indice++]);

			//Revisar si el tipo de pago es a MSI
			String tieneMeses = movDTO.getTipoPago();
			if (tieneMeses.contains("Meses")) {
				//Tipo pago MSI
				String[] plazoMeses= tieneMeses.split("Meses");
				String plazo = plazoMeses[0];
				movDTO.setNumeroMesesPromocion(Integer.valueOf(plazo));

				//Aplicar formato meses+ MSI
				movDTO.setTipoPago(plazo + " MSI");
			}

			movDTO.setLote(datosLinea[indice++]);
			movDTO.setNombreArchivo(datosLinea[indice++]);
			movDTO.setTarjeta(datosLinea[indice++]);
			movDTO.setNombreTH(datosLinea[indice++]);
			movDTO.setAutorizacion(datosLinea[indice++]);
			movDTO.setAfiliacion(datosLinea[indice++]);
			movDTO.setNombreAfiliacion(datosLinea[indice++]);
			movDTO.setSucursal(Utils.obtenerSucursalDeNombreAfiliacionSantander(movDTO.getNombreAfiliacion()));

			BigDecimal importe = new BigDecimal(datosLinea[indice++]);
			movDTO.setImporteBruto(importe);
			movDTO.setMoneda(datosLinea[indice++]);

			String fechaOp = datosLinea[indice++];
			movDTO.setFechaOperacionCadena(fechaOp);
			movDTO.setFechaOperacion(FechaUtil.convierteaFecha(fechaOp, FechaUtil.FORMATO_FECHA));

			movDTO.setHoraOperacion(datosLinea[indice++]);
			movDTO.setTipoTarjeta(datosLinea[indice++]);
			movDTO.setMarcaTarjeta(datosLinea[indice++]);
			movDTO.setBancoEmisor(datosLinea[indice++]);
			movDTO.setTipoOperacion(datosLinea[indice++].toUpperCase());

			String fechaDep= datosLinea[indice];
			movDTO.setFechaDeposito(FechaUtil.convierteaFecha(fechaDep, FechaUtil.FORMATO_FECHA));
			movDTO.setFechaDepositoCadena(fechaDep);
		}
		return movDTO;
	}

	private List<MovCorresponsalDTO> formatearMovimientos(List<MovCorresponsalDTO> movimientos) {
		if (!movimientos.isEmpty()) {
			for(MovCorresponsalDTO t : movimientos) {
				if (t.getNombreTH().trim().length() == 0)
					t.setNombreTH(null);
				else
					t.setNombreTH(t.getNombreTH().trim());

				t.setTipoPago(t.getTipoPago().toUpperCase());

				if (t.getTipoTarjeta().equals("C"))
					t.setTipoTarjeta(Constants.TIPO_TARJETA_CREDITO);
				else if (t.getTipoTarjeta().equals("D"))
					t.setTipoTarjeta(Constants.TIPO_TARJETA_DEBITO);
			}
		}
		return movimientos;
	}
}
