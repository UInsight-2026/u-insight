# Módulo A1: períodos académicos y cursos

## Descripción

La célula A1 administra el contexto académico sobre el que funciona todo U-Insight:
qué período se analiza, qué curso corresponde y si ambos están activos. Es la
primera célula del flujo `PERÍODO → CURSO → DOCENTE → … → SEGUIMIENTO` y no
consume información de ninguna otra: es la fuente.

Es dueña exclusiva de dos tablas, `academic_period` y `course`, y las expone a las
demás células únicamente por su API REST.

**Responsable:** Irvin José González Mateo, carnet 0900-24-24648 (coordinador y
único integrante de la célula A1).

## Endpoints (12)

| # | Método | Ruta | Respuesta |
|---|---|---|---|
| 1 | POST | `/api/v1/academic-periods` | 201 |
| 2 | GET | `/api/v1/academic-periods?status=&page=&size=&sort=` | 200 |
| 3 | GET | `/api/v1/academic-periods/{id}` | 200 |
| 4 | GET | `/api/v1/academic-periods/active` | 200 |
| 5 | PUT | `/api/v1/academic-periods/{id}` | 200 |
| 6 | PATCH | `/api/v1/academic-periods/{id}/status` | 200 |
| 7 | POST | `/api/v1/courses` | 201 |
| 8 | GET | `/api/v1/courses?status=&page=&size=&sort=` | 200 |
| 9 | GET | `/api/v1/courses/{id}` | 200 |
| 10 | GET | `/api/v1/courses/code/{code}` | 200 |
| 11 | PUT | `/api/v1/courses/{id}` | 200 |
| 12 | PATCH | `/api/v1/courses/{id}/status` | 200 |

- **Paginación:** `page` (por defecto 0), `size` (por defecto 20) y `sort` (por
  ejemplo `sort=year,desc`).
- **Filtro `?status=`:** un valor no reconocido responde 400; nunca se ignora.
- **Listados:** devuelven `{content, page, size, totalElements, totalPages}`.
- **Borrado:** no hay `DELETE` (RN-05). La baja es por cambio de estado.

El contrato detallado para las células que consumen esta API está en
[`a1-openapi.md`](a1-openapi.md).

## Reglas de negocio

| Regla | Descripción | HTTP |
|---|---|---|
| RN-01 | El código de curso es único, sin distinguir mayúsculas | 409 |
| RN-02 | `startDate` debe ser estrictamente anterior a `endDate` | 400 |
| RN-03 | No puede haber más de un período en estado ACTIVE | 409 |
| RN-04 | Un período CLOSED no acepta modificaciones | 409 |
| RN-05 | No hay borrado físico: la baja es por cambio de estado | 405 si se intenta DELETE |
| RN-06 | Solo `PLANNED → ACTIVE` y `ACTIVE → CLOSED`; no se reabre ni se saltan estados | 409 |
| RN-07 | El nombre del período es único dentro del mismo año | 409 |
| RN-08 | Si se envían créditos, deben ser mayores que cero | 400 |
| RN-09 | Un curso INACTIVE no debe ofrecerse para nuevas secciones | La API expone `status`; la célula A4 decide en su código |

- **Estados iniciales:** los períodos nacen en `PLANNED` y los cursos en `ACTIVE`.
- **RN-06:** está implementada en `PeriodStatus.canTransitionTo`.

## Estructura

```
backend/src/main/java/gt/edu/uinsight/
├── academicperiod/
│   ├── controller/   AcademicPeriodController
│   ├── service/      AcademicPeriodService, AcademicPeriodServiceImpl
│   ├── repository/   AcademicPeriodRepository
│   ├── entity/       AcademicPeriod, PeriodStatus
│   ├── dto/          request/ (Create, Update), response/
│   ├── mapper/       AcademicPeriodMapper
│   └── support/      Piezas comunes de A1 (periodos y cursos):
│       ├── exception/  AcademicExceptionHandler, AcademicErrorResponse, excepciones
│       ├── logging/    AcademicEventLogger, AcademicRequestInterceptor, AcademicWebConfig
│       ├── dto/        ChangeStatusRequest, PageResponse
│       └── openapi/    AcademicOpenApiGroup
└── course/           controller, service, repository, entity, dto, mapper
database/scripts/
├── 02_create_academic_period.sql
├── 03_create_course.sql
├── seed_a1_academic_periods.sql
└── seed_a1_courses.sql
```

