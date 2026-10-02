package gt.edu.uinsight.imports.service;

import gt.edu.uinsight.imports.config.CsvImportProperties;
import gt.edu.uinsight.imports.dto.response.ImportErrorDetail;
import gt.edu.uinsight.imports.entity.EstadoImportacion;
import gt.edu.uinsight.imports.entity.EstadoValidacion;
import gt.edu.uinsight.imports.entity.Importacion;
import gt.edu.uinsight.imports.entity.RegistroImportacion;
import gt.edu.uinsight.imports.exception.ImportNotConfirmableException;
import gt.edu.uinsight.imports.exception.ImportNotFoundException;
import gt.edu.uinsight.imports.repository.EstudianteValidoRepository;
import gt.edu.uinsight.imports.repository.ImportacionRepository;
import gt.edu.uinsight.imports.repository.RegistroImportacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportServiceQueryTest {

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
        service = new ImportService(importacionRepository, registroImportacionRepository,
                estudianteValidoRepository, properties);
    }

    @Test
    void consultarEstadoLanza404SiNoExiste() {
        when(importacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarEstado(99L))
                .isInstanceOf(ImportNotFoundException.class);
    }

    @Test
    void confirmarLanza409SiNoEstaValidado() {
        Importacion importacion = new Importacion("notas.csv", LocalDateTime.now(),
                EstadoImportacion.PENDIENTE, 1L);
        when(importacionRepository.findById(5L)).thenReturn(Optional.of(importacion));

        assertThatThrownBy(() -> service.confirmar(5L))
                .isInstanceOf(ImportNotConfirmableException.class);
    }

    @Test
    void confirmarMarcaConfirmadoSiEstaValidado() {
        Importacion importacion = new Importacion("notas.csv", LocalDateTime.now(),
                EstadoImportacion.PENDIENTE, 1L);
        importacion.setEstado(EstadoImportacion.VALIDADO);
        when(importacionRepository.findById(7L)).thenReturn(Optional.of(importacion));
        when(importacionRepository.save(any(Importacion.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = service.confirmar(7L);

        assertThat(respuesta.getEstado()).isEqualTo(EstadoImportacion.CONFIRMADO);
    }

    @Test
    void consultarErroresLanza404SiLaImportacionNoExiste() {
        when(importacionRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> service.consultarErrores(42L, PageRequest.of(0, 20)))
                .isInstanceOf(ImportNotFoundException.class);
    }

    @Test
    void consultarErroresDevuelvePaginaDeErrores() {
        when(importacionRepository.existsById(3L)).thenReturn(true);

        RegistroImportacion registro = new RegistroImportacion(
                3L, 2, "EST-999", "E1", null, EstadoValidacion.INVALIDO);
        registro.setCampoError("studentCode");
        registro.setMotivoError("El estudiante \"EST-999\" no existe.");

        Pageable pageable = PageRequest.of(0, 20);
        Page<RegistroImportacion> pagina = new PageImpl<>(List.of(registro), pageable, 1);
        when(registroImportacionRepository.findByImportacionIdAndEstadoValidacion(
                eq(3L), eq(EstadoValidacion.INVALIDO), eq(pageable)))
                .thenReturn(pagina);

        Page<ImportErrorDetail> resultado = service.consultarErrores(3L, pageable);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getCampo()).isEqualTo("studentCode");
    }
}
