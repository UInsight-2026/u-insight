
package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.service.IntegrationStatusService;
import gt.edu.uinsight.system.service.IntegrationStatusService.ModuleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = IntegrationStatusController.class,
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = IntegrationStatusController.class
        )
)
class IntegrationStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IntegrationStatusService service;

    @Test
    void getIntegrationStatus_multipleModules_returnsAllModules()
            throws Exception {

        when(service.getIntegrationStatus()).thenReturn(List.of(
                new ModuleStatus(
                        "users", "http://localhost:8081/health",
                        "UP", 25L, "Funcionando"
                ),
                new ModuleStatus(
                        "payments", "http://localhost:8082/health",
                        "DEGRADED", 150L, "Respuesta lenta"
                ),
                new ModuleStatus(
                        "reports", "http://localhost:8083/health",
                        "UP", 30L, "Funcionando"
                )
        ));

        mockMvc.perform(get("/api/v1/system/integration-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("users"))
                .andExpect(jsonPath("$[0].status").value("UP"))
                .andExpect(jsonPath("$[1].name").value("payments"))
                .andExpect(jsonPath("$[1].status").value("DEGRADED"))
                .andExpect(jsonPath("$[2].name").value("reports"))
                .andExpect(jsonPath("$[2].status").value("UP"));
    }

    @Test
    void getIntegrationStatus_downModule_returnsDownAlongsideUp()
            throws Exception {

        when(service.getIntegrationStatus()).thenReturn(List.of(
                new ModuleStatus(
                        "users", "http://localhost:8081/health",
                        "UP", 20L, "Funcionando"
                ),
                new ModuleStatus(
                        "payments", "http://localhost:8082/health",
                        "DOWN", 100L, "Error de conexión"
                ),
                new ModuleStatus(
                        "reports", "http://localhost:8083/health",
                        "UP", 35L, "Funcionando"
                )
        ));

        mockMvc.perform(get("/api/v1/system/integration-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].status").value("UP"))
                .andExpect(jsonPath("$[1].status").value("DOWN"))
                .andExpect(jsonPath("$[2].status").value("UP"));
    }
}
