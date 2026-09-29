# Módulo A5 — Gestión de evaluaciones

**Célula:** A5 · **Sección:** A — Datos académicos · **Curso:** Programación II (UMG) · Ciclo 2026
**Paquete:** `gt.edu.uinsight.evaluation` · **Tabla:** `evaluation`

## Objetivo

Definir, administrar y controlar el ciclo de vida de las evaluaciones (exámenes, quices, proyectos,
laboratorios, tareas) de cada sección, garantizando que ponderación, nota máxima, fecha y estado sean
correctos, para que A6 registre notas válidas y la Sección B calcule indicadores confiables.

## Arquitectura

```
evaluation/
├── api/            EvaluationQueryService, EvaluationSummary   ← contrato público para otras células
├── controller/     EvaluationController                        ← HTTP
├── domain/         EvaluationType, EvaluationStatus (RN6)
├── dto/request     CreateEvaluationRequest, UpdateEvaluationRequest, ChangeEvaluationStatusRequest
├── dto/response    EvaluationResponse
├── entity/         Evaluation                                  ← tabla evaluation (contrato SQL 8.3)
├── exception/      excepciones de negocio + EvaluationExceptionHandler
├── mapper/         EvaluationMapper
├── repository/     EvaluationRepository
└── service/        EvaluationServiceImpl + puertos de integración
                    SectionValidationPort  → A4 (SectionValidationJdbcAdapter)
                    GradeEvaluationSyncPort → A6 (GradeEvaluationRefJdbcAdapter)
```

## Endpoints

| Método | URL | Descripción | Éxito | Errores |
|---|---|---|---|---|
| POST | `/api/v1/evaluations` | Crear evaluación (HU1) | 201 | 400, 404, 422 |
| GET | `/api/v1/evaluations` | Listar todas | 200 | — |
| GET | `/api/v1/evaluations/{id}` | Consultar por id | 200 | 400, 404 |
| GET | `/api/v1/sections/{id}/evaluations` | Listar por sección (HU2) | 200 (lista vacía si no hay) | 404 |
| PUT | `/api/v1/evaluations/{id}` | Actualizar (HU3) | 200 | 400, 404, 409, 422 |
| PATCH | `/api/v1/evaluations/{id}/status` | Cambiar estado (HU4) | 200 | 400, 404, 409, 422 |

Documentación interactiva: `/swagger-ui.html` → grupo **Evaluations**.

Ejemplo de creación:

```json
POST /api/v1/evaluations
{ "sectionId": 10, "name": "Examen Parcial 1", "type": "EXAM",
  "evaluationDate": "2026-10-15", "maximumScore": 100, "weight": 30 }
```

## Reglas de negocio

| Regla | Descripción | Dónde |
|---|---|---|
| RN1 | La sección debe existir y estar ACTIVE (al crear, actualizar y activar) | `EvaluationServiceImpl.assertSectionActive` |
| RN2 | `maximumScore` > 0 (hasta 999.99) | Bean Validation en los DTOs |
| RN3 | `weight` > 0 y ≤ 100 | Bean Validation en los DTOs |
| RN4 | La suma de `weight` de la sección (sin CANCELLED) no supera 100 | `assertWeightWithinLimit` |
| RN5 | Una evaluación CLOSED no se modifica; por HU3 tampoco una CANCELLED | `EvaluationStatus.isEditable` |
| RN6 | DRAFT → ACTIVE → CLOSED, o DRAFT/ACTIVE → CANCELLED; sin transiciones inversas | `EvaluationStatus.allowedNext` |

Toda evaluación nace en **DRAFT**. `type` ∈ EXAM, QUIZ, PROJECT, LAB, ASSIGNMENT, OTHER (se acepta en
minúsculas y se guarda en mayúsculas).

## Errores

Formato estándar U-Insight:

```json
{ "timestamp": "2026-10-01T10:00:00", "status": 422, "error": "WEIGHT_LIMIT_EXCEEDED",
  "message": "La suma de ponderaciones de la sección 10 sería 105% ...", "details": [], "traceId": "..." }
```

