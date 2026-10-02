# Modulo de Docentes - API v1

Base: `/api/v1/teachers`

| Metodo | Ruta | Descripcion | Respuestas |
|---|---|---|---|
| POST | `/api/v1/teachers` | Crea un docente | 201, 400, 409 |
| GET | `/api/v1/teachers` | Lista todos los docentes | 200 |
| GET | `/api/v1/teachers/{id}` | Consulta un docente | 200, 404 |
| PUT | `/api/v1/teachers/{id}` | Actualiza los datos del docente | 200, 400, 404, 409 |
| PATCH | `/api/v1/teachers/{id}/status` | Cambia el estado (ACTIVE / INACTIVE) | 200, 400, 404 |
| GET | `/api/v1/teachers/{id}/sections` | Lista las secciones asignadas al docente | 200, 404 |
| PUT | `/api/v1/teachers/{id}/sections/{sectionId}` | Asigna el docente a una seccion existente | 200, 404, 409 |
| DELETE | `/api/v1/teachers/{id}` | Elimina al docente solo si no tiene historial | 204, 404, 409 |

Las secciones se leen de la tabla `section`, que pertenece a otro modulo. Este
modulo no las crea: solo consulta la carga academica del docente y valida las
reglas de asignacion y de baja.

## Ejemplos

### PUT /api/v1/teachers/{id}

```json
{
  "teacherCode": "DOC-001",
  "teacherName": "Ana Maria Lopez",
  "email": "ana.lopez@uinsight.edu.gt",
  "status": "ACTIVE"
}
```

`status` es opcional: si no se envia, el docente conserva el estado que tenia.

### PATCH /api/v1/teachers/{id}/status

```json
{ "status": "INACTIVE" }
```

### GET /api/v1/teachers/{id}/sections

```json
[
  {
    "id": 1,
    "sectionCode": "SEC-040",
    "status": "ACTIVE",
    "periodId": 1,
    "courseId": 1,
    "teacherId": 1
  }
]
```

### PUT /api/v1/teachers/{id}/sections/{sectionId}

No lleva cuerpo. Devuelve la seccion ya con el docente asignado, o 409 si el
docente esta INACTIVE.

La documentacion interactiva del API (Swagger UI) queda disponible en
`http://localhost:8080/swagger-ui.html` y el contrato OpenAPI en `/v3/api-docs`.

## Reglas de negocio

| Regla | Donde se aplica | Resultado si se incumple |
|---|---|---|
| Codigo unico de docente | `TeacherService.create` / `TeacherService.update` (comparacion sin distinguir mayusculas) y restriccion `unique` en la tabla `teachers` | 409 Conflict |
| No asignar docentes inactivos a nuevas secciones | `TeacherService.assignToSection` | 409 Conflict |
| No eliminar fisicamente docentes con historial | `TeacherService.delete` (si ya tiene secciones, la baja debe ser logica con PATCH `/status`) | 409 Conflict |
| El correo debe ser valido si se utiliza | `@Email` en `TeacherRequest`; el campo es opcional y una cadena vacia se guarda como `null` | 400 Bad Request |

## Formato de error

```json
{
  "timestamp": "2026-09-23T23:10:00",
  "status": 409,
  "error": "Conflict",
  "message": "Ya existe un docente con el codigo DOC-001",
  "fields": null
}
```

En los errores de validacion (400) el campo `fields` incluye el detalle por atributo.
Lo construye `gt.edu.uinsight.common.exception.ApiExceptionHandler`, que convive con el
manejador del modulo de analitica individual.

## Pruebas

`backend/src/test/java/gt/edu/uinsight/teacher/TeacherEndpointsTest.java` cubre los endpoints
y las cuatro reglas de negocio. Corren contra H2 en memoria, configurado en
`backend/src/test/resources/application.properties`, para no depender de una instancia
MySQL local.

```bash
cd backend
./mvnw test
```
