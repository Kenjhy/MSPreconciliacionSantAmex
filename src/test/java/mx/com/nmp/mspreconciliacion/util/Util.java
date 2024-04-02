package mx.com.nmp.mspreconciliacion.util;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;

import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.BancoDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.CampaniaDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.CuotaDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.ListaBancoDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.ListaCampaniaDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.ListaCuotaDTO;
import mx.com.nmp.mspreconciliacion.consumer.rest.comisiones.dto.ResponseComisionesMIDAsDTO;
import mx.com.nmp.mspreconciliacion.model.dto.CatalogoComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesPaginadoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.dto.PreConciliacionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RestPreconcilacionDTO;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.Corresponsal;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.CorresponsalConciliadoEPA;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.CorresponsalConciliadoMIT;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.CorresponsalMIT;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.Devolucion;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.Pago;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoEPA;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoMIT;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoMIT;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.Partida;

public class Util {

    public static List<PagoConciliadoEPA> generarPagosEPA() {
    	List<PagoConciliadoEPA> lista = new ArrayList<>();
    	PagoConciliadoEPA pago = new PagoConciliadoEPA();
    	CorresponsalConciliadoEPA c = new CorresponsalConciliadoEPA();
    	c.setAfiliacion("1113");
    	c.setAutorizacion("198");
    	c.setBancoEmisor("amex");
    	c.setComisionTransaccional(BigDecimal.valueOf(Double.valueOf("20")));
    	c.setEstablecimiento("99999");
    	c.setEstatusDevolucion(null);
    	c.setFechaDevolucion(null);
    	c.setFechaLiquidacion(null);
    	c.setFechaOperacion(Date.from(Instant.now()));
    	c.setHoraOperacion("16:30");
    	c.setImporteBruto(BigDecimal.valueOf(Double.valueOf("2000")));
    	c.setImporteDevolucion(BigDecimal.valueOf(Double.valueOf("2000")));
    	c.setIvaTransaccional(new Float(Double.valueOf("12")));
    	c.setComisionTransaccionalEPA(BigDecimal.valueOf(2));
    	c.setSobretasa(Float.valueOf(30));
    	c.setIvaSobretasa(Float.valueOf("1.5"));
    	c.setMarcaTarjeta("AMEX");
    	c.setMoneda("MXN");
    	c.setNumeroMesesPromocion("6");
    	c.setNumeroOperacion("1345");
    	c.setReferencia("25");
    	c.setSucursal("13");
    	c.setTipoDevolucion(null);
    	c.setTipoOperacion("VENTA");
    	c.setTipoPago("6 MSI");
    	pago.setCorresponsal(c);
    	pago.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	lista.add(pago);
    	
    	PagoConciliadoEPA pago2 = new PagoConciliadoEPA();
    	CorresponsalConciliadoEPA c2 = new CorresponsalConciliadoEPA();
    	c2.setAfiliacion("0");
    	c2.setAutorizacion("880098");
    	c2.setBancoEmisor("amex");
    	c2.setComisionTransaccional(BigDecimal.valueOf(Double.valueOf("1908.82")));
    	c2.setEstablecimiento("9352810429");
    	c2.setEstatusDevolucion(null);
    	c2.setFechaDevolucion(null);
    	c2.setFechaLiquidacion(null);
    	c2.setFechaOperacion(Date.from(Instant.now()));
    	c2.setHoraOperacion("16:30");
    	c2.setImporteBruto(BigDecimal.valueOf(Double.valueOf("95441")));
    	c2.setIvaTransaccional(new Float(Double.valueOf("305.4112")));
    	c2.setComisionTransaccionalEPA(BigDecimal.valueOf(1908.82));
    	c2.setSobretasa(Float.valueOf("4056.24"));
    	c2.setIvaSobretasa(Float.valueOf("649"));
    	c2.setMarcaTarjeta("AMEX");
    	c2.setMoneda("MXN");
    	c2.setNumeroMesesPromocion("6");
    	c2.setNumeroOperacion("1345");
    	c2.setReferencia("2580102382");
    	c2.setSucursal("13");
    	c2.setTipoDevolucion(null);
    	c2.setTipoOperacion("VENTA");
    	c2.setTipoPago("6 MSI");
    	pago2.setCorresponsal(c2);
    	pago2.setIdPago("eeJcdYYB4XtLsNJG5qzu");
    	lista.add(pago2);
    	return lista;
    }

