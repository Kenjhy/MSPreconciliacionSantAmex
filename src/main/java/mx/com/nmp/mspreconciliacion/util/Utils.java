package mx.com.nmp.mspreconciliacion.util;

import java.math.BigDecimal;

public class Utils {
	
	private Utils(){
		
	}

	public static BigDecimal calcularMontoTotal(BigDecimal montoNeto, BigDecimal comisionTransaccional, BigDecimal ivaTransaccional, BigDecimal sobretasa, BigDecimal ivaSobretasa) {
		BigDecimal montoTotal = null;
		
		montoTotal= montoNeto.subtract(comisionTransaccional)
				.subtract(ivaTransaccional)
				.subtract(sobretasa)
				.subtract(ivaSobretasa);
		
		return montoTotal;
	}
	
	public static String obtenerSucursalDeNombreAfiliacionSantander(String descripcion) {
		
		String sucursal = "";
		if (descripcion != null) {
			if (descripcion.length() > 18) {
				//"NAL MONTE PIEDAD 143 2" 
				sucursal= descripcion.substring(17,20);
			}else {
				//"NMP SUC 298"
				sucursal= descripcion.substring(7,11);
			}
		}
		
		if (sucursal.length() > 0)
			sucursal = sucursal.replace("\"", "").trim();
		
		return sucursal;
	}
	
}
