# C7 · Logging estructurado del módulo `system`

Rama: `feature/C7-logging`, sobre `feature/C7-week3-base`.

Cubre el requisito 4 de la semana 3: log de inicio de operación, de éxito, de
error y de regla de negocio rechazada.

## Eventos

| Evento | Nivel | Dónde se emite |
|---|---|---|
| `APPLICATION_STARTED` | INFO | `ApplicationStartupLogger` |
| `OPERATION_STARTED` | INFO | `RequestTraceFilter`, en cada petición a `/api/v1/system` |
| `OPERATION_COMPLETED` | INFO / WARN / ERROR según el estado | `RequestTraceFilter` |
| `RESOURCE_CREATED` | INFO | `SystemCheckService.createCheck` |
| `RESOURCE_NOT_FOUND` | WARN | `SystemExceptionHandler` |
| `VALIDATION_REJECTED` | WARN | `SystemExceptionHandler` |
| `BUSINESS_RULE_REJECTED` | WARN | `SystemExceptionHandler`, en el 409 de la transición `DOWN → UP` y en `IllegalArgumentException` |
| `UNEXPECTED_ERROR` | ERROR | `SystemExceptionHandler` |

## Formato

Una línea JSON por evento, con los 11 campos de la sección 10.2: `timestamp`,
`level`, `service`, `module`, `operation`, `method`, `path`, `status`,
`durationMs`, `traceId` y `message`.

```json
{"timestamp":"2026-09-24T03:01:43.079146100Z","level":"WARN","service":"u-insight","module":"system","operation":"BUSINESS_RULE_REJECTED","method":"PATCH","path":"/api/v1/system/checks/5/status","status":409,"durationMs":3,"traceId":"REQ-810EB1A1","message":"Invalid status transition from DOWN to UP: a check in DOWN must go through DEGRADED first"}
```

El `traceId` del cuerpo de error de la sección 10.1 es **el mismo** que el de la
línea de consola, así que la captura de la respuesta y la del log se emparejan.

El filtro solo actúa sobre `/api/v1/system`: no toca los endpoints de las demás
células ni ensucia su salida de consola.

## Cómo reproducir las cuatro capturas

Con la aplicación levantada (`./mvnw -o spring-boot:run`):

| Evento | Petición |
|---|---|
| `APPLICATION_STARTED` | sale solo al arrancar |
| `RESOURCE_CREATED` | `POST /api/v1/system/checks` con un cuerpo válido → 201 |
| `RESOURCE_NOT_FOUND` | `GET /api/v1/system/checks/9999` → 404 |
| `BUSINESS_RULE_REJECTED` | `PATCH /api/v1/system/checks/{id}/status` con `{"status":"UP"}` sobre una comprobación en `DOWN` → 409 |

Los datos semilla de `SystemCheckDataInitializer` dejan el componente `alerts`
en `DOWN` al arrancar, que sirve para el 409 sin preparar nada.

## Verificado el 23 de septiembre de 2026

- `./mvnw -o clean test` → **BUILD SUCCESS**, 14 pruebas.
- Aplicación levantada: los ocho eventos aparecen en consola con su `traceId`, y
  el del cuerpo del 409 coincide con el de la línea de log.