    public static List<PagoConciliadoMIT> generarPagosConciliadosMIT() {
    	List<PagoConciliadoMIT> lista = new ArrayList<>();
    	PagoConciliadoMIT pago = new PagoConciliadoMIT();
    	CorresponsalConciliadoMIT c = new CorresponsalConciliadoMIT();
    	c.setAfiliacion("1113");
    	c.setAutorizacion("198");
    	c.setBancoEmisor("SANTANDER");
    	c.setEstatusDevolucion(null);
    	c.setFechaDevolucion(null);
    	c.setFechaLiquidacion(null);
    	c.setFechaOperacion(Date.from(Instant.now()));
    	c.setHoraOperacion("16:30");
    	c.setImporte(BigDecimal.valueOf(Double.valueOf("2000")));
    	c.setMarcaTarjeta("VISA");
    	c.setMoneda("MXN");
    	c.setNumeroOperacion("1345");
    	c.setReferencia("25");
    	c.setSucursal("3");
    	c.setTipoDevolucion(null);
    	c.setTipoOperacion("VENTA");
    	c.setTipoTarjeta("DEBITO");
    	c.setTipoPago("6 MSI");
    	pago.setCorresponsal(c);
    	pago.setIdPago("0SriPoMBBaPdYAQHMKxW3");
    	
    	
    	PagoConciliadoMIT pago2 = new PagoConciliadoMIT();
    	CorresponsalConciliadoMIT c2 = new CorresponsalConciliadoMIT();
    	c2.setAfiliacion("1113");
    	c2.setAutorizacion("198");
    	c2.setBancoEmisor("BANAMEX");
    	c2.setEstatusDevolucion(null);
    	c2.setFechaDevolucion(null);
    	c2.setFechaLiquidacion(null);
    	c2.setFechaOperacion(Date.from(Instant.now()));
    	c2.setHoraOperacion("16:30");
    	c2.setImporte(BigDecimal.valueOf(Double.valueOf("2000")));
    	c2.setMarcaTarjeta("VISA");
    	c2.setMoneda("MXN");
    	c2.setNumeroOperacion("1345");
    	c2.setReferencia("25");
    	c2.setSucursal("23");
    	c2.setTipoDevolucion(null);
    	c2.setTipoOperacion("VENTA");
    	c2.setTipoTarjeta("DEBITO");
    	c2.setTipoPago("CREDITO");
    	pago2.setCorresponsal(c2);
    	pago2.setIdPago("0SriPoMBBaPdYAQHMKxW");

    	lista.add(pago);
    	return lista;
    }

    public static List<PagoMIT> generaDevolucionesMIT(boolean liquidada){
    	int estado = 17;
    	List<PagoMIT> pagos= new ArrayList<>();
    	PagoMIT p = new PagoMIT();
    	p.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	p.setFechaOperacion(Date.from(Instant.now()));
    	if (!liquidada) {
    		estado = 14;
    	}
    	p.setEstado(estado);
    	p.setMontoTotal(BigDecimal.valueOf(Double.valueOf("1351.0")));
    	
    	Partida pa = new Partida();
    	pa.setNumeroSucursalPartida(3);
    			
    	List<Partida> partidas = new ArrayList<>();
    	partidas.add(pa);
    	p.setPartidas(partidas);
    	    	
    	Devolucion dev = new Devolucion();
    	dev.setFechaCargoBancario(Date.from(Instant.now()));
    	dev.setFechaDevolucion("30/03/2023");
    	dev.setEstadoDevolucion(3); //liquidada
    	p.setDevolucion(dev);
    	
    	CorresponsalMIT c = new CorresponsalMIT();
		c.setRspAuth("12365");
		c.setRspDate(Date.from(Instant.now()));;
		c.setTxAmount("1351.0");
		c.setRspOperationNumber("12345078");
		c.setCcType("CREDITO");
		c.setCcNumber("2356");
		c.setRspDsMerchant("123");
		p.setCorresponsal(c);
		pagos.add(p);
    	return pagos;
    }
    
