package gt.edu.uinsight.alert.service;

import gt.edu.uinsight.alert.dto.AlertResponse;
import gt.edu.uinsight.alert.dto.UpdateAlertStatusRequest;
import gt.edu.uinsight.alert.model.Alert;
import gt.edu.uinsight.alert.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    private Alert mockAlert;
    private UpdateAlertStatusRequest request;

    @BeforeEach
    void setUp() {
        mockAlert = new Alert();
        mockAlert.setId(1L);
        mockAlert.setStatus("NEW");

        request = new UpdateAlertStatusRequest();
    }

    // Prueba 1: Transición válida
    @Test
    void updateAlertStatus_ValidTransition_ReturnsUpdatedAlert() {
        request.setStatus("IN_PROGRESS");
        when(alertRepository.findById(1L)).thenReturn(Optional.of(mockAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(mockAlert);

        AlertResponse response = alertService.updateAlertStatus(1L, request);

        assertEquals("IN_PROGRESS", response.getStatus());
        verify(alertRepository, times(1)).save(mockAlert);
    }

    // Prueba 2: Regla RN01 (Salto inválido de NEW a RESOLVED)
    @Test
    void updateAlertStatus_InvalidTransitionRn01_ThrowsException() {
        request.setStatus("RESOLVED");
        when(alertRepository.findById(1L)).thenReturn(Optional.of(mockAlert));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            alertService.updateAlertStatus(1L, request);
        });

        assertTrue(exception.getMessage().contains("RN01"));
        verify(alertRepository, never()).save(any());
    }

    // Prueba 3: Regla RN05 (No editar alerta ya resuelta)
    @Test
    void updateAlertStatus_AlreadyResolvedRn05_ThrowsException() {
        mockAlert.setStatus("RESOLVED");
        request.setStatus("IN_PROGRESS");
        when(alertRepository.findById(1L)).thenReturn(Optional.of(mockAlert));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            alertService.updateAlertStatus(1L, request);
        });

        assertTrue(exception.getMessage().contains("RN05"));
        verify(alertRepository, never()).save(any());
    }

    // Prueba 4: Regla RN03 (Registrar fecha al resolver)
    @Test
    void updateAlertStatus_ToResolved_SetsResolvedAtRn03() {
        mockAlert.setStatus("IN_PROGRESS"); // Estado previo válido
        request.setStatus("RESOLVED");
        
        when(alertRepository.findById(1L)).thenReturn(Optional.of(mockAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(mockAlert);

        AlertResponse response = alertService.updateAlertStatus(1L, request);

        assertNotNull(response.getResolvedAt());
        assertEquals("RESOLVED", response.getStatus());
    }

    // Prueba 5: Alerta no encontrada
    @Test
    void getAlertById_NotFound_ThrowsException() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alertService.getAlertById(99L);
        });

        assertEquals("Alerta no encontrada con ID: 99", exception.getMessage());
    }
}