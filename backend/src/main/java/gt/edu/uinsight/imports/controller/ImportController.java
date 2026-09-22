package gt.edu.uinsight.imports.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gt.edu.uinsight.imports.dto.response.ImportStatusResponse;
import gt.edu.uinsight.imports.dto.response.ImportValidateResponse;
import gt.edu.uinsight.imports.exception.ErrorResponse;
import gt.edu.uinsight.imports.service.ImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/v1/imports")
@Tag(name = "Importaciones",
        description = "Carga y validación de calificaciones vía CSV — célula A7")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @Operation(
            summary = "Validar un archivo CSV de calificaciones (esqueleto)",
            description = "Recibe el archivo, lo registra como una nueva Importacion en "
                    + "estado PENDIENTE y devuelve su resumen inicial. El parseo por fila "
                    + "y las reglas de negocio de la sección 4 de la cédula se incorporan "
                    + "en la Semana 3."
    )
    @ApiResponse(responseCode = "200", description = "Archivo recibido y registrado")
    @ApiResponse(responseCode = "400",
            description = "Archivo vacío, ausente o con extensión inválida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/grades/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportValidateResponse> validar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("usuarioId") Long usuarioId) {
        ImportValidateResponse response = importService.validar(file, usuarioId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Consultar el estado de una importación",
            description = "Primer endpoint funcional del módulo (tarea 13 del backlog).")
    @ApiResponse(responseCode = "200", description = "Importación encontrada")
    @ApiResponse(responseCode = "404", description = "No existe una importación con ese id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ImportStatusResponse> consultarEstado(@PathVariable Long id) {
        ImportStatusResponse response = importService.consultarEstado(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