    public static List<PagoMIT> generaDevolucionesMITExp(){
    	List<PagoMIT> pagos= new ArrayList<>();
    	PagoMIT p = new PagoMIT();
    	p.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	p.setFechaOperacion(null);
    	p.setEstado(17);
    	p.setMontoTotal(BigDecimal.valueOf(Double.valueOf("1351.0")));
    	
    	Devolucion dev = new Devolucion();
    	dev.setFechaCargoBancario(Date.from(Instant.now()));
    	dev.setFechaDevolucion("30/03/2023");
    	dev.setEstadoDevolucion(3); //liquidada
    	p.setDevolucion(dev);
    	
    	CorresponsalMIT c = new CorresponsalMIT();
		c.setRspAuth("12365");
		c.setRspDate(Date.from(Instant.now()));;
		c.setTxAmount("aaaa");
		c.setRspOperationNumber("12345078");
		c.setCcType("CREDITO");
		c.setCcNumber("2356");
		c.setRspDsMerchant("123");
		p.setCorresponsal(c);
		pagos.add(p);
    	return pagos;
    }
    
    public static List<PagoMIT> generaDevMIT(){
    	List<PagoMIT> pagos= new ArrayList<>();
    	PagoMIT p = new PagoMIT();
    	
		String fechaTest= "08/02/2023";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate ld = LocalDate.parse(fechaTest, formatter);
		Date fecha=  Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

    	p.setIdPago("o0CtMoYB4XtLsNJG_8VW");
    	p.setEstado(15);
    	p.setMontoTotal(BigDecimal.valueOf(Double.valueOf("4000.0")));
    	p.setPlataformaOrigen("MIT AMEX");
    	p.setFechaOperacion(fecha);
    	
    	Devolucion dev = new Devolucion();
    	dev.setFechaCargoBancario(Date.from(Instant.now()));
    	dev.setFechaDevolucion("30/03/2023");
    	dev.setEstadoDevolucion(2); //solicitada
    	p.setDevolucion(dev);
    	
    	CorresponsalMIT c = new CorresponsalMIT();
		c.setRspAuth("12365");
		c.setRspDate(Date.from(Instant.now()));;
		c.setTxAmount("4000.0");
		c.setRspOperationNumber("12345078");
		c.setCcType("CREDITO");
		c.setCcNumber("3001");
		c.setRspDsMerchant("123");
		p.setCorresponsal(c);
		pagos.add(p);
    	return pagos;
    }

    
    public static List<PagoMIT> buscarPagosMIT(){
    	List<PagoMIT> pagos= new ArrayList<>();
    	PagoMIT p = new PagoMIT();
    	p.setIdPago("0SriPoMBBaPdYAQHMKxW1");
    	p.setFechaOperacion(Date.from(Instant.now()));
    	p.setEstado(14);
    	p.setMontoTotal(BigDecimal.valueOf(Double.valueOf("51.0")));
    	p.setAutorizacion("123456");
    	
    	Devolucion dev = new Devolucion();
    	dev.setFechaCargoBancario(Date.from(Instant.now()));
    	dev.setFechaDevolucion("30/03/2023");
    	dev.setEstadoDevolucion(3); //liquidada
    	p.setDevolucion(dev);
    	
    	CorresponsalMIT c = new CorresponsalMIT();
		c.setRspAuth("12365");
		c.setRspDate(Date.from(Instant.now()));;
		c.setTxAmount("1351.0");
		c.setRspOperationNumber("12345078");
		c.setCcType("CREDITO");
		c.setCcNumber("2356");
		c.setRspDsMerchant("123");
		p.setCorresponsal(c);
		pagos.add(p);
    	return pagos;
    }
    

    
    public static List<PagoConciliadoEPA> generarDevolucionesEPA() {
    	List<PagoConciliadoEPA> lista = new ArrayList<>();
    	PagoConciliadoEPA pago = new PagoConciliadoEPA();
    	CorresponsalConciliadoEPA c = new CorresponsalConciliadoEPA();
    	c.setAfiliacion("1113");
    	c.setAutorizacion("198");
    	c.setBancoEmisor("amex");
    	c.setComisionTransaccional(BigDecimal.valueOf(Double.valueOf("20")));
    	c.setEstablecimiento("99999");
    	c.setEstatusDevolucion("SOLICITADA");
    	c.setFechaDevolucion(null);
    	c.setFechaLiquidacion(null);
    	c.setFechaOperacion(Date.from(Instant.now()));
    	c.setHoraOperacion("16:30");
    	c.setImporteBruto(BigDecimal.valueOf(Double.valueOf("2000")));
    	c.setImporteDevolucion(BigDecimal.valueOf(Double.valueOf("2000")));
    	c.setIvaTransaccional(new Float(Double.valueOf("12")));
    	c.setMarcaTarjeta("AMEX");
    	c.setMoneda("MXN");
    	c.setNumeroMesesPromocion("0");
    	c.setNumeroOperacion("1345");
    	c.setReferencia("25");
    	c.setSucursal("13");
    	c.setTipoDevolucion("ADMINISTRATIVA");
    	c.setTipoOperacion("DEVOLUCION");
    	c.setTipoPago("CREDITO");
    	pago.setCorresponsal(c);
    	pago.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	lista.add(pago);
    	return lista;
    }
    
