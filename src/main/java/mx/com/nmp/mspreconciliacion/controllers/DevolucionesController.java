package mx.com.nmp.mspreconciliacion.controllers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.ActualizarDevEstatusDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ActualizarDevolucionDTO;
import mx.com.nmp.mspreconciliacion.model.dto.DevolucionAMEXDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RequestDevolucionesDTO;
import mx.com.nmp.mspreconciliacion.services.DevolucionesService;
import mx.com.nmp.mspreconciliacion.util.Response;


@RestController
@RequestMapping(value = "/mspreconciliacion")
public class DevolucionesController {

    /**ac
     * Bean de la fabrica de instancias
     */
    @Autowired
    private BeanFactory beanFactory;

    /**
     * Imprime logs de la aplicacion
     */
    private final Logger logger = LoggerFactory.getLogger(DevolucionesController.class);

    @Autowired
    private DevolucionesService devolucionesService;

    /**
     * Permite obtener un listado de devoluciones
     * @param requestDevolucionesDTO
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/devoluciones", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response consultar(@RequestBody @Valid @NotNull RequestDevolucionesDTO requestDevolucionesDTO) throws PagoException {

        logger.info(">>>URL: POST /devoluciones/> REQUEST ENTRANTE: {}", requestDevolucionesDTO);
        return beanFactory.getBean(Response.class, HttpStatus.OK.toString(), Constants.MSG_EXITOSO, devolucionesService.consultarDevoluciones(requestDevolucionesDTO));
    }


    /**
     * Permite obtener un listado de devoluciones AMEX- para proceso automático
     * @param requestDevolucionesDTO
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/devoluciones/consultaamex", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response consultarIndiceAMEX(@RequestBody @Valid @NotNull DevolucionAMEXDTO devolucionAMEXDTO) throws PagoException {

        logger.info(">>>URL: POST /devoluciones/consultaamex > REQUEST ENTRANTE: {}", devolucionAMEXDTO);
        return beanFactory.getBean(Response.class, HttpStatus.OK.toString(), Constants.MSG_EXITOSO, devolucionesService.consultarDevolucionesEPA(devolucionAMEXDTO));
    }

    /**
     * Servicio que permite actualizar el estatus a LIQUIDADA de movimientos en corresponsales Santander/AMEX
     * @param requestDevolucionesDTO
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/devoluciones/actualizar", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response actualizar(@RequestBody @Valid @NotNull ActualizarDevolucionDTO movimientosLiquidar) throws PagoException {

        Response respuesta = null;
        logger.info(">>>URL: POST /devoluciones/actualizar > REQUEST ENTRANTE: {}", movimientosLiquidar);
        if (movimientosLiquidar.getCorresponsal() != null){
            respuesta= beanFactory.getBean(Response.class, HttpStatus.OK.toString(), Constants.MSG_EXITOSO, devolucionesService.actualizarEstatusLiquidar(movimientosLiquidar));
        }
        return respuesta;

    }


    /**
     * Servicio que permite actualizar el estatus a LIQUIDADA de movimientos en corresponsales Santander/AMEX
     * @param requestDevolucionesDTO
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/devoluciones/actualizarliquidadasconciliadas", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response actualizarLiquidadasConciliadas(@RequestBody @Valid @NotNull ActualizarDevEstatusDTO devoluciones) throws PagoException {

        Response respuesta = null;
        String request=  null;
        if (!devoluciones.getDevoluciones().isEmpty()){
            request= new Gson().toJson(devoluciones);
            logger.info(">>>URL: POST /devoluciones/actualizarestatus > REQUEST ENTRANTE: {}", request);
            respuesta= beanFactory.getBean(Response.class, HttpStatus.OK.toString(), Constants.MSG_EXITOSO, devolucionesService.actualizarLiquidadasConciliadas(devoluciones.getDevoluciones()));
        }
        return respuesta;

    }

}
