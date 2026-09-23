# U-Insight · Célula C7 · Plan de trabajo Semana 3

| | |
|---|---|
| **Sección** | C — Gestión, integración y presentación |
| **Componente** | Calidad, integración y APIs técnicas |
| **Paquete del módulo** | `gt.edu.uinsight.system` |
| **Repositorio** | https://github.com/UInsight-2026/u-insight |
| **Rama de trabajo** | `feature/C7-week3-base` |
| **Entrega** | miércoles 23 de septiembre de 2026, 23:59 |
| **Integrantes** | Coordinador (Marcos Pacheco), Dev 1 (Icjosue), Dev 2 (Ashly Aquino), Dev 3 (Oliver Yecute) |
| **Última sincronización con `develop`** | 22 de septiembre de 2026, commit `aa81c93` |

---

## 1. Propósito de la rama `feature/C7-week3-base`

Es la rama de partida de toda la célula para la semana 3. Contiene tres cosas ya combinadas:

1. **Todo el contenido actual de `develop`** (incluido el módulo `system` de la semana 2, ya fusionado).
2. **El trabajo adelantado de `feature/C7-health-json`**: `/health` en formato JSON y el verificador de base de datos.
3. **Los parches mínimos que hacen que el proyecto compile y arranque**, porque `develop` por sí solo no lo hace. Ver sección 2.

### Por qué no se trabaja directamente sobre `develop`

El motivo cambió respecto a la semana 2. Ya no es que falte el módulo `system` —ese PR ya fue fusionado— sino que **`develop` en su estado actual no compila ni levanta la aplicación**:

- Un error de compilación heredado de la célula B4 impide que el proyecto pase de `mvn compile`.
- Cinco colisiones distintas entre módulos de otras células impiden que el contexto de Spring arranque.
- La configuración de Swagger quedó inconsistente y la UI no se puede abrir.

`feature/C7-week3-base` incorpora la corrección de todo eso. Es la única rama del proyecto en la que, ahora mismo, la aplicación se puede ejecutar y probar.

### Punto de partida común

```bash
git fetch origin
git checkout -b feature/C7-{tarea} origin/feature/C7-week3-base
```

> `{tarea}` corresponde al nombre de rama asignado en la sección 6.

### Reglas de la rama

- No se hacen commits directamente sobre `feature/C7-week3-base`. Es rama de integración.
- Cada integrante trabaja en su `feature/C7-{tarea}` y abre un Pull Request **hacia `feature/C7-week3-base`**.
- El coordinador mantiene la rama sincronizada con `develop`. Cuando `develop` se mueve, lo avisa al grupo y todos ejecutan `git pull`.

---

## 2. Estado heredado de `develop` y qué hubo que corregir

Esta sección es material de entrega: explica por qué la rama de C7 contiene cambios en archivos que no son del módulo `system`, y qué hay que declarar como inconsistencia del proyecto.

### 2.1 El PR #37 de la semana 2 ya está fusionado

- Se fusionó en `develop` el 22 de septiembre como commit `a64566c` ("Feature/c7 week2 integration").
- Se verificó que los 10 archivos `.java` del módulo `system` quedaron **idénticos** a los de la rama de C7. Nada se perdió en la fusión.
- La fusión se hizo por *squash*, así que los commits individuales de la semana 2 no aparecen como ancestros de `develop`. Para la tabla de commits por integrante hay que consultar el historial de la rama `feature/C7-week2-integration`, no el de `develop`.

### 2.2 `develop` no compilaba

