# Contrato de la API de A1 para las células dependientes

Este documento es informativo. Indica qué endpoints de A1 puede consumir cada
célula y qué campos son estables. A1 no escribe código del lado de otras
células ni lee sus tablas.

## Garantías de estabilidad

- **Rutas:** las de este documento no cambian de ruta ni de método.
- **Columnas:** en `academic_period` y `course` no se renombran ni cambian de tipo
  `id`, `code`, `name` ni `status`. Pueden agregarse columnas nuevas, pero nunca
  quitarse.
- **Llaves foráneas:** A1 no declara ninguna. Las FK hacia `academic_period(id)` y
  `course(id)` las declara la célula dueña de la tabla que las necesita.
- **Acceso a los datos:** consuman la API por HTTP; no lean las tablas de A1
  directamente.

## Qué consume cada célula

| Célula | Uso | Endpoints recomendados |
|---|---|---|
| **A4** (secciones) | Validar período y curso antes de crear una sección. Rechazar cursos `INACTIVE` (RN-09) y, si aplica, períodos `CLOSED` | `GET /api/v1/academic-periods/{id}`, `GET /api/v1/courses/{id}`, `GET /api/v1/courses/code/{code}` |
| **A5** (evaluaciones) | Hereda el contexto a través de A4; opcionalmente el período vigente | `GET /api/v1/academic-periods/active` |
| **A6** (calificaciones) | Hereda el contexto a través de A4 y A5 | (ninguno directo) |
| **B6** (analítica) | Etiquetar resultados con nombre de período y código o nombre de curso | `GET /api/v1/academic-periods/{id}`, `GET /api/v1/courses/{id}` |
| **C5** (reportes) | Construir filtros por período y curso | `GET /api/v1/academic-periods`, `GET /api/v1/courses` (con `?status=` y paginación) |
| **C6** (frontend) | Poblar selectores de período y curso | `GET /api/v1/academic-periods?status=ACTIVE`, `GET /api/v1/academic-periods/active`, `GET /api/v1/courses?status=ACTIVE&size=100` |

La célula B1 ya consume `GET /api/v1/courses/{id}` y
`GET /api/v1/academic-periods/{id}` para validar existencia; ambos responden 404
cuando el recurso no existe.

## Respuestas

### `AcademicPeriodResponse`

```json
{
  "id": 2,
  "name": "Primer Semestre",
  "year": 2026,
  "startDate": "2026-01-12",
  "endDate": "2026-05-30",
  "status": "ACTIVE",
  "createdAt": "2026-01-05T09:12:00"
}
```

`status` puede ser `PLANNED`, `ACTIVE` o `CLOSED`. Siempre hay como máximo un
período `ACTIVE`.

### `CourseResponse`

```json
{
  "id": 7,
  "code": "PROG-II",
  "name": "Programación II",
  "description": "Curso de POO y APIs REST",
  "credits": 5,
  "status": "ACTIVE",
  "createdAt": "2026-01-05T09:12:00"
}
```

- **`status`:** puede ser `ACTIVE` o `INACTIVE`.
- **`credits` y `description`:** pueden venir en `null`.
- **`code`:** no cambia nunca después de crear el curso.

### Listados paginados

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 5,
  "totalPages": 1
}
```

## Endpoints

| Método | Ruta | Cuerpo | Éxito | Errores |
|---|---|---|---|---|
| POST | `/api/v1/academic-periods` | `{name, year, startDate, endDate}` | 201 | 400 (validación, RN-02), 409 (RN-07) |
| GET | `/api/v1/academic-periods` | `?status=&page=&size=&sort=` | 200 | 400 (estado u orden inválido) |
| GET | `/api/v1/academic-periods/{id}` | | 200 | 400 (id no numérico), 404 |
| GET | `/api/v1/academic-periods/active` | | 200 | 404 (no hay período activo) |
| PUT | `/api/v1/academic-periods/{id}` | `{name, startDate, endDate}` | 200 | 400 (RN-02), 404, 409 (RN-04, RN-07) |
| PATCH | `/api/v1/academic-periods/{id}/status` | `{status}` | 200 | 400 (estado inválido), 404, 409 (RN-03, RN-04, RN-06) |
| POST | `/api/v1/courses` | `{code, name, description?, credits?}` | 201 | 400 (validación, RN-08), 409 (RN-01) |
| GET | `/api/v1/courses` | `?status=&page=&size=&sort=` | 200 | 400 |
| GET | `/api/v1/courses/{id}` | | 200 | 400, 404 |
| GET | `/api/v1/courses/code/{code}` | | 200 | 404 (sin distinguir mayúsculas) |
| PUT | `/api/v1/courses/{id}` | `{name, description?, credits?}` | 200 | 400 (RN-08), 404 |
| PATCH | `/api/v1/courses/{id}/status` | `{status}` | 200 | 400, 404, 409 (mismo estado) |

## Formato de error

```json
{
  "timestamp": "2026-09-30T06:25:26",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "No existe un curso con id 999",
  "details": [],
  "traceId": "REQ-9CC37C87"
}
```

Cuando el error viene de una regla de negocio, `details` incluye `"rule: RN-0X"`.
