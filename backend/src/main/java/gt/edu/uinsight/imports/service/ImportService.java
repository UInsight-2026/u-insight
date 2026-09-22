package gt.edu.uinsight.imports.service;

import gt.edu.uinsight.imports.config.CsvImportProperties;
import gt.edu.uinsight.imports.dto.response.ImportStatusResponse;
import gt.edu.uinsight.imports.dto.response.ImportValidateResponse;
import gt.edu.uinsight.imports.entity.EstadoImportacion;
import gt.edu.uinsight.imports.entity.Importacion;
import gt.edu.uinsight.imports.exception.ImportNotFoundException;
import gt.edu.uinsight.imports.exception.InvalidCsvFileException;
import gt.edu.uinsight.imports.repository.ImportacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;


@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final ImportacionRepository importacionRepository;
    private final CsvImportProperties csvImportProperties;

    public ImportService(ImportacionRepository importacionRepository,
                          CsvImportProperties csvImportProperties) {
        this.importacionRepository = importacionRepository;
        this.csvImportProperties = csvImportProperties;
    }

    public ImportValidateResponse validar(MultipartFile file, Long usuarioId) {
        if (file == null || file.isEmpty()) {
            throw new InvalidCsvFileException(
                    "El archivo CSV está vacío o no fue proporcionado.");
        }

        String nombre = file.getOriginalFilename();
        String extensionPermitida = csvImportProperties.getAllowedExtension();
        if (nombre == null || !nombre.toLowerCase().endsWith(extensionPermitida)) {
            throw new InvalidCsvFileException(
                    "El archivo debe tener extensión " + extensionPermitida + ".");
        }

        Importacion importacion = new Importacion(
                nombre, LocalDateTime.now(), EstadoImportacion.PENDIENTE, usuarioId);
        importacion = importacionRepository.save(importacion);
        log.info("RESOURCE_CREATED tipo=Importacion id={} archivo={}",
                importacion.getId(), nombre);

        
        return new ImportValidateResponse(
                importacion.getId(),
                importacion.getEstado(),
                importacion.getTotalRegistros(),
                importacion.getRegistrosValidos(),
                importacion.getRegistrosInvalidos(),
                Collections.emptyList()
        );
    }

    public ImportStatusResponse consultarEstado(Long id) {
        Importacion importacion = importacionRepository.findById(id)
                .orElseThrow(() -> new ImportNotFoundException(id));

        return new ImportStatusResponse(
                importacion.getId(),
                importacion.getEstado(),
                importacion.getTotalRegistros(),
                importacion.getRegistrosValidos(),
                importacion.getRegistrosInvalidos(),
                importacion.getFechaCarga()
        );
    }
}