    public static List<PagoMIT> generaDevolucionesSolicitadosMIT(boolean aut){
    	int estado = 13;
    	List<PagoMIT> pagos= new ArrayList<>();
    	PagoMIT p = new PagoMIT();
    	p.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	if (!aut) {
    		estado = 14;
    	}
    	p.setEstado(estado);
    	p.setMontoTotal(BigDecimal.valueOf(Double.valueOf("1351.0")));
    	
    	Partida pa = new Partida();
    	pa.setNumeroSucursalPartida(3);
    			
    	List<Partida> partidas = new ArrayList<>();
    	partidas.add(pa);
    	
    	p.setPartidas(partidas);
    	Devolucion dev = new Devolucion();
    	dev.setFechaCargoBancario(null);
    	dev.setFechaDevolucion("31/03/2023");
    	dev.setEstadoDevolucion(2); 
    	p.setDevolucion(dev);
    	
    	CorresponsalMIT c = new CorresponsalMIT();
		c.setRspAuth("12365");
		c.setRspDate(Date.from(Instant.now()));;
		c.setTxAmount("1351.0");
		c.setRspOperationNumber("12345078");
		c.setCcType("CREDITO");
		c.setCcNumber("2356");
		c.setRspDsMerchant("123");
		p.setCorresponsal(c);
		pagos.add(p);
    	return pagos;
    }
    
