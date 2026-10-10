# Pendientes de coordinación — Célula C4 (Intervenciones y Seguimiento)

> Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy

Generado durante la implementación de la Fase 2 (semana 2). Ninguno de estos
cambios fue aplicado por mí a archivos compartidos; los dejo aquí para que el
coordinador general los aplique.

## 1. Dependencias faltantes en `backend/pom.xml`

Mi código (entidades JPA, DTOs con `record` + Bean Validation, y documentación
Swagger en el controller) asume que estas dependencias existen. Sin ellas,
`backend` no compila.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Adicional no listado en el checklist original: `springdoc-openapi`

El contexto del proyecto asumía que `springdoc-openapi-starter-webmvc-ui` ya
estaba en el pom, pero **no está** (verificado en `backend/pom.xml` actual).
Mi controller usa `@Tag`, `@Operation`, `@ApiResponses` y mis DTOs usan
`@Schema` (paquete `io.swagger.v3.oas.annotations.*`), que vienen de esta
dependencia:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version><!-- ajustar a la versión compatible con Spring Boot 4.1.1 --></version>
</dependency>
```

## 2. Datasource en `backend/src/main/resources/application.properties`

Actualmente el archivo solo tiene `spring.application.name=uinsight`, sin
datasource. Falta:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.open-in-view=false
```

`ddl-auto=none` porque el esquema se gestiona con los scripts de
`database/scripts/` (convención ya establecida en el proyecto), no con
generación automática de Hibernate.

## 3. `AlertValidationJdbcAdapter` es temporal (BKL-07)

Mientras la célula C3 no exponga `GET /api/v1/alerts/{id}`, creé
`intervention/service/AlertValidationJdbcAdapter` que consulta la tabla
`alert` directamente vía `JdbcTemplate` (`SELECT` de `id` y `status`).

**Asunción sin confirmar con C3:** que la tabla `alert` tiene columnas `id`
(PK) y `status` (string) con al menos los valores `RESOLVED` y `DISMISSED`
para "no activa". Si C3 usa otros nombres de columna o de estado, hay que
actualizar únicamente esta clase (por diseño, vía el puerto
`AlertValidationPort`, nada más en mi módulo depende del detalle).

En semana 3 (BKL-07) reemplazar `AlertValidationJdbcAdapter` por un cliente
HTTP real hacia la API de C3.

## 4. Manejo global de errores

No existe ningún `@RestControllerAdvice`/`@ControllerAdvice` en el repo
todavía (revisé `gt/edu/uinsight/exception/` y `gt/edu/uinsight/common/`,
ambos vacíos salvo `.gitkeep`). Por eso creé
`intervention/exception/InterventionExceptionHandler` con
`@RestControllerAdvice(assignableTypes = InterventionController.class)`,
acotado solo a mi controller para no chocar con lo que arme otra célula o el
coordinador.

Si en algún momento se define un `@RestControllerAdvice` **global** para todo
el proyecto, pídanle a quien lo escriba que registre estas excepciones (y
puedo retirar mi handler acotado):

| Excepción | HTTP | código `error` |
|---|---|---|
| `AlertNotFoundException` | 404 | `ALERT_NOT_FOUND` |
| `InterventionNotFoundException` | 404 | `INTERVENTION_NOT_FOUND` |
| `AlertNotActiveException` | 409 | `ALERT_NOT_ACTIVE` |
| `MethodArgumentNotValidException` | 400 | `VALIDATION_ERROR` |
| cualquier otra | 500 | `INTERNAL_ERROR` |

Formato de respuesta usado (`ErrorResponse` en mi paquete, candidato a
volverse un tipo compartido si el coordinador centraliza esto):
`{ timestamp, status, error, message, details, traceId }`.

## 5. `database/scripts/seed_c4_test_data.sql` usa tablas de otras células

Para poder probar mis 3 endpoints en Postman mientras C3 no tiene API, el
seed inserta filas en `academic_period`, `teacher`, `course`, `section` y
`alert`. **No existen todavía scripts `01`–`08` en el repo** que creen esas
tablas, así que las columnas que usé (`id`, `name`, `start_date`, `end_date`,
`first_name`, `last_name`, `email`, `code`, `course_id`, `teacher_id`,
`academic_period_id`, `section_id`, `status`, `created_at`) son una
**suposición razonable basada en el documento del proyecto, sin confirmar**.
Cuando esas células publiquen sus scripts reales, hay que ajustar este seed
(o pedirme que lo ajuste) para que los `INSERT` no fallen por columnas o
tipos distintos.

## 6. Compilación

Con los faltantes de los puntos 1 y 2, `./mvnw -f backend/pom.xml compile`
**no va a compilar** en este momento — es el resultado esperado, no un error
en mi código. En cuanto el coordinador aplique el pom y el
application.properties, debería compilar sin más cambios de mi parte.

Verifiqué esto forzando `-Dmaven.compiler.release=25` solo como chequeo local
(no se tocó el pom): los únicos errores que aparecen son símbolos de
`jakarta.persistence`, `jakarta.validation`, `io.swagger.v3.oas.annotations`,
`org.springframework.data.jpa` y `org.springframework.transaction` — exactamente
las dependencias listadas arriba. No hay errores de sintaxis en mi código.

## 7. `<java.version>26</java.version>` no coincide con el JDK local

El pom fija Java 26, pero el JDK instalado en esta máquina es Temurin 25.0.2
(`java -version` → `openjdk version "25.0.2"`). `./mvnw -f backend/pom.xml
compile` falla de entrada con `error: release version 26 not supported`, antes
de siquiera llegar a los faltantes de los puntos 1-2. Esto es independiente de
mi módulo; lo señalo porque bloquea compilar el proyecto completo en cualquier
máquina que no tenga JDK 26 instalado. No es algo que yo deba decidir (podría
ser intencional si el equipo va a instalar JDK 26), pero conviene que el
coordinador lo confirme.