## Errores

`AcademicExceptionHandler` está acotado a los dos controladores de A1 y tiene la
mayor precedencia, así que no interfiere con los manejadores de otras células.
Todas las respuestas de error siguen el formato común:

```json
{
  "timestamp": "2026-09-30T06:25:26",
  "status": 409,
  "error": "CONFLICT",
  "message": "Ya existe un curso con el codigo 'prog-ii'",
  "details": ["rule: RN-01"],
  "traceId": "REQ-9CC37C87"
}
```

| `error` | HTTP | Cuándo |
|---|---|---|
| `VALIDATION_ERROR` | 400 | Validación de DTO, RN-02, RN-08, estado no reconocido, JSON o id inválido |
| `NOT_FOUND` | 404 | El período o el curso no existe |
| `CONFLICT` | 409 | RN-01, RN-03, RN-04, RN-06 y RN-07 |
| `INTERNAL_ERROR` | 500 | Error inesperado o de base de datos |

## Logging

Cada evento se escribe como una línea JSON con los campos `timestamp`, `level`,
`service`, `module` (siempre `academic`), `operation`, `method`, `path`,
`status`, `durationMs`, `traceId` y `message`. El `traceId` es el mismo que viaja
en el cuerpo de error y en la cabecera `X-Trace-Id`.

| Nivel | Eventos |
|---|---|
| INFO | `OPERATION_STARTED`, `OPERATION_COMPLETED`, `ACADEMIC_PERIOD_CREATED`, `ACADEMIC_PERIOD_UPDATED`, `ACADEMIC_PERIOD_ACTIVATED`, `ACADEMIC_PERIOD_CLOSED`, `COURSE_CREATED`, `COURSE_UPDATED`, `COURSE_STATUS_CHANGED` |
| WARN | `BUSINESS_RULE_REJECTED` (con el id de la regla), `RESOURCE_NOT_FOUND`, `DUPLICATE_RESOURCE`, `VALIDATION_ERROR` |
| ERROR | `DATABASE_ERROR`, `UNEXPECTED_ERROR` |

- `APPLICATION_STARTED` lo registra el módulo `system` (C7).
- **Datos sensibles:** no se registran credenciales ni datos personales.

## Variables de entorno

Se definen en `.env.example`, en la raíz del repositorio:

| Variable | Descripción |
|---|---|
| `DB_HOST` | Host del servidor MySQL |
| `DB_PORT` | Puerto del servidor MySQL |
| `DB_NAME` | Nombre de la base de datos |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |

## Ejecución local

```bash
# 1. Levantar MySQL (desde la raíz del repositorio)
cp .env.example .env
docker compose -f docker-compose.dev.yml --env-file .env up -d

# 2. Cargar los scripts de A1 (solo si la base ya existía antes de agregarlos)
for f in 02_create_academic_period 03_create_course seed_a1_academic_periods seed_a1_courses; do
  docker exec -i uinsight-mysql mysql -uuinsight -puinsight uinsight < database/scripts/$f.sql
done

# 3. Arrancar el backend (JDK 17)
cd backend && ./mvnw spring-boot:run
```

- **Swagger UI:** http://localhost:8080/swagger-ui.html, en el grupo **A1 - academic-periods-courses**.
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs.

## Pruebas

```bash
cd backend
./mvnw test -Dtest='gt.edu.uinsight.academicperiod.**,gt.edu.uinsight.course.**'
```

Son 67 pruebas: servicios con Mockito, transiciones de los enums y controladores
con `@WebMvcTest`. No usan base de datos ni dependen de otras células.
