package gt.edu.uinsight.system.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:uinsight-test;NON_KEYWORDS=YEAR")
public class SystemExceptionHandlerTest {

    @Autowired
    private SystemExceptionHandler systemExceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(systemExceptionHandler)
                .build();
    }

    @Test
    void shouldReturnErrorContractOn404() throws Exception {
        mockMvc.perform(get("/dummy/404"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.timestamp").exists())
               .andExpect(jsonPath("$.status").value(404))
               .andExpect(jsonPath("$.error").exists())
               .andExpect(jsonPath("$.message").exists())
               .andExpect(jsonPath("$.details").exists()) 
               .andExpect(jsonPath("$.traceId").value(not(emptyString())));
    }

    @Test
    void shouldReturnErrorContractOn400() throws Exception {
        mockMvc.perform(post("/dummy/400")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.timestamp").exists())
               .andExpect(jsonPath("$.status").value(400))
               .andExpect(jsonPath("$.error").exists())
               .andExpect(jsonPath("$.message").exists())
               .andExpect(jsonPath("$.details").exists())
               .andExpect(jsonPath("$.traceId").value(not(emptyString())));
    }

    @Test
    void shouldReturnErrorContractOn409() throws Exception {
        mockMvc.perform(post("/dummy/409")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.timestamp").exists())
               .andExpect(jsonPath("$.traceId").exists());
    }

    @RestController
    static class DummyController {
        
        @GetMapping("/dummy/404")
        public void throw404() throws Exception {
            throw Mockito.mock(org.springframework.web.servlet.resource.NoResourceFoundException.class);
        }

        @PostMapping("/dummy/400")
        public void throw400() {
            throw new IllegalArgumentException("Petición inválida");
        }

        @PostMapping("/dummy/409")
        public void throw409() throws Exception {
            throw Mockito.mock(InvalidStatusTransitionException.class);
        }
    }
}