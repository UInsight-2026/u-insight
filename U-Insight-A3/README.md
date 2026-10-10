# U-Insight — Implementación A3

Este paquete conserva la estructura del repositorio [UInsight-2026/u-insight](https://github.com/UInsight-2026/u-insight) e incorpora la API de gestión de estudiantes de A3 en el backend Java/Spring Boot/JPA existente.

## Endpoints A3

- `POST /api/v1/students`
- `GET /api/v1/students`
- `GET /api/v1/students/{id}`
- `GET /api/v1/students/code/{code}`
- `PATCH /api/v1/students/{id}/status`
- `GET /api/v1/students/{id}/academic-history`

El detalle del contrato, ejemplos `curl` y las reglas se encuentran en [`docs/A3-gestion-estudiantes.md`](docs/A3-gestion-estudiantes.md).

## Reglas y datos

- El `studentCode` es único, se normaliza a mayúsculas y rechaza duplicados con `409`.
- Los DTOs validan los datos de entrada; los errores usan un formato JSON con `traceId`.
- No se expone una operación para eliminar estudiantes. La FK de calificaciones usa `ON DELETE RESTRICT`.
- El historial lee de la tabla compartida `grades`; si no hay notas, devuelve una lista vacía.
- `StudentService.requireActiveForEnrollment(id)` permite que A4 rechace inscripciones para estudiantes inactivos.
- Usar únicamente identidades ficticias o anonimizadas en datos de prueba.

La migración A3/A6 está en `database/scripts/11_create_students_and_grade_history.sql`. Si A6 ya definió `grades`, conservar una sola definición y mantener sus columnas compatibles con el historial.

## Ejecutar y probar

Se requiere JDK 25. Desde `backend/`:

```bash
./mvnw test
./mvnw spring-boot:run
```

La configuración local usa H2 en memoria. Para MySQL, definir `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`; el conector MySQL ya está incluido. La documentación Swagger se sirve en `/swagger-ui/index.html`.