package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.dto.request.CreateCheckRequest;
import gt.edu.uinsight.system.dto.response.CheckResponse;
import gt.edu.uinsight.system.service.SystemCheckService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system/checks")
public class SystemCheckController {

    private final SystemCheckService service;

    public SystemCheckController(SystemCheckService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CheckResponse> createCheck(
            @Valid @RequestBody CreateCheckRequest request,
            UriComponentsBuilder uriBuilder) {

        CheckResponse created = service.createCheck(request);

        return ResponseEntity
                .created(uriBuilder.path("/api/v1/system/checks/{id}")
                        .buildAndExpand(created.id())
                        .toUri())
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<CheckResponse>> getAllChecks() {
        return ResponseEntity.ok(service.getAllChecks());
    }

    @GetMapping("/{id}")
    public CheckResponse getCheckById(@PathVariable Long id) {
        return service.getCheckById(id);
    }
}
