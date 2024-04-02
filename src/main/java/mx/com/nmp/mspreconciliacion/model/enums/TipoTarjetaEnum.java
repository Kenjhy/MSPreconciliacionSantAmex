package mx.com.nmp.mspreconciliacion.model.enums;

import java.text.Normalizer;

public enum TipoTarjetaEnum {

    DEBITO ("Débito", "Tarjeta Debito"),
    CREDITO ("Crédito", "Tarjeta Credito"),
    INTERNACIONAL ("Internacional", "Tarjeta Internacional");

    private String nombre;
    private String descripcion;

    TipoTarjetaEnum(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public static TipoTarjetaEnum getByDescripcion(String descripcion) {
        descripcion = Normalizer.normalize(descripcion, Normalizer.Form.NFD);
        for (TipoTarjetaEnum tipoTarjetaEnum : TipoTarjetaEnum.values()) {
            String tipoTarjeta = Normalizer.normalize(tipoTarjetaEnum.getDescripcion(), Normalizer.Form.NFD);
            if (tipoTarjeta.equalsIgnoreCase(descripcion)) {
                return tipoTarjetaEnum;
            }
        }
        return null;
    }
}