    public static List<PagoConciliadoMIT> generarDevolucionesConciliadosMIT() {
    	List<PagoConciliadoMIT> lista = new ArrayList<>();
    	PagoConciliadoMIT pago = new PagoConciliadoMIT();
    	CorresponsalConciliadoMIT c = new CorresponsalConciliadoMIT();
    	c.setAfiliacion("1113");
    	c.setAutorizacion("198");
    	c.setBancoEmisor("BANAMEX");
    	c.setEstatusDevolucion("SOLICITADA");
    	c.setFechaDevolucion(null);
    	c.setFechaLiquidacion(null);
    	c.setFechaOperacion(Date.from(Instant.now()));
    	c.setHoraOperacion("16:30");
    	c.setImporte(BigDecimal.valueOf(Double.valueOf("2000")));
    	c.setMarcaTarjeta("VISA");
    	c.setMoneda("MXN");
    	c.setNumeroOperacion("1345");
    	c.setReferencia("25");
    	c.setSucursal("13");
    	c.setTipoDevolucion("ADMINISTRATIVA");
    	c.setTipoOperacion("DEVOLUCION");
    	c.setTipoPago("CREDITO");
    	pago.setCorresponsal(c);
    	pago.setIdPago("0SriPoMBBaPdYAQHMKxW");
    	lista.add(pago);
    	return lista;
    }
    
    public static Pago generarDev() {
		Pago p = new Pago();
		p.setId("0SriPoMBBaPdYAQHMKxW");
    	p.setEstado(14);
    	p.setMontoTotal(BigDecimal.valueOf(Double.valueOf("1351.0")));
    	
    	Partida pa = new Partida();
    	pa.setNumeroSucursalPartida(3);
    			
    	List<Partida> partidas = new ArrayList<>();
    	partidas.add(pa);
    	p.setPartidas(partidas);
    	
		return p;
	}
    
    public static Pago generarPagoGuardado() {
		Pago p = new Pago();
		p.setId("123ee");
		p.setPlataformaOrigen("MIT Santander");
    	p.setEstado(16);
    	p.setAutorizacion("123456");
    	p.setFechaOperacion(Date.from(Instant.now()));
    	p.setMontoTotal(BigDecimal.valueOf(Double.valueOf("1351.0")));
		return p;
	}
    
    public static Optional<Pago> encontrarPagoPreconciliacion() {
    	Pago p = new Pago();
    	p.setEstado(3);
    	p.setAutorizacion("123456");
    
    	Corresponsal c = new Corresponsal();
    	c.setNombre("MIT Santander");
    	p.setCorresponsal(c);
    	p.setFechaOperacion(Date.from(Instant.now()));
    	p.setId("988848ewerwiuio2");
    	p.setMontoTotal(BigDecimal.valueOf(Double.valueOf("351.0")));
		return Optional.of(p);
    }
    
    public static Optional<Pago> encontrarDevPreconciliacion() {
    	Pago p = new Pago();
    	p.setEstado(14);
    	p.setAutorizacion("1234567");
    
    	Corresponsal c = new Corresponsal();
    	c.setNombre("MIT Santander");
    	p.setCorresponsal(c);
    	p.setFechaOperacion(Date.from(Instant.now()));
    	p.setId("988848ewerwiuio3");
    	p.setMontoTotal(BigDecimal.valueOf(Double.valueOf("351.0")));
		return Optional.of(p);
    }    
    
    public static ResponseComisionesMIDAsDTO comisionesMidas() {
        ListaCuotaDTO cuota = new ListaCuotaDTO();
        
        ListaCampaniaDTO campania = new ListaCampaniaDTO();
        List<CampaniaDTO> campanias = new ArrayList<>();
        CampaniaDTO ca = new CampaniaDTO();
        ca.setMontoMinimo(BigDecimal.TEN);
        ca.setPlazo("3");
        ca.setRamo("");
        ca.setSucursal("10");
        campanias.add(ca);
        
        ListaBancoDTO banco = new ListaBancoDTO();
        List<BancoDTO> bancos = new ArrayList<>();
        BancoDTO b = new BancoDTO();
        b.setBanco("Santander");
        b.setSobretasa(BigDecimal.TEN);
        bancos.add(b);
        ca.setBancos(banco);

        List<CuotaDTO> cuotas = new ArrayList<>();
        CuotaDTO cu = new CuotaDTO();
        cu.setComision(BigDecimal.TEN);
        cu.setTarjeta("Santander");
        cu.setTipo("F");
        cuotas.add(cu);
        cuota.setCuota(cuotas);
        campania.setCampania(campanias);
        
        ResponseComisionesMIDAsDTO respuestaWS = new ResponseComisionesMIDAsDTO();
        respuestaWS.setCuotas(cuota);
        respuestaWS.setCampanias(campania);
        
        return respuestaWS;
    }
    
