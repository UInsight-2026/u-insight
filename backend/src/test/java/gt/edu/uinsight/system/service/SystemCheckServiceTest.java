package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.request.CreateCheckRequest;
import gt.edu.uinsight.system.dto.request.UpdateCheckStatusRequest;
import gt.edu.uinsight.system.dto.response.CheckResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.entity.SystemCheckLog;
import gt.edu.uinsight.system.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.system.logging.SystemEventLogger;
import gt.edu.uinsight.system.repository.SystemCheckLogRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemCheckServiceTest {

    @Mock
    private SystemCheckLogRepository repository;

    @Mock
    private SystemEventLogger eventLogger;

    @InjectMocks
    private SystemCheckService service;

    @Test
    void createCheckShouldLogResourceCreated() {
        SystemCheckLog saved = buildCheck(7L, "database", CheckStatus.UP);
        when(repository.save(any(SystemCheckLog.class))).thenReturn(saved);

        CheckResponse response = service.createCheck(
                new CreateCheckRequest("database", CheckStatus.UP, "Connection verified"));

        assertEquals(7L, response.id());
        verify(eventLogger).info("RESOURCE_CREATED", 201, "System check created with id 7");
    }

    @Test
    void getChecksShouldReturnFilteredPage() {
        SystemCheckLog log = buildCheck(1L, "database", CheckStatus.UP);
        Page<SystemCheckLog> page = new PageImpl<>(List.of(log));
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findByFilters(eq("database"), eq(CheckStatus.UP), eq(pageable)))
                .thenReturn(page);

        Page<CheckResponse> result = service.getChecks("database", CheckStatus.UP, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("database", result.getContent().get(0).component());
        assertEquals(CheckStatus.UP, result.getContent().get(0).status());
    }

    @Test
    void updateStatusShouldRejectTransitionFromDownToUp() {
        SystemCheckLog log = buildCheck(1L, "redis", CheckStatus.DOWN);
        when(repository.findById(1L)).thenReturn(Optional.of(log));

        UpdateCheckStatusRequest request = new UpdateCheckStatusRequest(CheckStatus.UP);

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateStatus(1L, request));

        verify(repository, never()).save(any(SystemCheckLog.class));
        assertEquals(CheckStatus.DOWN, log.getStatus());
    }

    private SystemCheckLog buildCheck(Long id, String component, CheckStatus status) {
        SystemCheckLog log = new SystemCheckLog();
        log.setId(id);
        log.setComponent(component);
        log.setStatus(status);
        return log;
    }
}
