package gt.edu.uinsight.imports.service;

import gt.edu.uinsight.grade.dto.BatchGradeResult;
import gt.edu.uinsight.grade.dto.GradeRequest;
import gt.edu.uinsight.grade.model.EvaluationRef;
import gt.edu.uinsight.grade.repository.EnrollmentRefRepository;
import gt.edu.uinsight.grade.service.GradeService;
import gt.edu.uinsight.imports.config.CsvImportProperties;
import gt.edu.uinsight.imports.dto.response.ImportErrorDetail;
import gt.edu.uinsight.imports.dto.response.ImportStatusResponse;
import gt.edu.uinsight.imports.dto.response.ImportValidateResponse;
import gt.edu.uinsight.imports.entity.EstadoImportacion;
import gt.edu.uinsight.imports.entity.EstadoValidacion;
import gt.edu.uinsight.imports.entity.EstudianteValido;
import gt.edu.uinsight.imports.entity.Importacion;
import gt.edu.uinsight.imports.entity.RegistroImportacion;
import gt.edu.uinsight.imports.exception.ImportNotConfirmableException;
import gt.edu.uinsight.imports.exception.ImportNotFoundException;
import gt.edu.uinsight.imports.exception.InvalidCsvFileException;
import gt.edu.uinsight.imports.repository.EstudianteValidoRepository;
import gt.edu.uinsight.imports.repository.EvaluacionReferenciaRepository;
import gt.edu.uinsight.imports.repository.ImportacionRepository;
import gt.edu.uinsight.imports.repository.RegistroImportacionRepository;
import gt.edu.uinsight.imports.util.ImportLogEvents;
import gt.edu.uinsight.imports.util.StructuredLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);
    private static final String ENCABEZADO_ESPERADO = "studentCode,evaluationCode,score";

    private static final String OP_VALIDAR = "validar";
    private static final String OP_CONFIRMAR = "confirmar";
    private static final String EVENT_GRADES_PROCESSED = "GRADES_PROCESSED";

    private final ImportacionRepository importacionRepository;
    private final RegistroImportacionRepository registroImportacionRepository;
    private final EstudianteValidoRepository estudianteValidoRepository;
    private final CsvImportProperties csvImportProperties;
    private final GradeService gradeService;
    private final EvaluacionReferenciaRepository evaluacionReferenciaRepository;
    private final EnrollmentRefRepository enrollmentRefRepository;

    public ImportService(ImportacionRepository importacionRepository,
                          RegistroImportacionRepository registroImportacionRepository,
                          EstudianteValidoRepository estudianteValidoRepository,
                          CsvImportProperties csvImportProperties,
                          GradeService gradeService,
                          EvaluacionReferenciaRepository evaluacionReferenciaRepository,
                          EnrollmentRefRepository enrollmentRefRepository) {
        this.importacionRepository = importacionRepository;
        this.registroImportacionRepository = registroImportacionRepository;
        this.estudianteValidoRepository = estudianteValidoRepository;
        this.csvImportProperties = csvImportProperties;
        this.gradeService = gradeService;
        this.evaluacionReferenciaRepository = evaluacionReferenciaRepository;
        this.enrollmentRefRepository = enrollmentRefRepository;
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

        StructuredLog.info(log, ImportLogEvents.IMPORT_STARTED, OP_VALIDAR, null,
                "archivo=" + nombre + " usuarioId=" + usuarioId);

        byte[] contenido;
        List<String> lineas;
        try {
            contenido = file.getBytes();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(contenido), StandardCharsets.UTF_8))) {
                lineas = reader.lines().collect(Collectors.toList());
            }
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
        importacion.adjuntarArchivo(contenido, file.getContentType());
        importacion = importacionRepository.save(importacion);
        StructuredLog.info(log, ImportLogEvents.RESOURCE_CREATED, OP_VALIDAR, importacion.getId(),
                "archivo=" + nombre + " bytes=" + importacion.getTamanoBytes()
                        + " sha256=" + importacion.getHashSha256());

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
                StructuredLog.warn(log, ImportLogEvents.BUSINESS_RULE_REJECTED, OP_VALIDAR,
                        importacion.getId(),
                        "fila=" + numeroFila + " campo=" + registro.getCampoError()
                                + " motivo=" + registro.getMotivoError());
            }
        }

        registroImportacionRepository.saveAll(registros);

        importacion.setTotalRegistros(registros.size());
        importacion.setRegistrosValidos(validos);
        importacion.setRegistrosInvalidos(invalidos);
        importacion.setEstado(EstadoImportacion.VALIDADO);
        importacion = importacionRepository.save(importacion);

        StructuredLog.info(log, ImportLogEvents.IMPORT_VALIDATED, OP_VALIDAR, importacion.getId(),
                "total=" + registros.size() + " validos=" + validos + " invalidos=" + invalidos);

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

        Optional<EstudianteValido> estudiante = studentCode.isEmpty()
                ? Optional.empty()
                : estudianteValidoRepository.findByStudentCode(studentCode);
        if (estudiante.isEmpty()) {
            return rechazar(registro, "studentCode", "El estudiante \"" + studentCode + "\" no existe.");
        }

        Resolucion resolucion = resolverEvaluacion(estudiante.get().getId(), studentCode, evaluationCode);
        if (resolucion.evaluacion() == null) {
            return rechazar(registro, resolucion.campo(), resolucion.motivo());
        }
        EvaluationRef evaluacion = resolucion.evaluacion();

        BigDecimal maximo = evaluacion.getMaximumScore();
        if (score.compareTo(csvImportProperties.getMinScore()) < 0 || score.compareTo(maximo) > 0) {
            return rechazar(registro, "score", "La nota " + score + " está fuera del rango permitido ("
                    + csvImportProperties.getMinScore() + "-" + maximo + ") para la evaluación "
                    + evaluationCode + ".");
        }

        String clave = estudiante.get().getId() + "|" + evaluacion.getId();
        if (!clavesVistas.add(clave)) {
            return rechazar(registro, "studentCode/evaluationCode",
                    "Registro duplicado para el estudiante " + studentCode
                            + " y la evaluación " + evaluationCode + ".");
        }

        return registro;
    }

    /**
     * Resuelve el evaluationCode del CSV contra grade_evaluation_ref (por nombre) y
     * se queda con la evaluación de la sección en la que el estudiante está inscrito.
     */
    private Resolucion resolverEvaluacion(Long studentId, String studentCode, String evaluationCode) {
        if (evaluationCode == null || evaluationCode.isBlank()) {
            return Resolucion.error("evaluationCode", "El código de evaluación está vacío.");
        }

        List<EvaluationRef> candidatas = evaluacionReferenciaRepository.findByNameIgnoreCase(evaluationCode);
        if (candidatas.isEmpty()) {
            return Resolucion.error("evaluationCode", "La evaluación \"" + evaluationCode + "\" no existe.");
        }

        List<EvaluationRef> inscritas = candidatas.stream()
                .filter(e -> enrollmentRefRepository.existsByStudentIdAndSectionId(studentId, e.getSectionId()))
                .collect(Collectors.toList());

        if (inscritas.isEmpty()) {
            return Resolucion.error("studentCode/evaluationCode",
                    "El estudiante " + studentCode + " no está inscrito en la sección de la evaluación "
                            + evaluationCode + ".");
        }
        if (inscritas.size() > 1) {
            return Resolucion.error("evaluationCode",
                    "El código de evaluación \"" + evaluationCode + "\" es ambiguo para el estudiante "
                            + studentCode + ".");
        }
        return Resolucion.ok(inscritas.get(0));
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

        List<RegistroImportacion> validos = registroImportacionRepository
                .findByImportacionIdAndEstadoValidacion(importId, EstadoValidacion.VALIDO, Pageable.unpaged())
                .getContent();

        int enviados = 0;
        int registrados = 0;

        if (!validos.isEmpty()) {
            List<GradeRequest> gradeRequests = validos.stream()
                    .map(this::toGradeRequest)
                    .collect(Collectors.toList());

            BatchGradeResult resultado = gradeService.registerBatch(gradeRequests);
            enviados = resultado.getTotalRequested();
            registrados = resultado.getTotalRegistered();

            String mensaje = "enviados=" + enviados + " registrados=" + registrados
                    + " rechazados=" + resultado.getTotalRejected();
            if (resultado.getTotalRejected() > 0) {
                StructuredLog.warn(log, EVENT_GRADES_PROCESSED, OP_CONFIRMAR, importId, mensaje);
            } else {
                StructuredLog.info(log, EVENT_GRADES_PROCESSED, OP_CONFIRMAR, importId, mensaje);
            }
        }

        importacion.setEstado(EstadoImportacion.CONFIRMADO);
        importacion.registrarConfirmacion(enviados, registrados);
        importacion = importacionRepository.save(importacion);

        StructuredLog.info(log, ImportLogEvents.IMPORT_CONFIRMED, OP_CONFIRMAR, importacion.getId(),
                "registrosValidos=" + importacion.getRegistrosValidos());

        return new ImportStatusResponse(
                importacion.getId(),
                importacion.getEstado(),
                importacion.getTotalRegistros(),
                importacion.getRegistrosValidos(),
                importacion.getRegistrosInvalidos(),
                importacion.getFechaCarga()
        );
    }

    private GradeRequest toGradeRequest(RegistroImportacion registro) {
        EstudianteValido estudiante = estudianteValidoRepository.findByStudentCode(registro.getStudentCode())
                .orElseThrow(() -> new IllegalStateException(
                        "Estudiante validado pero no encontrado en estudiante_valido: " + registro.getStudentCode()));

        Resolucion resolucion = resolverEvaluacion(
                estudiante.getId(), registro.getStudentCode(), registro.getEvaluationCode());
        if (resolucion.evaluacion() == null) {
            throw new IllegalStateException(
                    "La evaluación del registro ya no se puede resolver: " + resolucion.motivo());
        }

        GradeRequest request = new GradeRequest();
        request.setStudentId(estudiante.getId());
        request.setEvaluationId(resolucion.evaluacion().getId());
        request.setScore(registro.getScore());
        return request;
    }

    public Page<ImportErrorDetail> consultarErrores(Long importId, Pageable pageable) {
        if (!importacionRepository.existsById(importId)) {
            throw new ImportNotFoundException(importId);
        }

        return registroImportacionRepository
                .findByImportacionIdAndEstadoValidacion(importId, EstadoValidacion.INVALIDO, pageable)
                .map(r -> new ImportErrorDetail(r.getNumeroFila(), r.getCampoError(), r.getMotivoError()));
    }

    private record Resolucion(EvaluationRef evaluacion, String campo, String motivo) {
        static Resolucion ok(EvaluationRef evaluacion) {
            return new Resolucion(evaluacion, null, null);
        }

        static Resolucion error(String campo, String motivo) {
            return new Resolucion(null, campo, motivo);
        }
    }
}