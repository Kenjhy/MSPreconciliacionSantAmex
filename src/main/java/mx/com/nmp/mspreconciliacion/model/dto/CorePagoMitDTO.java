package mx.com.nmp.mspreconciliacion.model.dto;

import mx.com.nmp.mspreconciliacion.model.preconciliacion.Pago;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoMIT;

public class CorePagoMitDTO {

    private Boolean activo;

    private PagoMIT pagoMIT;
    
    private Pago pago;

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public PagoMIT getPagoMIT() {
        return pagoMIT;
    }

    public void setPagoMIT(PagoMIT pagoMIT) {
        this.pagoMIT = pagoMIT;
    }

	public Pago getPago() {
		return pago;
	}

	public void setPago(Pago pago) {
		this.pago = pago;
	}
}
