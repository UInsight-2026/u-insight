package gt.edu.uinsight.imports.service;

import gt.edu.uinsight.imports.config.CsvImportProperties;
import gt.edu.uinsight.imports.dto.response.ImportValidateResponse;
import gt.edu.uinsight.imports.entity.Importacion;
import gt.edu.uinsight.imports.exception.InvalidCsvFileException;
import gt.edu.uinsight.imports.repository.EstudianteValidoRepository;
import gt.edu.uinsight.imports.repository.ImportacionRepository;
import gt.edu.uinsight.imports.repository.RegistroImportacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportServiceValidationTest {

    @Mock
    private ImportacionRepository importacionRepository;
    @Mock
    private RegistroImportacionRepository registroImportacionRepository;
    @Mock
    private EstudianteValidoRepository estudianteValidoRepository;

    private ImportService service;

    @BeforeEach
    void setUp() {
        CsvImportProperties properties = new CsvImportProperties();
        properties.setAllowedExtension(".csv");
        properties.setMinScore(BigDecimal.ZERO);
        properties.setMaxScore(BigDecimal.valueOf(100));

        service = new ImportService(importacionRepository, registroImportacionRepository,
                estudianteValidoRepository, properties);

        when(importacionRepository.save(any(Importacion.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    private MockMultipartFile csv(String contenido) {
        return new MockMultipartFile("file", "notas.csv", "text/csv",
                contenido.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void rechazaArchivoVacio() {
        MockMultipartFile vacio = new MockMultipartFile("file", "notas.csv", "text/csv", new byte[0]);

        assertThatThrownBy(() -> service.validar(vacio, 1L))
                .isInstanceOf(InvalidCsvFileException.class)
                .hasMessageContaining("vacío");
    }

    @Test
    void rechazaEncabezadoInvalido() {
        MockMultipartFile archivo = csv("codigo,evaluacion,puntaje\nEST-001,E1,75\n");

        assertThatThrownBy(() -> service.validar(archivo, 1L))
                .isInstanceOf(InvalidCsvFileException.class)
                .hasMessageContaining("Encabezado");
    }

    @Test
    void rechazaNotaNoNumerica() {
        when(estudianteValidoRepository.existsByStudentCode(anyString())).thenReturn(true);
        MockMultipartFile archivo = csv("studentCode,evaluationCode,score\nEST-001,E1,ochenta\n");

        ImportValidateResponse resp = service.validar(archivo, 1L);

        assertThat(resp.getRegistrosInvalidos()).isEqualTo(1);
        assertThat(resp.getErrores().get(0).getCampo()).isEqualTo("score");
    }

    @Test
    void rechazaNotaFueraDeRango() {
        when(estudianteValidoRepository.existsByStudentCode(anyString())).thenReturn(true);
        MockMultipartFile archivo = csv("studentCode,evaluationCode,score\nEST-001,E1,150\n");

        ImportValidateResponse resp = service.validar(archivo, 1L);

        assertThat(resp.getRegistrosInvalidos()).isEqualTo(1);
        assertThat(resp.getErrores().get(0).getCampo()).isEqualTo("score");
    }

    @Test
    void rechazaEstudianteInexistente() {
        when(estudianteValidoRepository.existsByStudentCode("EST-999")).thenReturn(false);
        MockMultipartFile archivo = csv("studentCode,evaluationCode,score\nEST-999,E1,80\n");

        ImportValidateResponse resp = service.validar(archivo, 1L);

        assertThat(resp.getRegistrosInvalidos()).isEqualTo(1);
        assertThat(resp.getErrores().get(0).getCampo()).isEqualTo("studentCode");
    }

    @Test
    void rechazaRegistroDuplicado() {
        when(estudianteValidoRepository.existsByStudentCode(anyString())).thenReturn(true);
        MockMultipartFile archivo = csv(
                "studentCode,evaluationCode,score\nEST-001,E1,75\nEST-001,E1,80\n");

        ImportValidateResponse resp = service.validar(archivo, 1L);

        assertThat(resp.getRegistrosValidos()).isEqualTo(1);
        assertThat(resp.getRegistrosInvalidos()).isEqualTo(1);
        assertThat(resp.getErrores().get(0).getMotivo()).contains("duplicado");
    }

    @Test
    void aceptaFilaValida() {
        when(estudianteValidoRepository.existsByStudentCode("EST-001")).thenReturn(true);
        MockMultipartFile archivo = csv("studentCode,evaluationCode,score\nEST-001,E1,75\n");

        ImportValidateResponse resp = service.validar(archivo, 1L);

        assertThat(resp.getRegistrosValidos()).isEqualTo(1);
        assertThat(resp.getRegistrosInvalidos()).isEqualTo(0);
        assertThat(resp.getErrores()).isEmpty();
    }
}
