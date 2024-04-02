package mx.com.nmp.mspreconciliacion.model.enums;



public enum CorresponsalEnum {
	
	SANTANDER ("MIT Santander"),
	AMEX ("MIT AMEX");

	private String nombre;
	
	private CorresponsalEnum(String nombre) {
		this.nombre = nombre;
	}	
	
	public String getNombre() {
		return nombre;
	}	
	
	public static CorresponsalEnum getByNombre(String nombre) {
		for (CorresponsalEnum corresponsalEnum : CorresponsalEnum.values()) {
			if (corresponsalEnum.getNombre().equalsIgnoreCase(nombre)) {
				return corresponsalEnum;
			}
		}
		return null;
	}	
}
