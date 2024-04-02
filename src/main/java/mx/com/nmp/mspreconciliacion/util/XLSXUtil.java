package mx.com.nmp.mspreconciliacion.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase de utileria para procesar/generar archivos XLSX
 */
public class XLSXUtil {

    private XLSXUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final String FORMATO_MONEDA = "$#,##0.00;$#,##0.00";

    /**
     * Método que genera una celda con formato de moneda
     * @param libro libro xlsx
     * @param fila fila para crear la celda
     * @param numberCell numero de columna
     * @param value valor de la celda
     * @param isFormula indica si el valor es formula
     * @return celda
     */
    public static Cell createCellWithCurrenyFormat(XSSFWorkbook libro, Row fila, Integer numberCell, String value, boolean isFormula, CellStyle cellStyle){
        Cell celda = fila.createCell(numberCell);
        if(isFormula){
            celda.setCellFormula(value);
        }else {
            celda.setCellValue(Double.parseDouble(value));
        }
        if(cellStyle != null){
            celda.setCellStyle(cellStyle);
        }
        CellUtil.setCellStyleProperty(celda, CellUtil.DATA_FORMAT, libro.createDataFormat().getFormat(FORMATO_MONEDA));
        return celda;
    }

    /**
     * Método que genera el estilo de una celda
     * @param libro libro xlsx
     * @param fontColor color de la fuente
     * @param foregroundColor color del rellono
     * @param isBold indica si la fuente es negrita
     * @return CellStyle
     */
    public static CellStyle buildCellStyle(XSSFWorkbook libro, IndexedColors fontColor, IndexedColors foregroundColor, boolean isBold){
        final CellStyle style = libro.createCellStyle();
        final XSSFFont font = libro.createFont();
        font.setBold(isBold);
        font.setColor(fontColor.getIndex());
        style.setFillForegroundColor(foregroundColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(font);
        return style;
    }

    /**
     * Método que agrega un estilo a un conjunto de celdas de una fila
     * @param row fila a agregar el etilo
     * @param style estilo
     * @param colInit columna inicial
     * @param colFinal columnsa final
     */
    public static void setCellStyleRow(Row row, CellStyle style, int colInit, int colFinal){
        for (int i = colInit; i <= colFinal; i++){
            CellUtil.getCell(row, i).setCellStyle(style);
        }
    }

}
