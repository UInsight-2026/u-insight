# U-Insight

Plataforma de analítica académica y alertas tempranas. Toma las notas de cada sección, calcula indicadores estadísticos (media, mediana, dispersión, tendencia) y detecta automáticamente qué secciones y cuántos estudiantes están en riesgo de reprobar, para que docentes y coordinadores puedan intervenir a tiempo.

Proyecto integrador del curso **Programación II**, Universidad Mariano Gálvez de Guatemala, 2026.
Docente: Ing. Cristopher Muñoz.

## Funcionalidades

- **Gestión académica:** períodos, cursos, docentes, estudiantes, secciones, inscripciones, evaluaciones y calificaciones.
- **Analítica por sección:** tendencia central, posición, dispersión, tendencia y resumen consolidado.
- **Motor de riesgo:** evalúa reglas configurables y clasifica cada sección en riesgo bajo, medio o alto.
- **Alertas:** se generan automáticamente a partir de la evaluación de riesgo.
- **Intervenciones y seguimiento:** registro de tutorías, reuniones y otras acciones sobre una alerta.
- **Reportes:** resumen general, reportes por curso y por sección.
- **Frontend web:** Inicio, Exploración, Detalle de sección, Evolución, Alertas e Intervenciones.

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 25, Spring Boot 4.1.1, Spring Data JPA (Hibernate) |
| Base de datos | MySQL 8.4 |
| Documentación de API | Springdoc OpenAPI (Swagger UI) |
| Pruebas backend | JUnit 5, Mockito, MockMvc |
| Frontend | HTML, CSS y JavaScript sin frameworks, servido con Nginx |
| Pruebas frontend | Vitest |
| Despliegue | Docker y Docker Compose |

## Estructura del repositorio

```
u-insight/
├── backend/                 API REST con Spring Boot
│   └── src/main/java/gt/edu/uinsight/
│       ├── academicperiod/  Períodos académicos
│       ├── course/          Cursos
│       ├── teacher/         Docentes
│       ├── student/         Estudiantes
│       ├── section/         Secciones
│       ├── enrollment/      Inscripciones
│       ├── evaluation/      Evaluaciones
│       ├── grade/           Calificaciones
│       ├── imports/         Importación de datos
│       ├── analytics/       Indicadores estadísticos por sección y estudiante
│       ├── indicatorconfiguration/  Configuración de indicadores
│       ├── alertrule/       Reglas de alerta
│       ├── alert/           Motor de riesgo y alertas
│       ├── intervention/    Intervenciones
│       ├── followup/        Seguimiento de intervenciones
│       ├── report/          Reportes
│       └── system/          Estado y disponibilidad del sistema
├── frontend/                Interfaz web (ver frontend/README.md)
├── database/scripts/        Scripts SQL de creación y datos de prueba
├── docs/                    Documentación de APIs e integración entre módulos
├── postman/                 Colecciones de Postman
├── docker-compose.yml       Backend + frontend
└── docker-compose.dev.yml   MySQL local para desarrollo
```

## Ejecución local

### Requisitos

- Docker y Docker Compose
- Java 25 (solo si se ejecuta el backend fuera de Docker)
- Node.js (solo para correr las pruebas del frontend)

### 1. Variables de entorno

Crear un archivo `.env` en la raíz del repositorio. **No se sube al repositorio** (está en `.gitignore`).

```env
DB_NAME=uinsight
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
DB_ROOT_PASSWORD=tu_contraseña_root
DB_PORT=3307
```

Para el despliegue con `docker-compose.yml` también se usan `DB_HOST_DOCKER` y `DB_PORT_DOCKER`, que indican dónde está la base de datos vista desde el contenedor del backend.

### 2. Base de datos de desarrollo

```bash
docker compose -f docker-compose.dev.yml up -d
```

Levanta MySQL 8.4 en el puerto `3307`. Las tablas las crea Hibernate al iniciar el backend.

### 3. Backend y frontend

```bash
docker compose up -d --build
```

| Servicio | URL |
|---|---|
| Frontend | http://localhost |
| API | http://localhost/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

El frontend usa Nginx como proxy: las rutas `/api/` se reenvían al backend, por lo que el navegador solo necesita el puerto 80.

Para ejecutar solo el backend sin Docker:

```bash
cd backend
./mvnw spring-boot:run
```

## Pruebas

```bash
# Backend
cd backend
./mvnw test

# Frontend
cd frontend
npm install
npm test
```

## Documentación de la API

Con el backend en ejecución, la documentación interactiva de todos los endpoints está en **Swagger UI** (`/swagger-ui.html`) y la especificación OpenAPI en `/v3/api-docs`.

Documentación adicional por módulo en la carpeta [`docs/`](docs/).

## Flujo de trabajo

- `develop` es la rama de integración. Todo cambio entra por pull request.
- `main` contiene la versión estable, sincronizada desde `develop`.
- No se suben credenciales, archivos `.env` ni datos reales al repositorio.
