package mx.com.nmp.mspreconciliacion.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FechaUtil {

	private FechaUtil() {

	}
	
	public static final String FORMATO_WSCOMISIONES = "yyyy-MM-dd";
	public static final String FORMATO_ARCHIVO = "yyyyMMdd";
	public static final String FORMATO_FECHA = "dd/MM/yyyy";
	public static final String FORMATO_COMPLETO=  "dd/MM/yyyy HH:mm";
	public static final String FORMATO_HORA = "HH:mm";
	public static final String ZONE_GMT_6= "GMT-6";
	public static final String ZONE_AMERICA= "America/Mexico_City";
	
	private static final Logger log = LoggerFactory.getLogger(FechaUtil.class);

	public static Date convierteaFecha(String cadena, String formato){
		Date fecha= null;
		if (cadena != null) {
			DateTimeFormatter dtFormato = DateTimeFormatter.ofPattern(formato);

			if (formato.contains("HH")) {
				//formato con tiempo
				LocalDateTime ldt = LocalDateTime.parse(cadena, dtFormato);
				fecha = Date.from(ldt.atZone(ZoneId.of(ZONE_GMT_6)).toInstant());

			}else {
				LocalDate ld = LocalDate.parse(cadena.replace("\"", ""), dtFormato);
				Instant instant = Instant.from(ld.atStartOfDay(ZoneId.of(ZONE_GMT_6)));
				fecha = Date.from(instant);
			}
		}
		return fecha;
	}

	public static String convierteFechaaCadena(Date fecha, String formato) {
		String cadena = null;
		DateTimeFormatter dtFormato = DateTimeFormatter.ofPattern(formato);
		LocalDateTime ldt2= fecha.toInstant()
				.atZone(ZoneId.of(ZONE_GMT_6))
				.toLocalDateTime();
		cadena = ldt2.format(dtFormato);
		return cadena;
	}

	public static String convierteFechaaCadena(Date fecha, String formato, String zona) {
		String cadena = null;
		DateTimeFormatter dtFormato = DateTimeFormatter.ofPattern(formato);
		LocalDateTime ldt2= fecha.toInstant()
				.atZone(ZoneId.of(zona))
				.toLocalDateTime();
		cadena = ldt2.format(dtFormato);
		return cadena;
	}

	public static Date obtenerFechaIni(Date fecha) {
		Calendar ini = Calendar.getInstance();
		ini.setTime( fecha);
		ini.set(Calendar.HOUR_OF_DAY, 0);
		ini.set(Calendar.MINUTE, 0);
		ini.set(Calendar.SECOND, 0);
		ini.set(Calendar.MILLISECOND, 0);
		return ini.getTime();
	}


	public static Date obtenerFechaFin(Date fecha) {
		Calendar fin = Calendar.getInstance();
		fin.setTime( fecha);
		fin.set(Calendar.HOUR_OF_DAY, 23);
		fin.set(Calendar.MINUTE, 59);
		fin.set(Calendar.SECOND, 59);
		fin.set(Calendar.MILLISECOND, 59);
		return fin.getTime();
	}
	
	public static Date obtenerFechaMenosXDiasHabil(Date fecha, Integer numeroDiasRestaWSOnlinePre){
		int restaDias= 1;
		if (numeroDiasRestaWSOnlinePre != null) {
			restaDias= numeroDiasRestaWSOnlinePre;
		}
		Date fechaRestada= null;
		
	    LocalDate date = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    for (int i=0; i<=2; i++) {
	    	LocalDate dateResultado= null;
	    	Date fecCalculada = null;
	    	
	    	if (i> 0) {
	    		restaDias++;
	    	}
    		dateResultado = date.minus(restaDias, ChronoUnit.DAYS);
    		fecCalculada= Date.from(dateResultado.atStartOfDay(ZoneId.systemDefault()).toInstant());

		    Calendar fechaCal= new GregorianCalendar();
		    fechaCal.setTime(fecCalculada);
		    		
	        if (fechaCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY ) {
	            fechaRestada= fecCalculada;
	            break;
	        } 
	    }
		
	    log.info("diaHabil-fecha WSConsultOnline-preconciliacion, {} ", fechaRestada);
	    return fechaRestada;
    }

}
