package gt.edu.uinsight.indicatorconfiguration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import gt.edu.uinsight.indicatorconfiguration.dto.request.CreateIndicatorConfigurationRequest;
import gt.edu.uinsight.indicatorconfiguration.dto.request.UpdateIndicatorConfigurationRequest;
import gt.edu.uinsight.indicatorconfiguration.dto.response.IndicatorConfigurationResponse;
import gt.edu.uinsight.indicatorconfiguration.exception.IndicatorConfigurationConflictException;
import gt.edu.uinsight.indicatorconfiguration.exception.IndicatorConfigurationNotFoundException;
import gt.edu.uinsight.indicatorconfiguration.service.IndicatorConfigurationService;

@WebMvcTest(IndicatorConfigurationController.class)
class IndicatorConfigurationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IndicatorConfigurationService service;

    @Test
    void create_debeRetornar201_cuandoLaSolicitudEsValida() throws Exception {
        IndicatorConfigurationResponse response = new IndicatorConfigurationResponse(
                1L,
                "HIGH_RISK_PERCENTAGE",
                "50",
                "Porcentaje considerado de alto riesgo",
                LocalDateTime.now());

        when(service.create(any(CreateIndicatorConfigurationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/indicator-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "key": "HIGH_RISK_PERCENTAGE",
                          "value": "50",
                          "description": "Porcentaje considerado de alto riesgo"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.key").value("HIGH_RISK_PERCENTAGE"))
                .andExpect(jsonPath("$.value").value("50"));
    }

    @Test
    void create_debeRetornar400_cuandoLaClaveEsInvalida() throws Exception {
        mockMvc.perform(post("/api/v1/indicator-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "key": "clave invalida",
                          "value": "50"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void findByKey_debeRetornar404_cuandoNoExiste() throws Exception {
        when(service.findByKey("DOES_NOT_EXIST"))
                .thenThrow(new IndicatorConfigurationNotFoundException(
                        "Configuracion no encontrada"));

        mockMvc.perform(get("/api/v1/indicator-configurations/DOES_NOT_EXIST"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("INDICATOR_CONFIGURATION_NOT_FOUND"));
    }

    @Test
    void create_debeRetornar409_cuandoLaClaveYaExiste() throws Exception {
        when(service.create(any(CreateIndicatorConfigurationRequest.class)))
                .thenThrow(new IndicatorConfigurationConflictException(
                        "La clave ya existe"));

        mockMvc.perform(post("/api/v1/indicator-configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "key": "HIGH_RISK_PERCENTAGE",
                          "value": "50"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("INDICATOR_CONFIGURATION_CONFLICT"));
    }

    @Test
    void update_debeRetornar200_cuandoLaActualizacionEsValida() throws Exception {
        IndicatorConfigurationResponse response = new IndicatorConfigurationResponse(
                1L,
                "HIGH_RISK_PERCENTAGE",
                "55",
                "Umbral actualizado",
                LocalDateTime.now());

        when(service.update(
                eq("HIGH_RISK_PERCENTAGE"),
                any(UpdateIndicatorConfigurationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put(
                "/api/v1/indicator-configurations/HIGH_RISK_PERCENTAGE")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "value": "55",
                          "description": "Umbral actualizado"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key")
                        .value("HIGH_RISK_PERCENTAGE"))
                .andExpect(jsonPath("$.value").value("55"));
    }
}