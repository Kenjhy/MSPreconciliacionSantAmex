package mx.com.nmp.mspreconciliacion.model.dto;

public class MoPagosConciliadosMitDTO {

    private CorresponsalDTO corresponsal;
    private CoreDTO core;

    public CorresponsalDTO getCorresponsal() {
        return corresponsal;
    }

    public void setCorresponsal(CorresponsalDTO corresponsal) {
        this.corresponsal = corresponsal;
    }

    public CoreDTO getCore() { return core; }

    public void setCore(CoreDTO core) { this.core = core; }
}
