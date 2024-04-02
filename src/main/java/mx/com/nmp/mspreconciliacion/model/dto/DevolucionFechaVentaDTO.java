package mx.com.nmp.mspreconciliacion.model.dto;

import java.util.Date;

public class DevolucionFechaVentaDTO {

    private String idPago;
    private Date fechaTransaccionVenta;

    public String getIdPago() {
        return idPago;
    }
    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }
    public Date getFechaTransaccionVenta() {
        return fechaTransaccionVenta;
    }
    public void setFechaTransaccionVenta(Date fechaTransaccionVenta) {
        this.fechaTransaccionVenta = fechaTransaccionVenta;
    }
}