    public static List<MovCorresponsalDTO> generarMovsCentroPagos(){ 		
	    List<MovCorresponsalDTO> movWS = new ArrayList<>();
		MovCorresponsalDTO  movWS1 = new MovCorresponsalDTO();
		movWS1.setBancoEmisor("AZTECA");
		movWS1.setAfiliacion("8679485");
		movWS1.setHoraOperacion("11:56:00");
		movWS1.setAutorizacion("123456");
		movWS1.setMarcaTarjeta("MASTERCARD");
		movWS1.setMoneda("MXN");
		movWS1.setNombreArchivo("prva3FAA010222.txt");
		movWS1.setNumeroOperacion("111115");
		movWS1.setReferencia("12355");
		movWS1.setSucursal("134");
		movWS1.setTarjeta("6422");
		movWS1.setTipoOperacion("VENTA");
		movWS1.setTipoPago("CONTADO");
		movWS1.setTipoTarjeta("DEBITO");
		movWS1.setUsrTrx("mpay");
		movWS1.setImporteBruto(BigDecimal.TEN);
		movWS.add(movWS1);
		
		return movWS;
    }
    
    public static List<MovCorresponsalDTO> generarMovsLecturaArchivo(){ 		
		List<MovCorresponsalDTO> movSFTP = new ArrayList<>();
		MovCorresponsalDTO  mov = new MovCorresponsalDTO();
		mov.setFechaOperacion(Date.from(Instant.now()));
		mov.setBancoEmisor("Banamex");
		mov.setAfiliacion("8679485");
		mov.setHoraOperacion("11:56:00");
		mov.setAutorizacion("123456");
		mov.setMarcaTarjeta("MASTERCARD");
		mov.setMoneda("MXN");
		mov.setNombreArchivo("prva3FAA010222.txt");
		mov.setNumeroOperacion("111115");
		mov.setReferencia("12355");
		mov.setSucursal("134");
		mov.setTarjeta("6422");
		mov.setTipoOperacion("VENTA");
		mov.setTipoPago("CONTADO");
		mov.setTipoTarjeta("DEBITO");
		mov.setUsrTrx("mpay");
		mov.setImporteBruto(BigDecimal.TEN);
		movSFTP.add(mov);
		
		return movSFTP;
    }
    
    public static List<MovCorresponsalDTO> generarMovsLecturaAMEXArchivoDevs(){ 		
		List<MovCorresponsalDTO> movSFTP = new ArrayList<>();
		MovCorresponsalDTO  mov = new MovCorresponsalDTO();
		
		String fechaTest= "08/02/2023";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate ld = LocalDate.parse(fechaTest, formatter);
		Date fecha=  Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		mov.setFechaOperacion(fecha);
		mov.setBancoEmisor("AMEX");
		mov.setAutorizacion("123456");
		mov.setAfiliacion("8679485");
		mov.setHoraOperacion("11:56:00");
		mov.setMarcaTarjeta("MASTERCARD");
		mov.setMoneda("MXN");
		mov.setNombreArchivo("230208.txt");
		mov.setNumeroOperacion("111115");
		mov.setReferencia("12355");
		mov.setSucursal("134");
		mov.setTarjeta("371778XXXXX3001");
		mov.setTipoOperacion("DEVOLUCION");
		mov.setEsDevolucion(true);
		mov.setTipoPago("CONTADO");
		mov.setTipoTarjeta("DEBITO");
		mov.setUsrTrx("mpay");
		mov.setImporteBruto(BigDecimal.valueOf(4000L));
		movSFTP.add(mov);
		
		return movSFTP;
    }
    
    
    public static RestPreconcilacionDTO respuestaActualizaCron () {
		RestPreconcilacionDTO actualizaCron = new RestPreconcilacionDTO();
		actualizaCron.setCode("200 OK");
		actualizaCron.setObject(null);
		return actualizaCron;
    }
    
