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
| DELETE | `/api/v1/teachers/{id}` | Elimina al docente solo si no tiene historial | 204, 404, 409 |

Apoyo para las asignaciones (modulo de secciones):

| Metodo | Ruta | Descripcion | Respuestas |
|---|---|---|---|
| POST | `/api/v1/sections` | Registra una seccion y le asigna un docente | 201, 400, 404, 409 |
| GET | `/api/v1/sections` | Lista todas las secciones | 200 |

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
    "courseName": "Programacion I",
    "academicTerm": "2026-1",
    "teacherId": 1,
    "teacherName": "Ana Maria Lopez"
  }
]
```

### POST /api/v1/sections

```json
{
  "sectionCode": "SEC-040",
  "courseName": "Programacion I",
  "academicTerm": "2026-1",
  "teacherId": 1
}
```

## Reglas de negocio

| Regla | Donde se aplica | Resultado si se incumple |
|---|---|---|
| Codigo unico de docente | `TeacherService.create` / `TeacherService.update` (comparacion sin distinguir mayusculas) y restriccion `unique` en la tabla `teachers` | 409 Conflict |
| No asignar docentes inactivos a nuevas secciones | `SectionService.create` | 409 Conflict |
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

## Pruebas

`backend/src/test/java/gt/edu/uinsight/teacher/TeacherEndpointsTest.java` cubre los endpoints
nuevos y las cuatro reglas de negocio.

```bash
cd backend
./mvnw test
```
