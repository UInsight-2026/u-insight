// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.controller;

import gt.edu.uinsight.followup.dto.response.FollowUpResponse;
import gt.edu.uinsight.followup.entity.FollowUpResult;
import gt.edu.uinsight.followup.exception.FollowUpExceptionHandler;
import gt.edu.uinsight.followup.service.FollowUpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de la capa web del módulo de seguimiento: verifica la delegación al
 * service, la traducción a HTTP (RN-5/RN-6 mapeados vía {@link FollowUpExceptionHandler})
 * y la validación Bean Validation del request.
 */
@WebMvcTest(FollowUpController.class)
@Import(FollowUpExceptionHandler.class)
class FollowUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FollowUpService followUpService;

    @Test
    void create_debeResponder201_cuandoElRequestEsValido() throws Exception {
        Long interventionId = 5L;
        FollowUpResponse response = new FollowUpResponse(1L, interventionId, LocalDate.now(),
                "El estudiante mostró mejoría", FollowUpResult.IMPROVED);

        when(followUpService.create(eq(interventionId), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/interventions/{interventionId}/follow-ups", interventionId)
                        .contentType("application/json")
                        .content("""
                                {
                                    "followUpDate": "%s",
                                    "observation": "El estudiante mostró mejoría",
                                    "result": "IMPROVED"
                                }
                                """.formatted(LocalDate.now())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.interventionId").value(5));
    }

    @Test
    void create_debeResponder400_cuandoObservationEstaEnBlanco() throws Exception {
        Long interventionId = 5L;

        mockMvc.perform(post("/api/v1/interventions/{interventionId}/follow-ups", interventionId)
                        .contentType("application/json")
                        .content("""
                                {
                                    "followUpDate": "%s",
                                    "observation": "   "
                                }
                                """.formatted(LocalDate.now())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void listByIntervention_debeResponder200_conElListadoDelService() throws Exception {
        Long interventionId = 5L;
        FollowUpResponse response = new FollowUpResponse(1L, interventionId, LocalDate.now(),
                "Observación", FollowUpResult.PENDING);

        when(followUpService.listByIntervention(interventionId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/interventions/{interventionId}/follow-ups", interventionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].interventionId").value(5));
    }
}
