package mx.com.nmp.mspreconciliacion.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.model.dto.MovCorresponsalDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;

public class ArchivoSFTPUtil {

    private ArchivoSFTPUtil() {

    }

    public static String obtenerNombreArchivo(Date fecha, CorresponsalEnum corresponsal) {
        String nombreArchivo= "";
        SimpleDateFormat formato = null;
        String fechaConsulta = null;

        if (corresponsal.equals(CorresponsalEnum.SANTANDER)) {
            formato = new SimpleDateFormat("ddMMyy");
            nombreArchivo= Constants.NOMBRE_ARCHIVO_SANTANDER;
        }else if (corresponsal.equals(CorresponsalEnum.AMEX)) {
            formato = new SimpleDateFormat("yyMMdd");
            nombreArchivo= Constants.NOMBRE_ARCHIVO_AMEX;
        }

        if (formato != null)
            fechaConsulta = formato.format(fecha);
        return nombreArchivo.concat(fechaConsulta);
    }


    public static List<MovCorresponsalDTO> actualizarNombreSFTP(List<MovCorresponsalDTO> lista, String nombreArchivo) {
        if (!lista.isEmpty()) {
            lista.stream().forEach(mov-> mov.setNombreArchivo(nombreArchivo));
        }
        return lista;
    }

}
