# A3 — Gestión de estudiantes

Implementación para el backend del repositorio U-Insight (`backend/`), con Java, Spring Boot, Spring Data JPA y MySQL.

## Rutas

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/students` | Registra un estudiante anonimizado |
| `GET` | `/api/v1/students` | Lista estudiantes |
| `GET` | `/api/v1/students/{id}` | Busca por ID |
| `GET` | `/api/v1/students/code/{code}` | Busca por código |
| `PATCH` | `/api/v1/students/{id}/status` | Activa o desactiva un estudiante |
| `GET` | `/api/v1/students/{id}/academic-history` | Consulta calificaciones registradas |

## Ejemplos

Registrar:

```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H 'Content-Type: application/json' \
  -d '{"studentCode":"EST-0001","studentName":"Estudiante 0001","email":null}'
```

Cambiar estado:

```bash
curl -X PATCH http://localhost:8080/api/v1/students/1/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"INACTIVE"}'
```

Consultar historial:

```bash
curl http://localhost:8080/api/v1/students/1/academic-history
```

## Reglas implementadas

- El código es obligatorio, se normaliza a mayúsculas y es único; los duplicados responden `409`.
- Se validan código, nombre y correo opcional; los datos inválidos responden `400`.
- Las búsquedas inexistentes responden `404`.
- No existe endpoint de eliminación; la FK de `grades.student_id` impide eliminar estudiantes con calificaciones.
- El historial usa la tabla compartida `grades` y devuelve una lista vacía cuando el estudiante no tiene calificaciones.
- `StudentService.requireActiveForEnrollment(id)` permite a A4 rechazar inscripciones de estudiantes inactivos.

## Base de datos y ejecución

1. Ejecutar `database/scripts/11_create_students_and_grade_history.sql` sobre MySQL.
2. Configurar `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`.
3. Desde `backend/`, ejecutar `./mvnw test` y `./mvnw spring-boot:run`.
4. Swagger UI queda disponible en `/swagger-ui/index.html`.

El script crea `students` y el esquema mínimo compartido de `grades` descrito por A6. Si A6 ya creó esa tabla, conservar su script y alinear sus columnas con `evaluation_id`, `student_id`, `score`, `registered_at` y `status`; no ejecutar dos definiciones distintas.