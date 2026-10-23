# U-Insight — Frontend (Célula C6)

Interfaz web única de U-Insight. HTML, CSS y JavaScript puro — sin frameworks ni
bundlers — que consume las APIs REST expuestas por el backend (Spring Boot) de
las demás células del proyecto.

## Cómo ejecutarlo en desarrollo

El frontend se sirve como archivos estáticos; no requiere build. Dos formas
rápidas de levantarlo localmente:

```bash
npx http-server -p 5500 -c-1
# o, si tienes Python instalado:
python -m http.server 5500
```

`API_BASE` es una ruta relativa (`/api/v1`) en todos los controladores —
igual que en producción (ver sección de Docker/Nginx abajo) — así que en
desarrollo local necesitas que algo resuelva `/api/v1` hacia el backend.
La forma más simple es usar el flag de proxy de `http-server`, que
redirige cualquier ruta que no sea un archivo estático hacia el backend:

```bash
npx http-server -p 5500 -c-1 -P http://localhost:8080
```

Con el backend corriendo en `http://localhost:8080` (con CORS habilitado
para el origen local, o sin problema de CORS ya que ahora el navegador le
pega al mismo origen `localhost:5500` y es `http-server` quien reenvía la
petición al backend), abre `http://localhost:5500/index.html`.

## Pruebas

El proyecto usa [Vitest](https://vitest.dev/) para probar la lógica pura
(sin DOM) extraída a `src/js/utils/` y `src/js/components/`.

```bash
npm install
npm test        # una corrida
npm run test:watch
```

34 pruebas actuales, 0 fallos.

## Estructura

```
frontend/
├── index.html                   Inicio
├── exploracion.html             Exploración académica
├── detalle-seccion.html         Detalle de sección
├── evolucion.html                Evolución
├── alertas.html                  Alertas
├── intervenciones.html           Intervenciones
├── src/
│   ├── js/
│   │   ├── components/           Componentes reutilizables entre vistas
│   │   │   ├── tarjeta.js, tabla.js, grafico.js
│   │   ├── pages/                 Un controlador por vista (fetch + render)
│   │   ├── utils/                  Funciones puras, sin DOM (probadas con Vitest)
│   │   │   ├── formatters.js, validaciones.js
│   │   └── data/                   JSON de respaldo (ver "Estado por vista")
│   ├── css/                        Estilos específicos de una vista
│   └── styles/                     Estilos compartidos (global, layout, componentes)
├── nginx.conf                      Config de producción (proxy hacia el backend)
└── package.json                    Solo para correr Vitest en desarrollo
```

Cada vista sigue el mismo patrón: `<vista>.html` carga los componentes
compartidos, luego `utils/*.js` (si aplica) y por último
`src/js/pages/<vista>.js`, que al evento `DOMContentLoaded` hace `fetch` al
backend y renderiza el resultado con los componentes compartidos.

## Estado por vista (datos reales vs. simulados)

Las 6 vistas consumen datos reales. Ninguna depende ya de `src/data/*.mock.json`.

| Vista | Endpoint real |
|---|---|
| Inicio | `GET /api/v1/reports/overview` |
| Exploración | `GET /api/v1/courses`, `/academic-periods`, `/sections`, `/teachers` |
| Detalle de sección | `GET /api/v1/analytics/sections/{id}/summary`, `GET /api/v1/reports/alerts` |
| Evolución | `GET /api/v1/analytics/sections/{id}/trends` |
| Alertas | `GET /api/v1/reports/alerts` |
| Intervenciones | `GET/POST /api/v1/alerts`, `/alerts/{id}/interventions`, `/interventions/{id}/follow-ups` |

`src/data/` conserva algunos `.mock.json` de vistas que no necesitaron
cambios (detalle-seccion, evolucion, exploracion, inicio quedaron con un
archivo de respaldo de cuando se armaron las vistas por primera vez, ya
sin uso real); los dos que sí se leían en producción
(`intervenciones.mock.json`, `alertas.mock.json`) se eliminaron al
conectar Intervenciones a la API real.

## Variables de entorno / configuración

En desarrollo, `API_BASE = "/api/v1"` (ruta relativa) en cada archivo de
`src/js/pages/`; no requiere configuración porque el navegador resuelve
la ruta contra el mismo origen que sirve el frontend (ver proxy de
`http-server` arriba, o el de Nginx en producción).

### Docker / producción

`frontend/nginx.conf` sirve los archivos estáticos y hace proxy de
`/api/` hacia el backend. La URL del backend es la variable de entorno
`BACKEND_URL` (por defecto `http://backend:8080`, el nombre del servicio
en `docker-compose.yml`), sustituida en el arranque del contenedor por
el mecanismo de templates de la imagen oficial de `nginx` — ver
`Dockerfile` (`NGINX_ENVSUBST_FILTER` limita la sustitución solo a esa
variable, para no romper las variables nativas de nginx como `$host`).

Desde la raíz del repo:

```bash
docker compose up --build
```

Levanta `backend` (puerto 8080) y `frontend` (puerto 80, con el proxy ya
configurado). Para apuntar el frontend a un backend en otro host:

```bash
FRONTEND_BACKEND_URL=http://mi-backend:9090 docker compose up --build
```