| | |
|---|---|
| **Síntoma** | `./mvnw clean test` terminaba en `BUILD FAILURE` con `cannot find symbol`. |
| **Causa** | `TrendService.java:79` (célula B4, PR #43) invoca `gradeRepository.findByStudentIdOrderByEvaluation_EvaluationDateAsc(...)`, un método que **nunca fue declarado** en `GradeRepository`. |
| **Alcance** | El proyecto entero. Ninguna célula podía compilar. |
| **Corrección aplicada** | Se declaró el método en `trend/GradeRepository` como consulta nativa, con el mismo patrón que ya usaba el repositorio de B3 y conservando el nombre original para no tocar el servicio de B4. |
| **Pendiente** | Trasladarlo a la célula B4 para que lo adopte en `develop`. |

### 2.3 El contexto de Spring no arrancaba: cinco colisiones entre células

Ninguna es de C7. Todas impedían levantar la aplicación, es decir, impedían probar cualquier endpoint del proyecto.

| # | Colisión | Entre | Corrección aplicada |
|---|---|---|---|
| 1 | Dos interfaces `GradeRepository` peleando por el nombre de bean `gradeRepository` | B3 (`dispersion`) y B4 (`trend`) | Se nombró el bean de B4 como `trendGradeRepository` |
| 2 | Dos interfaces `EvaluationRepository` peleando por `evaluationRepository` | A5 (`evaluation`) y B4 (`trend`) | Se nombró el bean de B4 como `trendEvaluationRepository` |
| 3 | Tres entidades llamadas `Evaluation` y dos llamadas `Grade` sobre las mismas tablas | A5, B3 y B4 | Nombre de entidad propio (`TrendEvaluation`, `TrendGrade`, `DispersionEvaluation`) manteniendo el mismo `@Table`, más `@Column` en snake_case en las de B4, que no los declaraba |
| 4 | Entidades de B4 sin constructor sin argumentos, una `List` de DTO que JPA no puede persistir, y una clase `Student` vacía declarada como tipo gestionado | B4 | Constructores agregados, la lista marcada `@Transient`, `Student` convertida en entidad mínima |
| 5 | `GET /api/v1/analytics/students/{id}/trend` declarada dos veces → *ambiguous mapping* | B4 y B5 | La ruta de B4 se renombró a `/students/{id}/trend-analysis`. **No se eliminó ningún endpoint.** |

Todos los parches llevan un comentario en el código que explica el motivo y la célula que debe adoptarlo.

### 2.4 Swagger UI no levantaba

| | |
|---|---|
| **Qué pasó** | El PR #44 dejó declaradas a la vez `springdoc-openapi-starter-webmvc-api` 3.1.1 y `springdoc-openapi-starter-webmvc-ui` 2.8.6. |
| **Consecuencia** | Maven descartaba el `common` 2.8.6 que necesita la UI, y el contexto fallaba con una referencia circular en `swaggerWebMvcConfigurer`. |
| **Corrección aplicada** | Se conserva solo el starter `-ui` —que ya incluye al `-api`— en la serie 3.x, la compatible con el Spring Boot 4.1.1 del proyecto. |
| **Verificado** | `/swagger-ui.html` y `/v3/api-docs` responden 200. |

### 2.5 La base de datos es H2 en memoria, no MySQL

| | |
|---|---|
| **Qué pasó** | La célula A2 incorporó a `develop` un datasource **H2 en memoria** (`jdbc:h2:mem:teacherdb`). |
| **Efecto sobre C7** | El datasource MySQL con variables de entorno (sección 12.5 del documento oficial) no está en la rama. La aplicación arranca en H2. |
| **Por qué se resolvió así** | `application.properties` es compartido por las 21 células. Imponer el MySQL de C7 cambiaría la base de datos de todo el proyecto. |
| **Consecuencia práctica** | **No hace falta instalar MySQL ni levantar Docker para desarrollar.** Ver sección 5. |
| **Inconsistencia a declarar** | `docker-compose.yml` y `.env.example` siguen apuntando a MySQL mientras la aplicación arranca en H2. |
| **Pendiente** | Llevarlo al canal de coordinadores. Es una decisión de proyecto, no de célula. |

### 2.6 Dependencias del `pom.xml`

- `spring-boot-starter-validation` y `mysql-connector-j` fueron añadidos por C7, pero la célula A5 ya los incorporó en el PR #33. Se eliminó la copia duplicada.
- `spring-boot-starter-validation` es **crítico**: sin él, `hibernate-validator` no está en el classpath y las anotaciones `@Valid` compilan pero **no producen HTTP 400**. Si desaparece del `pom.xml`, el entregable de validaciones deja de funcionar en silencio.
- El `pom.xml` ha sido corregido tres veces en el proyecto (PR #34, #37 y #44) por conflictos resueltos conservando ambos lados. **Lección:** los conflictos no se resuelven en el editor web de GitHub, y todo merge se compila antes del push.

### 2.7 Manejo de errores: sigue acotado al paquete del módulo

- `SystemExceptionHandler` mantiene `@RestControllerAdvice(basePackages = "gt.edu.uinsight.system")`.
- Se verificó que no colisiona con los handlers de otras células: C4 acota el suyo con `assignableTypes`, y el de `analytics` solo atrapa su propia excepción.
- **Limitación conocida:** una URL inexistente (ej. `/api/v1/system/noexiste`) devuelve el formato por defecto de Spring, no el de la sección 10.1. Nunca funcionó. Se comprobó que quitar `basePackages` lo empeora: convierte los 404 en 500.

---

## 3. Requisitos de la semana 3

Objetivo oficial (sección 15.3 del documento del proyecto): **implementar el núcleo funcional del módulo. Meta de avance: 60%.**

| # | Requisito | Cómo lo cubre C7 |
|---|---|---|
| 1 | APIs: CRUD, **búsquedas y filtros**, actualizaciones, **cambios de estado** y **paginación** | Dev 1 |
| 2 | Lógica de negocio en la capa de servicio (no se acepta controller → repository directo) | Dev 1 y Dev 2 |
| 3 | **Integración real con al menos otra célula** | Dev 3 |
| 4 | Logs de inicio de operación, éxito, error y regla de negocio rechazada | Coordinador |
| 5 | **Mínimo 5 pruebas unitarias** + pruebas manuales en Swagger o Postman | 2 por integrante = 6 |

Además se cierra lo que la semana 2 dejó **explícitamente diferido**:

- `GET /api/v1/system/readiness`
- `DatabaseHealthIndicator` (verificador de base de datos) → **ya integrado en la rama**
- `GET /api/v1/system/integration-status`
- Logging estructurado

### El entregable

Un **documento PDF** que contenga:

1. **Evidencias de cumplimiento** — documentación y capturas que demuestren cada requisito de la semana.
2. **Código fuente y control de versiones** — enlace al repositorio, ramas usadas, commits por integrante y estado de los Pull Requests.

> Por eso la sección 6 especifica, para cada tarea, **qué evidencia hay que capturar**. Sin la captura, el trabajo no cuenta para el PDF.

---

## 4. Estado verificado de la rama

Comprobado el 22 de septiembre de 2026 sobre `feature/C7-week3-base`, con la aplicación levantada.

### Compilación y pruebas

| Comprobación | Resultado |
|---|---|
| `./mvnw -o clean test` | **BUILD SUCCESS** |
| Pruebas | **4/4 pasan** (3 las aportó C4, 1 es la de contexto) |
| Arranque de la aplicación | `Started UinsightApplication` en 3.4 s, Tomcat en el puerto 8080 |

### Endpoints del módulo `system`

| Endpoint | Resultado verificado |
|---|---|
| `GET /api/v1/system/health` | **200** con `{"status":"UP","database":"UP"}` — ya en JSON |
| `GET /api/v1/system/checks` | 200, listado |
| `POST /api/v1/system/checks` | 201 con el recurso creado |
| `POST /api/v1/system/checks` con cuerpo vacío | **400** — la validación funciona |
| `GET /api/v1/system/checks/{id}` | 200 / 404 en formato sección 10.1 |
| `/swagger-ui.html` y `/v3/api-docs` | 200 |

### Módulos de otras células (los que necesita Dev 3)

| Endpoint | Célula | Resultado verificado |
|---|---|---|
| `GET /api/v1/teachers` | A2 | **200** |
| `GET /api/v1/evaluations` | A5 | **200** |
| `GET /api/v1/analytics/students/{id}/summary` | B5 | **200** |
| `GET /api/v1/analytics/sections/{id}/summary` | B6 | **200** |
| `GET /api/v1/analytics/sections/{id}/dispersion` | B3 | **200** |
| `GET /api/v1/alerts/{id}/interventions` | C4 | **500** — depende de la tabla `alert` que C3 no ha entregado |

> Los cinco primeros sirven como dependencia real para `/integration-status`. El de C4 no se usa.

### Ya integrado desde `feature/C7-health-json`

- `HealthResponse` — record con `status` y `database`, el formato exacto de la sección 7.3.
- `DatabaseHealthIndicator` — valida la conexión con `isValid(2s)`, devuelve `UP` / `DOWN`.
- `HealthController` devolviendo JSON, con anotaciones Swagger.
- Anotaciones Swagger en `SystemCheckController`.

**Dev 2 ya no está bloqueada:** `DatabaseHealthIndicator` está en la rama y se reutiliza tal cual.

---

## 5. Requisitos de entorno

### Obligatorio para los cuatro integrantes

| Herramienta | Nota |
|---|---|
| **JDK 25** | Obligatorio. El `pom.xml` fija `<java.version>25</java.version>`. Con JDK 17 o 21 **no compila**. |
| **Git** | |
| **IDE** | IntelliJ IDEA o VS Code con el Extension Pack for Java |

**Maven no se instala** — el repositorio incluye Maven Wrapper (`./mvnw`).

### Lo que no hace falta

| Herramienta | Motivo |
|---|---|
| **MySQL** | La aplicación arranca en **H2 en memoria**. Las tablas se crean solas con `ddl-auto=update`. |
| **Docker Desktop** | Solo servía para levantar MySQL. Queda **opcional**, para probar `docker-compose`. |
| **Dokploy y WSL** | Corresponden al despliegue (Sistemas Operativos I), no al desarrollo de Programación II. Solo los necesita el coordinador, y únicamente si va a desplegar. |

### Verificación de entorno — tarea 0 (~3 minutos, previa a escribir código)

```bash
java -version                  # debe decir 25
git --version
cd backend
./mvnw -o clean test           # debe terminar en BUILD SUCCESS, 4 tests
./mvnw -o spring-boot:run      # debe decir "Started UinsightApplication"
```

Con la aplicación levantada, estas tres deben responder:

```
http://localhost:8080/swagger-ui.html          -> carga la interfaz
http://localhost:8080/api/v1/system/health     -> {"status":"UP","database":"UP"}
http://localhost:8080/api/v1/system/checks     -> 200
```

Si algo de esto falla, el trabajo se detiene y se avisa en el grupo: es un problema de entorno, no de la tarea asignada. La primera compilación descarga dependencias y necesita red; las siguientes funcionan con `-o` (modo offline).

> Nada de esto funciona sobre `develop`. Solo sobre `feature/C7-week3-base` y las ramas que nazcan de ella.

---

## 6. Tareas por integrante

> Formato: **qué hacer** → **cuándo se considera completada** → **qué evidencia capturar para el PDF**.

---

### Coordinador — Marcos Pacheco

**Rama:** trabaja sobre `feature/C7-week3-base` y en `feature/C7-logging`

#### Qué hacer

1. **Avisar a las células afectadas** de los parches de la sección 2, para que los adopten en `develop` en lugar de que vivan solo en la rama de C7:
   - **B4** — el método faltante en `GradeRepository`, las entidades `trend`, la ruta duplicada con B5 y el `trendService = null` de `TrendController`.
   - **B3 y A5** — los nombres de bean y de entidad.
   - **Canal de coordinadores** — el `pom.xml` de springdoc y la contradicción H2 / MySQL.
2. Mantener `feature/C7-week3-base` sincronizada con `develop`. Si `develop` se mueve, rehacer el merge **resolviendo los archivos compartidos a favor de develop** y comprobar que la aplicación sigue arrancando antes de avisar al grupo.
3. Implementar **logging estructurado** (sección 10.2) con los eventos mínimos:
   - `APPLICATION_STARTED`
   - `RESOURCE_CREATED`
   - `RESOURCE_NOT_FOUND`
   - `BUSINESS_RULE_REJECTED`
4. Revisar y fusionar los Pull Requests de Dev 1, Dev 2 y Dev 3, en ese orden.
5. Redactar el PDF de entrega.

#### Completada cuando

- Los avisos a B3, B4, A5 y al canal de coordinadores están enviados y documentados.
- Los cuatro eventos de log aparecen en consola al ejercitar los endpoints.
- Los tres PR de los devs están fusionados y `./mvnw -o clean test` pasa después de cada fusión.

#### Evidencia para el PDF

- **Captura de la consola** con las líneas de log de los cuatro eventos.
- Captura del PR #37 ya fusionado en `develop`.
- Captura del árbol de ramas o del listado de PR de la célula.
- Tabla de commits por integrante (el historial de la semana 2 está en `feature/C7-week2-integration`, no en `develop`).
- **Captura del `BUILD FAILURE` de `develop` junto al `BUILD SUCCESS` de la rama de C7.** Es la evidencia del trabajo de integración, que pesa 15% de la nota.

---

### Dev 1 — Icjosue · CRUD ampliado, filtros y paginación

**Rama:** `feature/C7-check-filters`

#### Qué hacer

1. Ampliar `GET /api/v1/system/checks` con **filtros opcionales**:
   - `?component=database`
   - `?status=UP`
   - Combinables entre sí.
2. Añadir **paginación**: `?page=0&size=10`, devolviendo `Page<CheckResponse>`.
3. Implementar `PATCH /api/v1/system/checks/{id}/status` para cambiar el estado de una comprobación.
4. Añadir una **regla de negocio** en `SystemCheckService` (no en el controller): una comprobación en estado `DOWN` no puede pasar directamente a `UP` sin pasar por `DEGRADED`. Al intentarlo → **HTTP 409**.
5. Crear `UpdateCheckStatusRequest` como DTO propio de la operación (sección 9.3: un DTO por operación, nunca uno genérico).
6. Escribir **2 pruebas unitarias** de `SystemCheckService`: una del filtro y una de la regla de negocio rechazada.

#### Completada cuando

- Los filtros devuelven resultados distintos según los parámetros.
- La paginación devuelve la estructura `Page` con `totalElements` y `totalPages`.
- El `PATCH` cambia el estado y persiste.
- La transición inválida devuelve **409** con el formato de la sección 10.1 (`timestamp`, `status`, `error`, `message`, `details`, `traceId`).
- Las 2 pruebas pasan con `./mvnw -o clean test`.

#### Evidencia para el PDF

- Captura de `GET /checks?component=database&status=UP` con respuesta 200 y resultados filtrados.
- Captura de `GET /checks?page=0&size=5` mostrando la estructura de paginación.
- Captura de `PATCH /checks/{id}/status` con 200.
- **Captura del 409** con el cuerpo del error completo.
- Captura de la consola de Maven con las 2 pruebas en verde.

---

### Dev 2 — Ashly Aquino · Readiness y verificador de base de datos

**Rama:** `feature/C7-readiness`

> `DatabaseHealthIndicator` **ya está en la rama base**, escrito y probado. Se reutiliza, no se reescribe. No hay que esperar a nadie para empezar.

#### Qué hacer

1. Implementar `GET /api/v1/system/readiness`. Según el documento oficial (sección 7.3), debe indicar tres cosas:
   - Si la **base de datos** está disponible → usando `DatabaseHealthIndicator`.
   - Si las **configuraciones** están cargadas.
   - Si los **servicios críticos** están disponibles.
2. Crear `ReadinessResponse` como DTO de respuesta, con el detalle por componente.
3. Devolver el código HTTP correcto:
   - **200** si todo está listo.
   - **503 Service Unavailable** si algo no lo está. *(Es la diferencia clave con `/health`, que siempre devuelve 200 mientras el proceso viva.)*
4. Colocar la lógica de decisión en un **servicio**, no en el controller.
5. Anotar el endpoint con `@Operation` y `@ApiResponses`, siguiendo el ejemplo que ya tiene `HealthController`.
6. Escribir **2 pruebas unitarias**: una del caso "todo listo" → 200, otra del caso "base de datos caída" → 503.

#### Completada cuando

- `/readiness` responde 200 con la aplicación sana.
- Simulando una base de datos caída, responde **503** y el detalle señala cuál componente falló.
- Las 2 pruebas pasan.

#### Evidencia para el PDF

- Captura de `GET /readiness` con **200** y el cuerpo completo.
- Captura de `GET /readiness` con **503**, mostrando qué componente falló.
- Captura de Swagger UI con `/readiness` documentado.
- Captura de las 2 pruebas en verde.

---

### Dev 3 — Oliver Yecute · Integration-status (integración real entre células)

**Rama:** `feature/C7-integration-status`

> Esta tarea cubre el requisito más importante y más difícil de improvisar de la semana 3: *"integración inicial con al menos una dependencia real entre células"*. Es el de mayor peso en la nota de Integración (15%).

#### Qué hacer

1. Implementar `GET /api/v1/system/integration-status`, que reporte si los módulos de las demás células responden.
2. Consultar **los módulos ya verificados en la sección 4**, que responden 200 en esta rama:
   - `GET /api/v1/teachers` — célula A2
   - `GET /api/v1/evaluations` — célula A5
   - `GET /api/v1/analytics/students/{id}/summary` — célula B5
   - `GET /api/v1/analytics/sections/{id}/summary` — célula B6
   - `GET /api/v1/analytics/sections/{id}/dispersion` — célula B3, opcional como quinto
3. Crear `IntegrationStatusResponse` que devuelva, por cada módulo: nombre, estado (`UP` / `DOWN` / `UNKNOWN`) y tiempo de respuesta en milisegundos.
4. **Nunca propagar la excepción de otra célula.** Si un módulo falla, se reporta `DOWN` y el endpoint sigue devolviendo 200. Un módulo caído no puede tumbar el diagnóstico.
5. Reutilizar el enum `CheckStatus` existente, sin crear otro.
6. Escribir **2 pruebas unitarias**: una con todos los módulos respondiendo, otra con uno caído, verificando que se reporta `DOWN` sin lanzar excepción.

> Para el caso degradado hay una opción sin simulacros: `GET /api/v1/alerts/1/interventions` de la célula C4 devuelve **500** de verdad, porque depende de una tabla que C3 no ha entregado. Sirve como módulo caído real para la captura.

#### Completada cuando

- El endpoint devuelve el estado de **al menos 2 módulos reales** de otras células.
- Con un módulo caído, sigue respondiendo 200 y lo marca `DOWN`.
- Las 2 pruebas pasan.

#### Evidencia para el PDF

- Captura de `GET /integration-status` con el estado de todos los módulos.
- **Captura del caso degradado**: al menos un módulo en `DOWN` y el endpoint respondiendo 200.
- Captura de Swagger UI con el endpoint documentado.
- Captura de las 2 pruebas en verde.
- Esta última captura es **la prueba de la "integración real entre células"** y debe quedar bien visible en el PDF.

---

## 7. Dependencias entre tareas

### Quién depende de quién

| Tarea | Depende de | Qué necesita recibir | Tipo |
|---|---|---|---|
| Todas | Tarea 0 — verificación de entorno (sección 5) | `BUILD SUCCESS` y la aplicación levantada | Bloqueante |
| Coordinador — logging en `SystemCheckService` | **Dev 1** | El PR de filtros fusionado primero, para no editar el mismo archivo en paralelo | De orden |
| Coordinador — PDF | **Dev 1, Dev 2 y Dev 3** | Las capturas de evidencia de cada sección | Bloqueante |
| Todas, al sincronizar | **Coordinador** | Aviso de que `develop` se movió y hay que hacer `git pull` | De orden |
| Adopción de los parches en `develop` | Células **B3, B4 y A5** | Que acepten los cambios de la sección 2 | Externo, no bloquea a C7 |

### Tareas sin dependencias (arrancan de inmediato)

| Integrante | Tarea | Motivo |
|---|---|---|
| Dev 1 | Filtros, paginación, `PATCH`, regla de negocio | Todo el código base que necesita ya está en la rama |
| Dev 2 | `/readiness` | `DatabaseHealthIndicator` ya está integrado; **el bloqueo de la versión anterior de este plan quedó resuelto** |
| Dev 3 | `/integration-status` | Los cinco módulos de otras células ya responden 200 en esta rama |
| Coordinador | Logging, avisos a las otras células | Trabajo propio sobre la rama de integración |

### Riesgos de conflicto entre ramas

| Archivo | Lo tocan | Cómo se evita el conflicto |
|---|---|---|
| `SystemCheckService` | Dev 1 y Coordinador (logging) | Se fusiona primero el PR de Dev 1; el logging se escribe sobre la versión ya fusionada |
| `HealthController` | Nadie más | Ya quedó integrado con la versión JSON; no se vuelve a tocar |
| `application.properties`, `pom.xml` | Cualquiera, al sincronizar con `develop` | Solo el coordinador los modifica, y resuelve **a favor de develop**, salvo la corrección de springdoc de la sección 2.4, que hay que conservar |
| Archivos de `analytics/trend` y `analytics/dispersion` | Los parches de la sección 2 | Si B4 o B3 los corrigen en `develop`, el merge siguiente los reemplaza y **hay que verificar que la aplicación siga arrancando** |

### Dependencias externas no bloqueantes

- **Célula C3 → C4:** la tabla `alert` no existe y los endpoints de intervenciones devuelven 500. No afecta a ninguna tarea de C7; Dev 3 lo usa a favor como módulo caído real.
- **Célula A2 → datasource H2:** define la base de datos de todo el proyecto. Se documenta como limitación (sección 10); no se modifica.

---

## 8. Itinerario de la jornada

| Momento | Quién |
|---|---|
| 0–15 min | Todos: tarea 0 (verificación de entorno, sección 5) y creación de su rama |
| 0–20 min | Coordinador: enviar los avisos a B3, B4, A5 y al canal de coordinadores |
| 15–90 min | Dev 1 (filtros, paginación, `PATCH`), Dev 2 (`/readiness`) y Dev 3 (`/integration-status`), los tres en paralelo y sin esperarse |
| 90–120 min | Todos: escribir las 2 pruebas unitarias de cada tarea y dejar `./mvnw -o clean test` en verde |
| 120–150 min | Todos: probar los endpoints en Swagger UI y capturar las evidencias de su sección |
| 150–170 min | `git pull origin feature/C7-week3-base`, resolución local de conflictos y apertura de los PR: primero Dev 1, luego Dev 2 y Dev 3 |
| 170–200 min | Coordinador: fusionar los tres PR en ese orden, ejecutar `clean test` tras cada fusión y escribir el logging estructurado sobre la versión final |
| Últimos 30 min | Coordinador: prueba conjunta de los cuatro endpoints, captura de los logs y armado del PDF con las evidencias recibidas |

---

## 9. Resumen de cobertura

| Requisito de la semana 3 | Responsable | Estado |
|---|---|---|
| Filtros y búsquedas | Dev 1 | Pendiente |
| Paginación | Dev 1 | Pendiente |
| Cambios de estado | Dev 1 | Pendiente |
| Regla de negocio en el servicio | Dev 1 | Pendiente |
| Verificador de base de datos | Dev 2 | **Listo e integrado** |
| `/health` en formato JSON | Coordinador | **Listo e integrado** |
| Endpoint `/readiness` | Dev 2 | Pendiente |
| Endpoint `/integration-status` | Dev 3 | Pendiente |
| Integración real entre células | Dev 3 | Pendiente |
| Logging estructurado | Coordinador | Pendiente |
| Mínimo 5 pruebas unitarias | Todos (2 c/u = 6) | Pendiente |
| Documento PDF | Coordinador | Pendiente |
| Que el proyecto compile y arranque | Coordinador | **Listo**, ver sección 2 |

---

## 10. Limitaciones a declarar en el documento de entrega

1. La rama de C7 contiene **parches en código de las células B3 y B4** sin los cuales el proyecto no compila ni arranca (sección 2.2 y 2.3). Están comentados en el código y notificados a sus células, pero **todavía no fueron adoptados en `develop`**.
2. La ruta `GET /api/v1/analytics/students/{id}/trend` de la célula B4 fue **renombrada** a `/trend-analysis` para eliminar el mapeo ambiguo con la de B5. La decisión definitiva corresponde a esas dos células.
3. Los dos endpoints de `TrendController` (B4) responden 500 aunque la aplicación arranque, porque su servicio está fijado a `null` en el código. Es código sin terminar de B4.
4. La aplicación arranca en **H2 en memoria**, no en MySQL, porque así viene configurado `develop`. `docker-compose.yml` y `.env.example` siguen apuntando a MySQL.
5. El manejo de errores sigue **acotado al paquete del módulo** mientras no se asigne el responsable del handler global del proyecto.
6. Las **URL inexistentes** devuelven el formato de error por defecto de Spring, no el de la sección 10.1. Limitación conocida, no introducida esta semana.
7. Los endpoints de la célula C4 dependen de una tabla `alert` que la célula C3 aún no entrega.
8. Tres entidades de distintas células (`Evaluation`) y dos (`Grade`) **modelan las mismas tablas con tipos distintos** (`String` frente a `LocalDate`, `Double` frente a `BigDecimal`). Conviven porque `ddl-auto=update` acumula columnas, pero es una inconsistencia de diseño del proyecto que hay que resolver entre A5, B3 y B4.

---

## 11. Lista de verificación previa al Pull Request

- [ ] `./mvnw -o clean test` termina en **BUILD SUCCESS**
- [ ] La aplicación levanta y los endpoints propios responden en **Swagger UI**, no solo en las pruebas automáticas
- [ ] **Todas** las evidencias de la sección correspondiente, capturadas
- [ ] `git pull origin feature/C7-week3-base` ejecutado **justo antes** de abrir el PR
- [ ] Conflictos, si hubo, **resueltos en local y recompilados** — nunca en el editor web de GitHub
- [ ] Commits con la convención acordada: `feat:`, `fix:`, `test:`, `docs:`, `refactor:`, `chore:`
