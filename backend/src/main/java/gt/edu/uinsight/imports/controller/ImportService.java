package gt.edu.uinsight.imports.service;

import gt.edu.uinsight.imports.config.CsvImportProperties;
import gt.edu.uinsight.imports.dto.response.ImportErrorDetail;
import gt.edu.uinsight.imports.dto.response.ImportStatusResponse;
import gt.edu.uinsight.imports.dto.response.ImportValidateResponse;
import gt.edu.uinsight.imports.entity.EstadoImportacion;
import gt.edu.uinsight.imports.entity.EstadoValidacion;
import gt.edu.uinsight.imports.entity.Importacion;
import gt.edu.uinsight.imports.entity.RegistroImportacion;
import gt.edu.uinsight.imports.exception.ImportNotConfirmableException;
import gt.edu.uinsight.imports.exception.ImportNotFoundException;
import gt.edu.uinsight.imports.exception.InvalidCsvFileException;
import gt.edu.uinsight.imports.repository.EstudianteValidoRepository;
import gt.edu.uinsight.imports.repository.ImportacionRepository;
import gt.edu.uinsight.imports.repository.RegistroImportacionRepository;
import gt.edu.uinsight.imports.util.ImportLogEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);
    private static final String ENCABEZADO_ESPERADO = "studentCode,evaluationCode,score";

    private final ImportacionRepository importacionRepository;
    private final RegistroImportacionRepository registroImportacionRepository;
    private final EstudianteValidoRepository estudianteValidoRepository;
    private final CsvImportProperties csvImportProperties;

    public ImportService(ImportacionRepository importacionRepository,
                          RegistroImportacionRepository registroImportacionRepository,
                          EstudianteValidoRepository estudianteValidoRepository,
                          CsvImportProperties csvImportProperties) {
        this.importacionRepository = importacionRepository;
        this.registroImportacionRepository = registroImportacionRepository;
        this.estudianteValidoRepository = estudianteValidoRepository;
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

        log.info("{} archivo={} usuarioId={}", ImportLogEvents.IMPORT_STARTED, nombre, usuarioId);

        List<String> lineas;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            lineas = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new InvalidCsvFileException("No se pudo leer el archivo CSV: " + e.getMessage());
        }

        if (lineas.isEmpty()) {
            throw new InvalidCsvFileException("El archivo CSV no contiene encabezado ni datos.");
        }

        String encabezado = lineas.get(0).trim();
        if (!ENCABEZADO_ESPERADO.equalsIgnoreCase(encabezado)) {
            throw new InvalidCsvFileException(
                    "Encabezado inválido. Se esperaba \"" + ENCABEZADO_ESPERADO + "\".");
        }

        Importacion importacion = new Importacion(
                nombre, LocalDateTime.now(), EstadoImportacion.PENDIENTE, usuarioId);
        importacion = importacionRepository.save(importacion);
        log.info("{} tipo=Importacion id={} archivo={}",
                ImportLogEvents.RESOURCE_CREATED, importacion.getId(), nombre);

        List<RegistroImportacion> registros = new ArrayList<>();
        Set<String> clavesVistas = new HashSet<>();
        int validos = 0;
        int invalidos = 0;

        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (linea == null || linea.isBlank()) {
                continue;
            }
            int numeroFila = i + 1;
            String[] columnas = linea.split(",", -1);

            RegistroImportacion registro = validarFila(importacion.getId(), numeroFila, columnas, clavesVistas);
            registros.add(registro);

            if (registro.getEstadoValidacion() == EstadoValidacion.VALIDO) {
                validos++;
            } else {
                invalidos++;
                log.warn("{} importId={} fila={} campo={} motivo={}",
                        ImportLogEvents.BUSINESS_RULE_REJECTED, importacion.getId(),
                        numeroFila, registro.getCampoError(), registro.getMotivoError());
            }
        }

        registroImportacionRepository.saveAll(registros);

        importacion.setTotalRegistros(registros.size());
        importacion.setRegistrosValidos(validos);
        importacion.setRegistrosInvalidos(invalidos);
        importacion.setEstado(EstadoImportacion.VALIDADO);
        importacion = importacionRepository.save(importacion);

        log.info("{} importId={} total={} validos={} invalidos={}",
                ImportLogEvents.IMPORT_VALIDATED, importacion.getId(),
                registros.size(), validos, invalidos);

        List<ImportErrorDetail> errores = registros.stream()
                .filter(r -> r.getEstadoValidacion() == EstadoValidacion.INVALIDO)
                .map(r -> new ImportErrorDetail(r.getNumeroFila(), r.getCampoError(), r.getMotivoError()))
                .collect(Collectors.toList());

        return new ImportValidateResponse(
                importacion.getId(),
                importacion.getEstado(),
                importacion.getTotalRegistros(),
                importacion.getRegistrosValidos(),
                importacion.getRegistrosInvalidos(),
                errores
        );
    }

    private RegistroImportacion validarFila(Long importacionId, int numeroFila, String[] columnas,
                                             Set<String> clavesVistas) {
        String studentCode = columnas.length > 0 ? columnas[0].trim() : "";
        String evaluationCode = columnas.length > 1 ? columnas[1].trim() : "";
        String scoreTexto = columnas.length > 2 ? columnas[2].trim() : "";

        RegistroImportacion registro = new RegistroImportacion(
                importacionId, numeroFila, studentCode, evaluationCode, null, EstadoValidacion.VALIDO);

        if (columnas.length < 3) {
            return rechazar(registro, "fila",
                    "La fila no tiene las 3 columnas esperadas (studentCode,evaluationCode,score).");
        }

        BigDecimal score;
        try {
            score = new BigDecimal(scoreTexto);
        } catch (NumberFormatException ex) {
            return rechazar(registro, "score", "La nota \"" + scoreTexto + "\" no es un valor numérico.");
        }
        registro.setScore(score);

        if (score.compareTo(csvImportProperties.getMinScore()) < 0
                || score.compareTo(csvImportProperties.getMaxScore()) > 0) {
            return rechazar(registro, "score", "La nota " + score + " está fuera del rango permitido ("
                    + csvImportProperties.getMinScore() + "-" + csvImportProperties.getMaxScore() + ").");
        }

        if (studentCode.isEmpty() || !estudianteValidoRepository.existsByStudentCode(studentCode)) {
            return rechazar(registro, "studentCode", "El estudiante \"" + studentCode + "\" no existe.");
        }

        String clave = studentCode + "|" + evaluationCode;
        if (!clavesVistas.add(clave)) {
            return rechazar(registro, "studentCode/evaluationCode",
                    "Registro duplicado para el estudiante " + studentCode
                            + " y la evaluación " + evaluationCode + ".");
        }

        return registro;
    }

    private RegistroImportacion rechazar(RegistroImportacion registro, String campo, String motivo) {
        registro.setEstadoValidacion(EstadoValidacion.INVALIDO);
        registro.setCampoError(campo);
        registro.setMotivoError(motivo);
        return registro;
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

    public ImportStatusResponse confirmar(Long importId) {
        Importacion importacion = importacionRepository.findById(importId)
                .orElseThrow(() -> new ImportNotFoundException(importId));

        if (importacion.getEstado() != EstadoImportacion.VALIDADO) {
            throw new ImportNotConfirmableException(importId, importacion.getEstado());
        }

        importacion.setEstado(EstadoImportacion.CONFIRMADO);
        importacion = importacionRepository.save(importacion);

        log.info("{} importId={} registrosValidos={}",
                ImportLogEvents.IMPORT_CONFIRMED, importacion.getId(), importacion.getRegistrosValidos());

        return new ImportStatusResponse(
                importacion.getId(),
                importacion.getEstado(),
                importacion.getTotalRegistros(),
                importacion.getRegistrosValidos(),
                importacion.getRegistrosInvalidos(),
                importacion.getFechaCarga()
        );
    }

    public Page<ImportErrorDetail> consultarErrores(Long importId, Pageable pageable) {
        if (!importacionRepository.existsById(importId)) {
            throw new ImportNotFoundException(importId);
        }

        return registroImportacionRepository
                .findByImportacionIdAndEstadoValidacion(importId, EstadoValidacion.INVALIDO, pageable)
                .map(r -> new ImportErrorDetail(r.getNumeroFila(), r.getCampoError(), r.getMotivoError()));
    }
}
