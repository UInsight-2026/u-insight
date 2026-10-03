package gt.edu.uinsight.analytics.summary.controller;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import gt.edu.uinsight.analytics.centraltendency.exception.GlobalAnalyticsExceptionHandler;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.service.SectionValidationService;
import gt.edu.uinsight.analytics.summary.service.SummaryService;

@WebMvcTest(
        controllers = SummaryController.class,
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        SummaryController.class,
                        GlobalAnalyticsExceptionHandler.class
                }
        )
)
class SummaryControllerWebMvcTest {

    private static final String BASE = "/api/v1/analytics/sections";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SummaryService summaryService;

    @MockitoBean
    private SectionValidationService sectionValidationService;

    @Test
    @DisplayName("GET summary completo responde 200")
    void getSummary_responde200CuandoCompleto() throws Exception {

        SectionSummary summary = new SectionSummary();
        summary.setSectionId(1L);
        summary.setStudentsAtRisk(2);
        summary.setUnavailableComponents(Collections.emptyList());

        when(sectionValidationService.exists(1L)).thenReturn(true);
        when(summaryService.getSummary(1L)).thenReturn(summary);

        mockMvc.perform(get(BASE + "/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sectionId").value(1))
                .andExpect(jsonPath("$.studentsAtRisk").value(2))
                .andExpect(jsonPath("$.unavailableComponents").isEmpty());
    }

    @Test
    @DisplayName("GET summary parcial responde 206")
    void getSummary_responde206CuandoEsParcial() throws Exception {

        SectionSummary summary = new SectionSummary();
        summary.setSectionId(1L);
        summary.setStudentsAtRisk(2);
        summary.setUnavailableComponents(List.of("trend"));

        when(sectionValidationService.exists(1L)).thenReturn(true);
        when(summaryService.getSummary(1L)).thenReturn(summary);

        mockMvc.perform(get(BASE + "/1/summary"))
                .andExpect(status().isPartialContent())
                .andExpect(jsonPath("$.sectionId").value(1))
                .andExpect(jsonPath("$.unavailableComponents[0]").value("trend"));
    }

    @Test
    @DisplayName("GET summary con sección inexistente responde 404")
    void getSummary_responde404CuandoSeccionNoExiste() throws Exception {

        when(sectionValidationService.exists(999L)).thenReturn(false);

        mockMvc.perform(get(BASE + "/999/summary"))
                .andExpect(status().isNotFound());

        org.mockito.Mockito.verify(summaryService, never()).getSummary(999L);
    }

    @Test
    @DisplayName("GET summary con error inesperado responde 500")
    void getSummary_responde500CuandoOcurreError() throws Exception {

        when(sectionValidationService.exists(1L)).thenReturn(true);
        when(summaryService.getSummary(1L))
                .thenThrow(new RuntimeException("Error de prueba"));

        mockMvc.perform(get(BASE + "/1/summary"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message")
                        .value("Error inesperado en el servidor"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
