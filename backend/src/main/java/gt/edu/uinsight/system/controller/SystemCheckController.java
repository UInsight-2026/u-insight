package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.dto.UpdateCheckStatusRequest;
import gt.edu.uinsight.system.model.SystemCheck;
import gt.edu.uinsight.system.service.SystemCheckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system/checks")
public class SystemCheckController {

    private final SystemCheckService service;

    public SystemCheckController(SystemCheckService service) {
        this.service = service;
    }

    // Método para paginación y filtros 
    @GetMapping
    public ResponseEntity<Page<SystemCheck>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String component,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findAll(status, component, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SystemCheck> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateCheckStatusRequest request) {

        SystemCheck updatedCheck = service.updateStatus(id, request);
        return ResponseEntity.ok(updatedCheck);
    }
}