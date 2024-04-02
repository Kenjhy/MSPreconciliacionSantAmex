package mx.com.nmp.mspreconciliacion.model.dto;

public class CoreDTO {

    private Long cliente;
    private Long montoTotal;


    public Long getCliente() {
        return cliente;
    }

    public void setCliente(Long cliente) {
        this.cliente = cliente;
    }

    public Long getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Long montoTotal) {
        this.montoTotal = montoTotal;
    }
}
