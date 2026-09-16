package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.entity.SystemCheckLog;
import gt.edu.uinsight.system.service.SystemCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system/checks")
public class SystemCheckController {

    private final SystemCheckService service;

    public SystemCheckController(SystemCheckService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SystemCheckLog> createCheck(@RequestBody SystemCheckLog log) {
        return ResponseEntity.ok(service.saveCheck(log));
    }

    @GetMapping
    public ResponseEntity<List<SystemCheckLog>> getAllChecks() {
        return ResponseEntity.ok(service.getAllChecks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemCheckLog> getCheckById(@PathVariable Long id) {
        return service.getCheckById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Esto devuelve el 404 que te piden
    }
}