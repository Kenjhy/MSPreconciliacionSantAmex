package mx.com.nmp.mspreconciliacion.centropagos.consumer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import mx.com.nmp.mspreconciliacion.centropagos.consumer.soap.ObjectFactory;
import mx.com.nmp.mspreconciliacion.centropagos.consumer.soap.Transacciones;
import mx.com.nmp.mspreconciliacion.centropagos.consumer.soap.TransaccionesResponse;
import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.MovCentroPagosDTO;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RequestCentroPagosDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.util.FechaUtil;

@Service
@Component("WSCentroPagos")
public class CentroPagosSoapService  extends WebServiceGatewaySupport{


	@Value(value = "${mspreconciliacion.variables.centroPagosWSInfo.user}")
	public String user;

	@Value(value = "${mspreconciliacion.variables.centroPagosWSInfo.pwd}")
	public String pwd;

	@Value(value = "${mspreconciliacion.variables.centroPagosWSInfo.company}")
	public String company;

	@Value(value = "${mspreconciliacion.variables.centroPagosWSInfo.url}")
	public String url;

	@Value(value = "${mspreconciliacion.variables.centroPagosWSInfo.llaveNMP}")
	public String llaveNMP;

	@Value(value = "${mspreconciliacion.variables.centroPagosWSInfo.datoFijoNMP}")
	public String datoFijoNMP;

	private static final Logger LOG = LoggerFactory.getLogger(CentroPagosSoapService.class);

	public List<MovCorresponsalDTO> consultarTransacciones(CorresponsalEnum corresponsal, Date fecha) throws PagoException{
		List<MovCorresponsalDTO> respuestaList = null;
		TransaccionesResponse respuesta= null;
		String request= null;

		LOG.info("Entra a método de consultarTransacciones de CentroPagosSoapService, WS-Centro de Pagos");

		request = generarRequest(fecha);
		ObjectFactory factory= new ObjectFactory();
		Transacciones reqJAXB = factory.createTransacciones();
		reqJAXB.setDatoFijo(datoFijoNMP);
		reqJAXB.setRequestEncriptado(request);
		try {
			LOG.info("ejecuta WS con Request generado-CentroPagosSoapService, WS-Centro de Pagos");
			LOG.info("conexión Centro de pagosW url ={}, datofijo = {}, fecha={}", url, datoFijoNMP, fecha);
			LOG.info("conexión Centro de pagosW requestEncriptado={}",request );
			respuesta = (TransaccionesResponse) getWebServiceTemplate().marshalSendAndReceive(url, reqJAXB);

		}catch(Exception ex) {
			LOG.error(Constants.GENERIC_EXCEPTION_INITIAL_MESSAGE, ex);
			throw PagoException.ERROR_WSCENTROPAGOS_IO;
		}

		respuestaList= generarResponse(respuesta!=null?respuesta.getOut():null,corresponsal);
		return respuestaList;
	}



	private String  generarRequest(Date fecha) throws PagoException{
		String datosRequestXML = null;
		String datosRequestEncrypt= null;
		String fechaParam= null;

		LOG.info("Entra a método de generarRequest de CentroPagosSoapService, WS-Centro de Pagos");

		try {
			fechaParam = FechaUtil.convierteFechaaCadena(fecha, FechaUtil.FORMATO_FECHA);
			RequestCentroPagosDTO request = new RequestCentroPagosDTO();
			request.setUser(user);
			request.setPwd(pwd);
			request.setCompany(company);
			request.setDate(fechaParam);

			String ini = "<RequestCentroPagosDTO>";
			String fin = "</RequestCentroPagosDTO>";

			String xml = new XmlMapper().writeValueAsString(request);
			datosRequestXML = xml.substring(ini.length(), xml.length()- fin.length());

			//Encriptar
			datosRequestEncrypt= AESEncryption.encrypt(datosRequestXML, llaveNMP);

		}catch(Exception ex) {
			LOG.error(Constants.GENERIC_EXCEPTION_INITIAL_MESSAGE, ex);
			throw PagoException.ERROR_WSCENTROPAGOS_REQUEST;
		}

		return datosRequestEncrypt;

	}

