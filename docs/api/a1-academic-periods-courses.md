# Modulo: Periodos academicos y cursos (Celula A1)

## Descripcion

Modulo responsable de la gestion de periodos academicos y cursos (punto 7.1 de
la guia del proyecto U-Insight). Expone operaciones sobre `AcademicPeriod` y
`Course`, con DTOs por operacion, validaciones declarativas en los DTOs y
reglas de negocio en la capa de servicio.

## Integrantes

| Nombre | Carnet |
|---|---|
| Irvin Jose Gonzalez Mateo | 0900-24-24648 |
| Jeremy Jonathan Cardenas Mauricio | 0900-22-10240 |

## Endpoints

| Metodo | Ruta | Estado |
|---|---|---|
| POST | `/api/v1/academic-periods` | Implementado |
| GET | `/api/v1/academic-periods` | Implementado |
| GET | `/api/v1/academic-periods/{id}` | Implementado |
| POST | `/api/v1/courses` | Implementado |
| GET | `/api/v1/courses` | Implementado |
| GET | `/api/v1/courses/{id}` | Implementado |

El modulo contempla 12 endpoints en total; los 6 restantes quedan pendientes
de diseno con la guia oficial del proyecto y se agregan en una entrega
posterior.

## Reglas de negocio aplicadas

- RN-01: el codigo de un curso es unico sin distinguir mayusculas (409).
- RN-02: la fecha de inicio de un periodo debe ser anterior a la fecha de fin (400).
- RN-07: no se repite el nombre de un periodo dentro del mismo anio (409).
- RN-08: si se envian creditos, deben ser mayores que cero (400).

Los periodos se crean en estado `PLANNED` y los cursos en estado `ACTIVE`.

## Variables de entorno

Definidas en `.env.example` (raiz del repositorio):

| Variable | Descripcion |
|---|---|
| `DB_HOST` | Host del servidor MySQL |
| `DB_PORT` | Puerto del servidor MySQL |
| `DB_NAME` | Nombre de la base de datos |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contrasena de la base de datos |

## Ejecucion local

1. Copiar `.env.example` a `.env` y ajustar los valores si es necesario.
2. Levantar un MySQL 8 local o en contenedor con esas credenciales.
3. Ejecutar los scripts de `database/scripts/` en orden (01 a 05).
4. Exportar las variables de entorno y ejecutar `./mvnw spring-boot:run` desde `backend/`.
5. La documentacion interactiva queda disponible en `/swagger-ui.html`.
