package gt.edu.uinsight.system.service;

import gt.edu.uinsight.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.system.dto.UpdateCheckStatusRequest;
import gt.edu.uinsight.system.model.SystemCheck;
import gt.edu.uinsight.system.repository.SystemCheckRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemCheckServiceTest {

    @Mock
    private SystemCheckRepository repository;

    @InjectMocks
    private SystemCheckService service;

    @Test
    void findAll_shouldReturnFilteredPage() {
        // Arrange (Preparar datos)
        SystemCheck check = new SystemCheck("database", "UP");
        Page<SystemCheck> mockPage = new PageImpl<>(List.of(check));
        Pageable pageable = PageRequest.of(0, 10);
        
        when(repository.findByStatusContainingIgnoreCaseAndComponentContainingIgnoreCase(
                eq("UP"), eq("database"), eq(pageable)))
                .thenReturn(mockPage);

        // Act (Ejecutar el método)
        Page<SystemCheck> result = service.findAll("UP", "database", pageable);

        // Assert (Verificar el resultado)
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("database", result.getContent().get(0).getComponent());
    }

    @Test
    void updateStatus_shouldThrowExceptionWhenDownToUp() {
        // Arrange
        SystemCheck mockCheck = new SystemCheck("redis", "DOWN");
        when(repository.findById(1L)).thenReturn(Optional.of(mockCheck));

        UpdateCheckStatusRequest request = new UpdateCheckStatusRequest();
        request.setStatus("UP");

        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, () -> {
            service.updateStatus(1L, request);
        });

        // Verificar que NUNCA se llame a save() porque debe fallar antes
        verify(repository, never()).save(any(SystemCheck.class));
    }
}