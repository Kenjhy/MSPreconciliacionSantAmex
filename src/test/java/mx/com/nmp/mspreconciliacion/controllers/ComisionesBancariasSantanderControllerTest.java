package mx.com.nmp.mspreconciliacion.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.com.nmp.mspreconciliacion.config.Constants;
import mx.com.nmp.mspreconciliacion.exceptions.PagoException;
import mx.com.nmp.mspreconciliacion.model.dto.ArchivoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.CatalogoComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.dto.ComisionesPaginadoDTO;
import mx.com.nmp.mspreconciliacion.model.dto.RequestComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.model.enums.TipoTarjetaEnum;
import mx.com.nmp.mspreconciliacion.model.error.ModelError;
import mx.com.nmp.mspreconciliacion.repository.IPagoConciliadoEPARepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoMITRepository;
import mx.com.nmp.mspreconciliacion.repository.IPagoRepository;
import mx.com.nmp.mspreconciliacion.services.ComsionBancariaService;
import mx.com.nmp.mspreconciliacion.services.impl.ComisionBancariaSantanderServiceImpl;
import mx.com.nmp.mspreconciliacion.util.Response;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ComisionesBancariasSantanderControllerTest {

    /**
     * Constante del endpoint base
     */
    private final String ENDPOINT_BASE = "/mspreconciliacion/comisiones";

    private final String STR_RESPORTE_BASE64 =  "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";

    private static final String DATE_FORMAT = "ddMMyyyy";

    /**
     * Referencia al {@link MockMvc}
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Referencia al {@link ObjectMapper}
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Referencia al servicio {@link ComsionBancariaService} para el corresponal SANTANDER
     */
    @MockBean
    private ComisionBancariaSantanderServiceImpl comisionBancariaSantanderService;

    @MockBean
    private IPagoConciliadoEPARepository pagoConciliadoEPARepository;

    @MockBean
    private IPagoMITRepository pagoMITRepository;

    @MockBean
    private IPagoRepository pagoRepository;


    @BeforeEach
    void setUp(){
        reset(comisionBancariaSantanderService);
    }

    @Test
    void whenRightRequestThenConsultaComisiones() throws Exception {

        RequestComisionesDTO requestComisiones = buildRequestComisiones();

        ComisionesPaginadoDTO comisiones = buildComisionesPaginado(Collections.singletonList(buildComision()));

        when(comisionBancariaSantanderService.consultarComisiones(any(RequestComisionesDTO.class))).thenReturn(comisiones);

        this.mockMvc.perform(post(ENDPOINT_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(requestComisiones)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Response response = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
                    Assertions.assertNotNull(response);
                    Assertions.assertEquals(Constants.MSG_EXITOSO, response.getMessage());
                    ComisionesPaginadoDTO comisionesResult = objectMapper.convertValue(response.getObject(), ComisionesPaginadoDTO.class);
                    Assertions.assertEquals(comisionesResult.getTotalRegistros(),comisiones.getTotalRegistros());
                    Assertions.assertFalse(comisionesResult.getComisionesList().isEmpty());
                });
    }

    @Test
    void whenRightRequestThenReporteComisiones() throws Exception {
        RequestComisionesDTO requestComisiones = buildRequestComisiones();

        when(comisionBancariaSantanderService.reporteComisiones(any(RequestComisionesDTO.class))).thenReturn(STR_RESPORTE_BASE64);

        this.mockMvc.perform(post(ENDPOINT_BASE + "/reporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(requestComisiones)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Response response = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
                    Assertions.assertNotNull(response);
                    Assertions.assertEquals(Constants.MSG_EXITOSO, response.getMessage());

                    ArchivoDTO base64 = objectMapper.convertValue(response.getObject(), ArchivoDTO.class);
                    Assertions.assertNotNull(base64);
                    Assertions.assertEquals(base64.getArchivoBase64(),STR_RESPORTE_BASE64);
                });

    }

    @Test
    void whenConsultarCatalogoComisionesThenOk() throws Exception {

        CatalogoComisionesDTO catalogoComisiones = buildCatalogoComisiones();

        when(comisionBancariaSantanderService.consultarCatalogoComisiones(any(Date.class))).thenReturn(catalogoComisiones);

        String fechaOperacion = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        this.mockMvc.perform(get(ENDPOINT_BASE + "/santander/{fechaOperacion}",fechaOperacion)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Response response = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
                    Assertions.assertNotNull(response);
                    Assertions.assertEquals(Constants.MSG_EXITOSO, response.getMessage());

                    CatalogoComisionesDTO responseCatalogo = objectMapper.convertValue(response.getObject(), CatalogoComisionesDTO.class);
                    Assertions.assertNotNull(responseCatalogo);
                    Assertions.assertNotNull(responseCatalogo.getBanco());
                    Assertions.assertFalse(responseCatalogo.getBanco().isEmpty());
                    Assertions.assertNotNull(responseCatalogo.getSucursal());
                    Assertions.assertFalse(responseCatalogo.getSucursal().isEmpty());
                });
    }

    @Test
    void validateRequestConsultaComisionCorresponsalIncorrecto() throws Exception {

        RequestComisionesDTO requestComisiones = buildRequestComisiones();
        requestComisiones.setCorresponsal("OPENPAY");

        ComisionesPaginadoDTO comisiones = buildComisionesPaginado(Collections.singletonList(buildComision()));

        when(comisionBancariaSantanderService.consultarComisiones(any(RequestComisionesDTO.class))).thenReturn(comisiones);

        this.mockMvc.perform(post(ENDPOINT_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(requestComisiones)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    ModelError response = objectMapper.readValue(result.getResponse().getContentAsString(), ModelError.class);
                    Assertions.assertNotNull(response);
                    Assertions.assertEquals(response.getEstado(), PagoException.CORRESPONSAL_INCORRECTO.getEstado());
                });
    }

    @Test
    void validateRequestReporteComisionesCorresponsalIncorrecto() throws Exception {
        RequestComisionesDTO requestComisiones = buildRequestComisiones();
        requestComisiones.setCorresponsal("OPENPAY");

        when(comisionBancariaSantanderService.reporteComisiones(any(RequestComisionesDTO.class))).thenReturn(STR_RESPORTE_BASE64);


        this.mockMvc.perform(post(ENDPOINT_BASE + "/reporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(requestComisiones)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    ModelError response = objectMapper.readValue(result.getResponse().getContentAsString(), ModelError.class);
                    Assertions.assertNotNull(response);
                    Assertions.assertEquals(response.getEstado(), PagoException.CORRESPONSAL_INCORRECTO.getEstado());
                });

    }

    @Test
    void validateRequestConsultaComisionCorresponsalMissing() throws Exception {

        RequestComisionesDTO requestComisiones = buildRequestComisiones();
        requestComisiones.setCorresponsal(null);

        ComisionesPaginadoDTO comisiones = buildComisionesPaginado(Collections.singletonList(buildComision()));

        when(comisionBancariaSantanderService.consultarComisiones(any(RequestComisionesDTO.class))).thenReturn(comisiones);

        this.mockMvc.perform(post(ENDPOINT_BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(requestComisiones)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    ModelError response = objectMapper.readValue(result.getResponse().getContentAsString(), ModelError.class);
                    Assertions.assertNotNull(response);
                    Assertions.assertEquals("corresponsal: El corresponsal es requerido", response.getDescripcion());
                });
    }

    @Test
    void validateRequestReporteComisionesFechaMissing() throws Exception {
        RequestComisionesDTO requestComisiones = buildRequestComisiones();
        requestComisiones.setFecha(null);

        when(comisionBancariaSantanderService.reporteComisiones(any(RequestComisionesDTO.class))).thenReturn(STR_RESPORTE_BASE64);

        this.mockMvc.perform(post(ENDPOINT_BASE + "/reporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(requestComisiones)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    ModelError response = objectMapper.readValue(result.getResponse().getContentAsString(), ModelError.class);
                    Assertions.assertNotNull(response);
                    Assertions.assertEquals("fecha: La fecha es requerida", response.getDescripcion());
                });

    }

    private RequestComisionesDTO buildRequestComisiones(){
        RequestComisionesDTO requestComisiones = new RequestComisionesDTO();
        requestComisiones.setCorresponsal(CorresponsalEnum.SANTANDER.getNombre());
        requestComisiones.setBanco("VISA");
        requestComisiones.setSucursal("10");
        requestComisiones.setTipoTarjeta(TipoTarjetaEnum.DEBITO.name());
        requestComisiones.setTipoPago("CONTADO");
        requestComisiones.setFecha(new Date());
        return requestComisiones;
    }

    private ComisionesPaginadoDTO buildComisionesPaginado(List<ComisionesDTO> comisiones){
        ComisionesPaginadoDTO comisionesPaginado = new ComisionesPaginadoDTO();
        comisionesPaginado.setTieneMasPaginas(false);
        comisionesPaginado.setTotalRegistros(comisiones.size());
        comisionesPaginado.setNumeroRegistros(comisiones.size());
        comisionesPaginado.setNumeroPagina(0);
        comisionesPaginado.setComisionesList(comisiones);
        return comisionesPaginado;
    }

    private ComisionesDTO buildComision(){
        ComisionesDTO comision = new ComisionesDTO();
        comision.setBanco("BBVA");
        comision.setSucursal("10");
        comision.setFechaLiquidacion(new Date());
        comision.setFechaVenta(new Date());
        comision.setTipoTarjeta("Debito");
        comision.setTipoPago("CONTADO");
        comision.setPlazo("0");

        comision.setImporteNeto(BigDecimal.TEN);
        comision.setComisionTransaccional(BigDecimal.TEN);
        comision.setIvaTransaccional(BigDecimal.TEN);
        comision.setSobretasa(BigDecimal.TEN);
        comision.setIvaSobretasa(BigDecimal.TEN);
        comision.setMontoTotal(BigDecimal.TEN);
        comision.setComisionTransaccionalMIDAS(BigDecimal.ZERO);
        comision.setSobretasaMIDAS(BigDecimal.ZERO);
        return comision;
    }

    private CatalogoComisionesDTO buildCatalogoComisiones(){
        CatalogoComisionesDTO catalogoComisiones = new CatalogoComisionesDTO();
        catalogoComisiones.setBanco(Collections.singletonList("BBVA"));
        catalogoComisiones.setSucursal(Collections.singletonList("10"));
        return catalogoComisiones;
    }


}
