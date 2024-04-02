package mx.com.nmp.mspreconciliacion.model.dto;

import lombok.NoArgsConstructor;
import mx.com.nmp.mspreconciliacion.model.config.ColumnaArchivo;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
public class ComisionesDetalleDTO {
    private static final String EMPTY_VALUE = "\"null\"";
    private static final String COMILLA = "\"";
    private static final String COMILLAS = "\"\"";
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    @ColumnaArchivo(nombreColumna = "Banco")
    private String banco;
    @ColumnaArchivo(nombreColumna = "Sucursal")
    private String sucursal;
    @ColumnaArchivo(nombreColumna = "FechaVenta")
    private Date fechaVenta;
    @ColumnaArchivo(nombreColumna = "ImporteNeto")
    private BigDecimal importeNeto;
    @ColumnaArchivo(nombreColumna = "ComisionTransaccional")
    private BigDecimal comisionTransaccional;
    @ColumnaArchivo(nombreColumna = "IvaTransaccional")
    private BigDecimal ivaTransaccional;
    @ColumnaArchivo(nombreColumna = "Sobretasa")
    private BigDecimal sobretasa;
    @ColumnaArchivo(nombreColumna = "IvaSobretasa")
    private BigDecimal ivaSobretasa;
    @ColumnaArchivo(nombreColumna = "SobretasaMIDAS")
    private BigDecimal sobretasaMIDAS;
    @ColumnaArchivo(nombreColumna = "ComisionTransaccionalMIDAS")
    private BigDecimal comisionTransaccionalMIDAS;
    @ColumnaArchivo(nombreColumna = "ComisionTransaccionalEPA")
    private BigDecimal comisionTransaccionalEPA;
    @ColumnaArchivo(nombreColumna = "MontoTotal")
    private BigDecimal montoTotal;

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
        String respuesta =  format(getBanco()) + separador +
                format(getSucursal()) + separador +
                formatDate(getFechaVenta()) + separador +
                formatNumber(getImporteNeto()) + separador +
                formatNumber(getComisionTransaccional()) + separador +
                formatNumber(getIvaTransaccional()) + separador +
                formatNumber(getSobretasa()) + separador +
                formatNumber(getIvaSobretasa()) + separador +
                formatNumber(getSobretasaMIDAS()) + separador +
                formatNumber(getComisionTransaccionalMIDAS()) + separador +
                formatNumber(getComisionTransaccionalEPA()) + separador +
                formatNumber(getMontoTotal());

        return respuesta;
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



    //start get-set
    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
    }

    public BigDecimal getComisionTransaccional() {
        return comisionTransaccional;
    }

    public void setComisionTransaccional(BigDecimal comisionTransaccional) {
        this.comisionTransaccional = comisionTransaccional;
    }

    public BigDecimal getIvaTransaccional() {
        return ivaTransaccional;
    }

    public void setIvaTransaccional(BigDecimal ivaTransaccional) {
        this.ivaTransaccional = ivaTransaccional;
    }

    public BigDecimal getSobretasa() {
        return sobretasa;
    }

    public void setSobretasa(BigDecimal sobretasa) {
        this.sobretasa = sobretasa;
    }

    public BigDecimal getIvaSobretasa() {
        return ivaSobretasa;
    }

    public void setIvaSobretasa(BigDecimal ivaSobretasa) {
        this.ivaSobretasa = ivaSobretasa;
    }

    public BigDecimal getSobretasaMIDAS() {
        return sobretasaMIDAS;
    }

    public void setSobretasaMIDAS(BigDecimal sobretasaMIDAS) {
        this.sobretasaMIDAS = sobretasaMIDAS;
    }

    public BigDecimal getComisionTransaccionalMIDAS() {
        return comisionTransaccionalMIDAS;
    }

    public void setComisionTransaccionalMIDAS(BigDecimal comisionTransaccionalMIDAS) {
        this.comisionTransaccionalMIDAS = comisionTransaccionalMIDAS;
    }

    public BigDecimal getComisionTransaccionalEPA() {
        return comisionTransaccionalEPA;
    }

    public void setComisionTransaccionalEPA(BigDecimal comisionTransaccionalEPA) {
        this.comisionTransaccionalEPA = comisionTransaccionalEPA;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
}