	private List<MovCorresponsalDTO> generarResponse(String respuesta, CorresponsalEnum corresponsal) throws PagoException{
		List<MovCorresponsalDTO> respuestaList = new ArrayList<>();
		List<MovCorresponsalDTO> respuestaListFiltrada = new ArrayList<>();
		String datosDescifrados=null;

		LOG.info("ejecuta Response generarResponse-CentroPagosSoapService, WS-Centro de Pagos");
		//Desencriptar
		try {

			if (respuesta != null) {
				datosDescifrados= AESEncryption.decrypt(respuesta, llaveNMP);
				LOG.info("conexión Centro de pagosW responseEncriptado={}",respuesta);
				LOG.info("conexión Centro de pagosW responseDesencriptado={}",datosDescifrados);
				LOG.info("conexión Centro de pagosW llaveNMP={}",llaveNMP);

				//Si existe problemas en datos de entrada
				if (!datosDescifrados.startsWith(Constants.ERROR_WSCENTROPAGOS)){

					//Tratamiento por XML mal armado como respuesta de WS-Centro de pagos
					String ini = "<transacciones>";
					String fin = "</transacciones>";
					int indexIni= datosDescifrados.indexOf(ini);
					int indexFin= datosDescifrados.indexOf(fin);
					String datosSinTagErroneo = datosDescifrados.substring(indexIni, indexFin+ fin.length());
					LOG.info(datosSinTagErroneo);

					//Serializar a objeto de salida
					MovCentroPagosDTO dat= new XmlMapper().readValue(datosSinTagErroneo, MovCentroPagosDTO.class);

					if (dat != null) {
						respuestaList= dat.getTransaccion();

						Predicate<MovCorresponsalDTO> filtroAmex= (MovCorresponsalDTO mov) -> mov.getMarcaTarjeta().equals(CorresponsalEnum.AMEX.getNombre());

						if (corresponsal.equals(CorresponsalEnum.AMEX)) {
							respuestaListFiltrada= respuestaList.stream().filter(o -> o.getCodigoEstado().equals("00")) .filter(filtroAmex).collect(Collectors.toList());	
						}else {
							respuestaListFiltrada= respuestaList.stream().filter(o -> o.getCodigoEstado().equals("00")).collect(Collectors.toList());
						}
						respuestaListFiltrada= procesarResponse(respuestaListFiltrada);
						LOG.info(Constants.MSG_SUCCESS_WSCENTROPAGOS);
					}
				}else {
					throw PagoException.ERROR_WSCENTROPAGOS;
				}
			}

		}catch(PagoException e) {
			throw e;
		}catch(Exception ex) {
			LOG.error(Constants.GENERIC_EXCEPTION_INITIAL_MESSAGE, ex);
			throw PagoException.ERROR_WSCENTROPAGOS_RESPONSE;
		}
		return respuestaListFiltrada;
	}

	private List<MovCorresponsalDTO> procesarResponse(List<MovCorresponsalDTO> movs) {

		List<MovCorresponsalDTO>  listaSinCancelaciones= new ArrayList<>();
		
		//Nombre
		for(MovCorresponsalDTO t: movs) {
			if (!t.getTipoOperacion().equals(Constants.TIPO_OPERACION_CANCELA)) {
				if (t.getNombreTH() != null && t.getNombreTH().length() > 0) {
					if (t.getNombreTH().trim().length() > 0) {
						t.setNombreTH(t.getNombreTH().trim());
					}else {
						t.setNombreTH(null);		
					}
				}
	
				t.setMarcaTarjeta(t.getMarcaTarjeta().toUpperCase());
				t.setTipoTarjeta(t.getTipoTarjeta().toUpperCase());
				if (t.getTipoPago().startsWith("C")) {
					t.setTipoPago(t.getTipoPago().toUpperCase());
				}
	
				//Revisar si el tipo de pago es a MSI //No ha llegado información de MSI aún 23/02/23
				String tieneMeses = t.getTipoPago();
				if (tieneMeses.contains("Meses")) {
					//Tipo pago MSI
					String[] plazoMeses= tieneMeses.split("Meses");
					String plazo = plazoMeses[0];
					t.setNumeroMesesPromocion(Integer.valueOf(plazo));
	
					//Aplicar formato meses+ MSI
					t.setTipoPago(plazo + " MSI");
				}
	
				if (t.getFechaOperacionCadena() != null) {
	
					Date fecha = FechaUtil.convierteaFecha(t.getFechaOperacionCadena(), FechaUtil.FORMATO_COMPLETO);
					String fechaSinHrs= FechaUtil.convierteFechaaCadena(fecha, FechaUtil.FORMATO_FECHA);
					t.setFechaOperacion(FechaUtil.convierteaFecha(fechaSinHrs, FechaUtil.FORMATO_FECHA));
					t.setHoraOperacion(FechaUtil.convierteFechaaCadena(fecha, FechaUtil.FORMATO_HORA));
				}
				if (t.getFechaDepositoCadena() != null) {
					Date fecha= FechaUtil.convierteaFecha(t.getFechaDepositoCadena(), FechaUtil.FORMATO_FECHA);
					t.setFechaDeposito(fecha);
				}
				mapearDevoluciones(t);
				listaSinCancelaciones.add(t);
			}
			
		}
		return listaSinCancelaciones;
	}

	private void mapearDevoluciones(MovCorresponsalDTO t) {
		//Devolucion
		if (t.getTipoOperacion().equals(Constants.TIPO_OPERACION_DEVOLUCION)) {
			t.setTipoDevolucion(Constants.TIPO_DEVOLUCION_AUTOMATICA);
			t.setEstatusDevolucion(Constants.ESTATUS_DEVOLUCION_SOLICITADA);
			t.setEstatusDevolucionId(Constants.ESTATUS_DEVOLUCION_SOLICITADA_ID);
			t.setFechaDevolucion(t.getFechaDeposito());
			t.setFechaLiquidacion(t.getFechaDeposito());
		}
	}
}