| HTTP | `error` | Cuándo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Campos inválidos (detalle por campo en `details`), estado inexistente, id no numérico |
| 400 | `MALFORMED_REQUEST` | JSON mal formado o fecha que no es `yyyy-MM-dd` |
| 404 | `EVALUATION_NOT_FOUND` | No existe la evaluación |
| 404 | `SECTION_NOT_FOUND` | No existe la sección |
| 409 | `EVALUATION_NOT_EDITABLE` | PUT sobre CLOSED o CANCELLED |
| 409 | `INVALID_STATUS_TRANSITION` | Transición no permitida por RN6 |
| 422 | `SECTION_NOT_ACTIVE` | La sección existe pero no está ACTIVE |
| 422 | `WEIGHT_LIMIT_EXCEEDED` | Se superaría el 100% de ponderación |
| 500 | `INTERNAL_ERROR` | Error inesperado (se registra con traceId) |

El `traceId` se toma del header `X-Trace-Id` o `X-Correlation-ID`; si no viene, se genera.

## Integraciones

**Entrada — A4 (Secciones).** `SectionValidationPort` valida existencia y estado de la sección.
Implementación actual: `SectionValidationJdbcAdapter` (consulta `section.id` y `section.status`).
Cuando A4 publique su API se reemplaza el adapter sin tocar el servicio.

**Salida — A6 (Calificaciones).**
1. *Sincronización automática:* cuando una evaluación pasa a ACTIVE, o se actualiza estando ACTIVE,
   `GradeEvaluationRefJdbcAdapter` crea o actualiza su fila en `grade_evaluation_ref`
   (id, section_id, name, maximum_score). Así A6 valida las notas con datos reales de A5 sin carga manual.
   Si la tabla no existe, se registra `INTEGRATION_ERROR` y A5 sigue funcionando.
2. *Consulta directa (recomendada para A6):* inyectar `EvaluationQueryService`:
   ```java
   evaluationQueryService.findSummary(evaluationId)   // Optional<EvaluationSummary>
   evaluationQueryService.isGradable(evaluationId)    // true solo si está ACTIVE
   ```
3. *HTTP:* `GET /api/v1/evaluations/{id}` o `GET /api/v1/sections/{id}/evaluations`.

**Sección B** consume las evaluaciones de forma indirecta, a través de las notas de A6.

## Logs

Formato clave=valor con SLF4J, sin datos sensibles.

| Nivel | Eventos |
|---|---|
| INFO | `EVALUATION_REQUEST`, `EVALUATION_CREATED`, `EVALUATION_UPDATED`, `EVALUATION_STATUS_CHANGED`, `EVALUATION_SYNCED_TO_GRADES` |
| WARN | `EVALUATION_NOT_FOUND`, `SECTION_NOT_FOUND`, `SECTION_NOT_ACTIVE`, `WEIGHT_LIMIT_EXCEEDED`, `EVALUATION_NOT_EDITABLE`, `INVALID_STATUS_TRANSITION`, `VALIDATION_ERROR`, `INTEGRATION_ERROR` |
| ERROR | `INTERNAL_ERROR` |

## Pruebas

```powershell
cd backend
.\mvnw.cmd test "-Dtest=EvaluationServiceImplTest,EvaluationControllerTest"
```

- `EvaluationServiceImplTest` (18 pruebas unitarias, Mockito): RN1–RN6, exclusión de CANCELLED,
  exclusión de la propia ponderación al actualizar, integración con A6, consultas.
- `EvaluationControllerTest` (11 pruebas MockMvc): códigos 201/200/400/404/409/422 y formato de error.

### Prueba manual en Swagger

1. Ejecutar `database/scripts/local-dev/stub_section_for_a5_testing.sql`
   (sección **10** ACTIVE y **11** CLOSED) mientras A4 no publique su tabla.
2. Levantar la app y abrir `/swagger-ui.html`.
3. Flujo sugerido: crear (201, DRAFT) → activar (200) → actualizar (200) → cerrar (200) →
   intentar actualizar (409) → intentar reactivar (409); crear con `sectionId` 11 (422),
   con `sectionId` 999 (404) y con `weight` 80 cuando ya hay 30 (422).

## Limitaciones y dependencias

- La validación de sección depende de la tabla `section` de A4; mientras no exista se usa el stub local.
- `grade_evaluation_ref` no guarda el estado: si una evaluación pasa a CLOSED o CANCELLED, A6 debe
  consultar `EvaluationQueryService.isGradable` para bloquear nuevas notas.
- Las columnas `type` y `status` son VARCHAR según el contrato SQL (8.3); los valores válidos se
  controlan con los enums de `domain/`.