    public static RestPreconcilacionDTO respuestaActualizaCronError () {
		RestPreconcilacionDTO actualizaCron = new RestPreconcilacionDTO();
		actualizaCron.setCode("400");
		actualizaCron.setObject(null);
		return actualizaCron;
    }
    
    public static PreConciliacionDTO requestPreconciliacion(boolean cronHabilitado) {
		String fechaTest= "25/03/2023";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate ld = LocalDate.parse(fechaTest, formatter);
		Date fecha=  Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		
		PreConciliacionDTO datosPrecon= new PreConciliacionDTO();
		datosPrecon.setFecha(fecha);
		datosPrecon.setIdProcesoCron(163L);
		datosPrecon.setNumeroReintento(0);
		datosPrecon.setActualizarProcesoCron(cronHabilitado);
		return datosPrecon;
    }
    
    public static ComisionesPaginadoDTO generarComisionesPaginado(List<ComisionesDTO> comisiones){
        ComisionesPaginadoDTO comisionesPaginado = new ComisionesPaginadoDTO();
        comisionesPaginado.setTieneMasPaginas(false);
        comisionesPaginado.setTotalRegistros(comisiones.size());
        comisionesPaginado.setNumeroRegistros(comisiones.size());
        comisionesPaginado.setNumeroPagina(0);
        comisionesPaginado.setComisionesList(comisiones);
        return comisionesPaginado;
    }

    public static ComisionesDTO generarComisionAMEX(){
        ComisionesDTO comision = new ComisionesDTO();
        comision.setBanco("BBVA");
        comision.setSucursal("10");
        comision.setFechaLiquidacion(new Date());
        comision.setFechaVenta(new Date());
        comision.setTipoTarjeta("Debito");
        comision.setTipoPago("CONTADO");
        comision.setPlazo("0");

        comision.setImporteNeto(BigDecimal.TEN);
        comision.setComisionTransaccional(BigDecimal.TEN);
        comision.setIvaTransaccional(BigDecimal.TEN);
        comision.setSobretasa(BigDecimal.TEN);
        comision.setIvaSobretasa(BigDecimal.TEN);
        comision.setMontoTotal(BigDecimal.TEN);
        comision.setComisionTransaccionalMIDAS(BigDecimal.ZERO);
        comision.setSobretasaMIDAS(BigDecimal.ZERO);
        return comision;
    }

    public static CatalogoComisionesDTO generarCatalogoComisionesAMEX(){
        CatalogoComisionesDTO catalogoComisiones = new CatalogoComisionesDTO();
        catalogoComisiones.setBanco(Collections.singletonList("AMEX"));
        catalogoComisiones.setSucursal(Collections.singletonList("510"));
        return catalogoComisiones;
    }
    
