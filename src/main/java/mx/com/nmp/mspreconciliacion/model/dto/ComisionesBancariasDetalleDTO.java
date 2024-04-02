package mx.com.nmp.mspreconciliacion.model.dto;

import mx.com.nmp.mspreconciliacion.model.config.ColumnaArchivo;
import mx.com.nmp.mspreconciliacion.model.enums.TipoTarjetaEnum;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ComisionesBancariasDetalleDTO {
    private static final String EMPTY_VALUE = "\"null\"";
    private static final String COMILLA = "\"";
    private static final String COMILLAS = "\"\"";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    @ColumnaArchivo(nombreColumna = "Sucursal")
    private String sucursal;
    @ColumnaArchivo(nombreColumna = "Fecha")
    private String fecha;
    @ColumnaArchivo(nombreColumna = "Importe")
    private BigDecimal importe;
    @ColumnaArchivo(nombreColumna = "Emisor")
    private String emisor;
    @ColumnaArchivo(nombreColumna = "Banco")
    private String banco;
    @ColumnaArchivo(nombreColumna = "TipoTarjeta")
    private TipoTarjetaEnum tipoTarjeta;
    @ColumnaArchivo(nombreColumna = "TipoPago")
    private String tipoPago;
    @ColumnaArchivo(nombreColumna = "ComisionTransaccional")
    private BigDecimal comisionTransaccional;
    @ColumnaArchivo(nombreColumna = "IvaTransaccionalidad")
    private BigDecimal ivaTransaccionalidad;
    @ColumnaArchivo(nombreColumna = "SobreTasa")
    private BigDecimal sobreTasa;
    @ColumnaArchivo(nombreColumna = "IvaSobreTasa")
    private BigDecimal ivaSobreTasa;
    @ColumnaArchivo(nombreColumna = "MontoTotal")
    private BigDecimal montoTotal;

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public TipoTarjetaEnum getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(TipoTarjetaEnum tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public BigDecimal getComisionTransaccional() {
        return comisionTransaccional;
    }

    public void setComisionTransaccional(BigDecimal comisionTransaccional) {
        this.comisionTransaccional = comisionTransaccional;
    }

    public BigDecimal getIvaTransaccionalidad() {
        return ivaTransaccionalidad;
    }

    public void setIvaTransaccionalidad(BigDecimal ivaTransaccionalidad) {
        this.ivaTransaccionalidad = ivaTransaccionalidad;
    }

    public BigDecimal getSobreTasa() {
        return sobreTasa;
    }

    public void setSobreTasa(BigDecimal sobreTasa) {
        this.sobreTasa = sobreTasa;
    }

    public BigDecimal getIvaSobreTasa() {
        return ivaSobreTasa;
    }

    public void setIvaSobreTasa(BigDecimal ivaSobreTasa) {
        this.ivaSobreTasa = ivaSobreTasa;
    }

    public static String getEmptyValue() {
        return EMPTY_VALUE;
    }

    public static String getCOMILLA() {
        return COMILLA;
    }

    public static String getCOMILLAS() {
        return COMILLAS;
    }

    public static String getDateFormat() {
        return DATE_FORMAT;
    }


    public String toStringSCV() {
        String separador = ",";
        return format(getSucursal()) + separador +
                format(getFecha()) + separador +
                formatNumber(getImporte()) + separador +
                format(getEmisor()) + separador +
                format(getBanco()) + separador +
                format(getTipoTarjeta().getNombre()) + separador +//buscar
                format(getTipoPago()) + separador +
                formatNumber(getComisionTransaccional()) + separador +
                formatNumber(getIvaTransaccionalidad()) + separador +
                formatNumber(getSobreTasa()) + separador +
                formatNumber(getIvaSobreTasa())+ separador +
                formatNumber(getMontoTotal());
    }

    private String format(String value) {
        if (value == null || Strings.isBlank(value)) {
            return getEmptyValue();
        }
        value = value.replaceAll(getCOMILLA(), getCOMILLAS());
        return getCOMILLA().concat(value).concat(getCOMILLA());
    }

    private String formatNumber(Number value) {
        if (value == null) {
            return getEmptyValue();
        }
        return getCOMILLA().concat(value.toString()).concat(getCOMILLA());
    }

    private String formatDate(Date value) {
        DateFormat dateFormat = new SimpleDateFormat(getDateFormat());
        if (value == null) {
            return getEmptyValue();
        }
        String strDate = dateFormat.format(value);
        return getCOMILLA().concat(strDate).concat(getCOMILLA());
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}
