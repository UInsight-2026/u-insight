# C1 - Configuracion de indicadores

Modulo encargado de administrar parametros configurables utilizados por otros componentes de U-Insight.

## Endpoints

| Metodo | URL | Descripcion | Exito |
|---|---|---|---|
| POST | `/api/v1/indicator-configurations` | Crea una configuracion | 201 |
| GET | `/api/v1/indicator-configurations` | Lista configuraciones | 200 |
| GET | `/api/v1/indicator-configurations/{key}` | Consulta por clave | 200 |
| PUT | `/api/v1/indicator-configurations/{key}` | Actualiza valor y/o descripcion | 200 |

## Crear configuracion

```json
{
  "key": "HIGH_RISK_PERCENTAGE",
  "value": "50",
  "description": "Porcentaje de estudiantes en riesgo que activa alerta de seccion"
}
```

Respuesta esperada: `201 Created`.

## Consultar por clave

`GET /api/v1/indicator-configurations/HIGH_RISK_PERCENTAGE`

Si la clave no existe se responde `404 Not Found`.

## Actualizar

```json
{
  "value": "55",
  "description": "Actualizado despues de revision"
}
```

Cada actualizacion registra el evento `INDICATOR_CONFIGURATION_CHANGED` con el valor anterior y el nuevo.

## Errores implementados

- `400 Bad Request`: validaciones o JSON invalido.
- `404 Not Found`: clave inexistente.
- `409 Conflict`: clave duplicada.
- `500 Internal Server Error`: error inesperado.

## Swagger

Con la aplicacion levantada, Swagger UI queda disponible en:

`http://localhost:8080/swagger-ui/index.html`

El grupo OpenAPI del modulo se llama `C1 - indicator-configurations`.