    public static ResponseComisionesMIDAsDTO respuestaWSComisiones() {
		ListaCampaniaDTO campanias = new ListaCampaniaDTO();
		ListaCuotaDTO cuotas= new ListaCuotaDTO();
		
		List<CampaniaDTO> campania= new ArrayList<>();
		CampaniaDTO camp = new CampaniaDTO();
		camp.setCampania("Campaña test1");
		camp.setMontoMinimo(BigDecimal.TEN);
		camp.setRamo("Diamantes");
		camp.setPlazo("6");
		camp.setSucursal("13");
		campania.add(camp);
		
		ListaBancoDTO bancos = new ListaBancoDTO();
		List<BancoDTO> banco = new ArrayList<>();
		BancoDTO b = new BancoDTO();
		b.setBanco("AMERICAN EXPRESS");
		b.setSobretasa(new BigDecimal(2));
		banco.add(b);
		bancos.setBanco(banco);
		camp.setBancos(bancos);
		
		campanias.setCampania(campania);
		List<CuotaDTO> cuota = new ArrayList<>();
        CuotaDTO cu = new CuotaDTO();
        cu.setComision(new BigDecimal(3));
        cu.setTarjeta("INTERNACIONAL");
        cu.setTipo("F");
        cuota.add(cu);
        cuotas.setCuota(cuota);
        
        ResponseComisionesMIDAsDTO respuesta = new ResponseComisionesMIDAsDTO();
        respuesta.setCampanias(campanias);
        respuesta.setCuotas(cuotas);
        respuesta.setFecha("21/03/2023");
        
        return respuesta;
    }
    
    public static ResponseComisionesMIDAsDTO respuestaWSComisionesSantander() {
		ListaCampaniaDTO campanias = new ListaCampaniaDTO();
		ListaCuotaDTO cuotas= new ListaCuotaDTO();
		
		List<CampaniaDTO> campania= new ArrayList<>();
		CampaniaDTO camp = new CampaniaDTO();
		camp.setCampania("Campaña test1");
		camp.setMontoMinimo(BigDecimal.TEN);
		camp.setRamo("Diamantes");
		camp.setPlazo("3");
		camp.setSucursal("3");
		campania.add(camp);
		
		ListaBancoDTO bancos = new ListaBancoDTO();
		List<BancoDTO> banco = new ArrayList<>();
		BancoDTO b = new BancoDTO();
		b.setBanco("AMERICAN EXPRESS");
		b.setSobretasa(new BigDecimal(2));
		banco.add(b);
		bancos.setBanco(banco);
		camp.setBancos(bancos);
		
		campanias.setCampania(campania);
		List<CuotaDTO> cuota = new ArrayList<>();
        CuotaDTO cu = new CuotaDTO();
        cu.setComision(new BigDecimal(3));
        cu.setTarjeta("INTERNACIONAL");
        cu.setTipo("F");
        cuota.add(cu);
        cuotas.setCuota(cuota);
        
        ResponseComisionesMIDAsDTO respuesta = new ResponseComisionesMIDAsDTO();
        respuesta.setCampanias(campanias);
        respuesta.setCuotas(cuotas);
        respuesta.setFecha("21/03/2023");
        
        return respuesta;
    }
    
    public static SearchHits<PagoConciliadoMIT> getSimpleSearchHintsConciliadoMIT(List<PagoConciliadoMIT> pagos) {
	  List<SearchHit<PagoConciliadoMIT>> searchHitList = pagos.stream().map(pago -> new SearchHit<>("",pago.getId(),"",1.0f,null,null,null,null,null,null,pago)).collect(Collectors.toList());
	  return new SearchHitsImpl<>(pagos.size(),null,1.0f,"",searchHitList,null);
	}
    
    public static SearchHits<PagoConciliadoEPA> getSimpleSearchHintsEPA(List<PagoConciliadoEPA> pagos) {
	  List<SearchHit<PagoConciliadoEPA>> searchHitList = pagos.stream().map(pago -> new SearchHit<>("",pago.getId(),"",1.0f,null,null,null,null,null,null,pago)).collect(Collectors.toList());
	  return new SearchHitsImpl<>(pagos.size(),null,1.0f,"",searchHitList,null);
	}
    
    public static SearchHits<PagoMIT> getSimpleSearchHints(List<PagoMIT> pagos) {
	  List<SearchHit<PagoMIT>> searchHitList = pagos.stream().map(pago -> new SearchHit<>("",pago.getId(),"",1.0f,null,null,null,null,null,null,pago)).collect(Collectors.toList());
	  return new SearchHitsImpl<>(pagos.size(),null,1.0f,"",searchHitList,null);
	}
}
