# C7 · Logging estructurado — estado y pendiente

Rama: `feature/C7-logging` (nace de `feature/C7-week3-base`).

## Hecho

| Evento | Nivel | Dónde se emite |
|---|---|---|
| `APPLICATION_STARTED` | INFO | `ApplicationStartupLogger` |
| `OPERATION_STARTED` | INFO | `RequestTraceFilter`, en cada petición a `/api/v1/system` |
| `OPERATION_COMPLETED` | INFO / WARN / ERROR según el status | `RequestTraceFilter` |
| `RESOURCE_NOT_FOUND` | WARN | `SystemExceptionHandler` |
| `VALIDATION_REJECTED` | WARN | `SystemExceptionHandler` |
| `BUSINESS_RULE_REJECTED` | WARN | `SystemExceptionHandler`, vía `IllegalArgumentException` |
| `UNEXPECTED_ERROR` | ERROR | `SystemExceptionHandler` |

Formato: una línea JSON por evento con los 11 campos de la sección 10.2
(`timestamp`, `level`, `service`, `module`, `operation`, `method`, `path`,
`status`, `durationMs`, `traceId`, `message`).

El `traceId` del cuerpo de error de la sección 10.1 es **el mismo** que el de la
línea de consola, así que la captura de la respuesta y la del log se emparejan.

El filtro solo actúa sobre `/api/v1/system`: no toca los endpoints de las demás
células ni ensucia su salida de consola.

## Pendiente (espera al PR de Dev 1)

`SystemCheckService` lo está modificando Dev 1 (filtros, paginación, `PATCH`,
regla DOWN→UP). Para no editar el mismo archivo en paralelo, faltan dos líneas
que se insertan **después** de fusionar su PR:

1. En `createCheck(...)`, tras guardar:

```java
eventLogger.info("RESOURCE_CREATED", 201, "System check created with id " + saved.getId());
```

2. En la regla de negocio DOWN→UP, si Dev 1 usa una excepción propia en lugar de
   `IllegalArgumentException`, añadir su `@ExceptionHandler` en
   `SystemExceptionHandler` con la operación `BUSINESS_RULE_REJECTED` y estado 409.
   Si la lanza como `IllegalArgumentException`, ya queda cubierto sin tocar nada.

Ambas requieren inyectar `SystemEventLogger` en el constructor de
`SystemCheckService`.

## Verificado el 22 de septiembre de 2026

- `./mvnw -o clean test` → **BUILD SUCCESS**, 6 pruebas (4 previas + 2 nuevas).
- Aplicación levantada y ejercitada: `/health` 200, `/checks/9999` 404,
  `POST /checks` con cuerpo vacío 400, `POST /checks` válido 201. Los eventos
  aparecen en consola con su `traceId`.
