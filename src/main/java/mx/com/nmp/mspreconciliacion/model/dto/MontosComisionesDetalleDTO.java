package mx.com.nmp.mspreconciliacion.model.dto;

import mx.com.nmp.mspreconciliacion.model.config.ColumnaArchivo;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MontosComisionesDetalleDTO {
    private static final String EMPTY_VALUE = "\"null\"";
    private static final String COMILLA = "\"";
    private static final String COMILLAS = "\"\"";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    @ColumnaArchivo(nombreColumna = "ImporteNeto_m")
    private BigDecimal importeNeto;
    @ColumnaArchivo(nombreColumna = "ComisionTransaccional_m")
    private BigDecimal comisionTransaccional;
    @ColumnaArchivo(nombreColumna = "IvaTransaccional_m")
    private BigDecimal ivaTransaccional;
    @ColumnaArchivo(nombreColumna = "Sobretasa_m")
    private BigDecimal sobretasa;
    @ColumnaArchivo(nombreColumna = "IvaSobretasa_m")
    private BigDecimal ivaSobretasa;
    @ColumnaArchivo(nombreColumna = "MontoTotal_m")
    private BigDecimal montoTotal;

    public MontosComisionesDetalleDTO(){
        setImporteNeto(BigDecimal.ZERO);
        setComisionTransaccional(BigDecimal.ZERO);
        setIvaTransaccional(BigDecimal.ZERO);
        setSobretasa(BigDecimal.ZERO);
        setIvaSobretasa(BigDecimal.ZERO);
        setMontoTotal(BigDecimal.ZERO);
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

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
    //end get-set

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
        return formatNumber(getImporteNeto()) + separador +
                formatNumber(getComisionTransaccional()) + separador +
                formatNumber(getIvaTransaccional()) + separador +
                formatNumber(getSobretasa()) + separador +
                formatNumber(getIvaSobretasa()) + separador +
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



}
