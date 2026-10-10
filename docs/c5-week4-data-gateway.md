# C5 - Gateway de datos reales, Semana 4

Responsable: Cesar Luis Aguilon Pascual. Rama: `feature/c5-data-gateway`.

## Entrega

`JpaReportDataGateway` implementa los nueve metodos de `ReportDataGateway`.
Lee secciones e inscripciones de A4, periodos de A1 y docentes de A2. Traduce
los IDs al codigo de docente y al periodo `YYYY-1` / `YYYY-2` que consumen los
reportes. Solicita el riesgo a `RiskGateway`, conservando `null` cuando B7 no
lo puede calcular. Los servicios existentes mantienen sus dependencias.

Incluye `AlertQueryPort`, el contrato que implementara Allan. El gateway
completa las alertas con datos de su seccion antes de aplicar los filtros,
incluidos los filtros combinados. Solo consulta una vez cada seccion por lista
de alertas. Cada llamada a un proveedor se protege por separado; una falla se
registra con su fuente y no elimina los datos obtenidos de otras fuentes.

`countEnrolledStudents` cuenta las inscripciones devueltas por A4, sin filtrar
por estado, segun el contrato de esta semana. En el gateway mock usa
`studentsAtRisk` como referencia simulada acordada en la guia; ese valor no es
una matricula real. Se elimina la clase vacia duplicada `report/mock/MockAlert`.

## Dependencias e integracion

- Se integro `origin/develop` y la entrega de Angel de `feature/c5-overview`
  hasta `d4e2912`, que proporciona `RiskGateway`, `RiskSnapshot` y el adaptador
  B7. Su PR debe fusionarse antes que este, como establece la guia. Mientras
  no se fusione, la comparacion contra `develop` incluye tambien esos cambios.
- Se corrigio un import preexistente de A1 que impedia compilar:
  `PropertyReferenceException` esta en `org.springframework.data.core` en el
  JAR Spring Data Commons 4.1.1 del proyecto. No cambia su comportamiento.
- Se actualizaron los imports de `WebMvcTest` y la anotacion `MockitoBean` en
  las pruebas MVC de A1 y B6: los imports anteriores de Spring Boot 3 impedían
  compilar cualquier prueba con Spring Boot 4.1.1. Se conservan sus casos y
  aserciones y se incluyen en la verificacion.
- Los puertos de alertas JDBC y de respaldo son entrega de Allan. Hasta su
  integracion, el parametro opcional del gateway permite iniciar el contexto
  JPA sin ese bean; las consultas de alertas devuelven vacio y registran C3
  como no disponible. No se incorpora otra implementacion que compita con la
  suya. `c5.alert-source=jdbc` requiere su entrega para leer la tabla.

## Ejecucion y datos de prueba

El valor predeterminado es `c5.data-source=mock`. Solo existe un bean
`ReportDataGateway` para cada valor soportado (`mock` o `jpa`). Esto selecciona
el origen de C5; los demas modulos de la aplicacion siguen requiriendo su base
de datos.

En PowerShell, con JDK 25 y una base MySQL de desarrollo configurada:

```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--c5.data-source=jpa --c5.alert-source=none"
```

Una vez creadas las tablas por Hibernate, ejecutar **una sola vez**
`database/scripts/local-dev/seed_c5_week4.sql` en un esquema local de pruebas
vacio. No mezclarlo con el stub de C4: aquel no contiene `section_id` ni
`created_at`. El script no borra datos ni modifica tablas existentes.

Datos esperados: tres secciones (10, 11, 12), dos docentes, un periodo y tres
inscripciones. Las secciones 10 y 11 pertenecen al curso 5; los conteos de
inscripciones son 2, 1 y 0. De las tres alertas del seed, dos estan activas en
la seccion 10 y una esta resuelta en la 11 (la tabla necesita el puerto de Allan
para ser consumida).

Despues de integrar a Allan se puede arrancar con:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--c5.data-source=jpa --c5.alert-source=jdbc"
```

## Pruebas y limites

```powershell
cd backend
.\mvnw.cmd test "-Dtest=gt.edu.uinsight.report.**,gt.edu.uinsight.academicperiod.**,SummaryControllerWebMvcTest"
```

Las pruebas nuevas cubren traduccion, conteos, fallos de proveedores, filtros
combinados, enriquecimiento de alertas y seleccion de beans en un contexto
Spring sin base de datos. No sustituyen la prueba manual del seed en MySQL ni
la validacion de los cuatro endpoints tras integrar las entregas restantes.

Verificacion del 02/10/2026 con JDK 25: **114 pruebas, 0 fallos, 0 errores,
0 omitidas** (`BUILD SUCCESS`): 71 de C5, 39 de A1 y 4 del controlador B6.
La entrega agrega 19 pruebas de C5 (14 del gateway JPA, 4 de configuracion y
1 del conteo simulado). El seed no se ejecuto sobre una base MySQL real.

- A1 aun no publica el catalogo `Course` en la base integrada: consultas de
  cursos vacias, `courseCode=null`. Un filtro por codigo de curso no devuelve
  secciones sin un codigo verificable; no se ignora ese filtro.
- A3 sigue pendiente: `studentsAtRisk=0` como valor de transporte acordado, no
  como evidencia de ausencia de riesgo.
- La interfaz acordada no permite distinguir un conteo cero de una falla de
  A4 ni una lista vacia de una fuente caida. Se registra la fuente en el log;
  no se agrega un contrato paralelo de disponibilidad ni estado mutable
  compartido. La declaracion completa de `unavailableSources` sigue siendo
  trabajo de integracion de los reportes de la celula.
- Los reportes de curso/seccion degradados, validacion contra catalogo, CORS y
  lectura JDBC de alertas corresponden a las entregas de Adrianna y Allan.

## Origen de datos (semana 5)

| Propiedad | Valores previstos | Por omision | Efecto |
|-----------|-------------------|-------------|--------|
| `c5.data-source` | `mock`, `jpa` | `mock` | `jpa` consulta los repositorios reales de periodos A1, docentes A2, secciones e inscripciones A4. |
| `c5.alert-source` | `none`, `jdbc` | `none` | `jdbc` seleccionara la lectura de la tabla `alert` de C3 cuando se integre el adaptador de Allan. |

El gateway de esta rama no consulta directamente A3 ni el catalogo de cursos.
`c5.alert-source` documenta el contrato de integracion: no activa por si sola un
adaptador que aun no esta incluido. Sin `AlertQueryPort`, el gateway JPA devuelve
listas de alertas vacias. En modo `mock`, las alertas vienen del gateway simulado;
`none` no elimina esas alertas de prueba. La seleccion de origen de C5 tampoco
elimina la dependencia de base de datos de los demas modulos del backend.

Para probar el origen real, configurar una base local y usar
`--c5.data-source=jpa --c5.alert-source=none`. Tras integrar el adaptador JDBC,
usar `--c5.data-source=jpa --c5.alert-source=jdbc` y cargar el seed descrito arriba
solo en un esquema de pruebas vacio.

## Pruebas de integracion (semana 5)

`JpaReportDataGatewayIntegrationTest` utiliza H2 y los repositorios reales de
A1, A2 y A4. Persiste datos, ejecuta `flush` y limpia el contexto de persistencia
antes de consultar: las verificaciones leen la base, no objetos en memoria.
Cada prueba se revierte al terminar mediante una transaccion.

Los siete casos cubren una seccion completa, periodo inexistente, docente
inexistente, seccion inexistente, ausencia de C3, filtrado entre dos semestres y
conteo de inscripciones de distintas secciones. El caso de filtro exige una
coincidencia concreta para evitar que una lista vacia pase la prueba. Los huecos
usan IDs sin registro, porque las columnas de seccion no admiten valores nulos.
B7 se sustituye por `RiskSnapshot.unavailable()`; no se presenta como una prueba
del motor de riesgo, de C3 ni de MySQL.

Se usa `@SpringJUnitConfig` con auto-configuracion JPA y escaneo limitado a las
entidades/repositorios consumidos. El ejemplo de la guia usa el import de
`@DataJpaTest` de Spring Boot 3; Boot 4 requiere otro modulo de pruebas que no
esta en el POM. Este contexto prueba las mismas consultas sin agregar una
dependencia ni escanear entidades ajenas a la entrega de C5.

Desde `backend`, con JDK 25:

```powershell
.\mvnw.cmd -o test "-Dtest=JpaReportDataGatewayIntegrationTest"
.\mvnw.cmd -o clean test "-Dtest=gt.edu.uinsight.report.**,gt.edu.uinsight.academicperiod.**,SummaryControllerWebMvcTest"
```

Las correcciones de integracion de Semana 4 se conservan como antecedente:
`AcademicExceptionHandler` usa `org.springframework.data.core.PropertyReferenceException`;
`AcademicPeriodControllerTest` y `SummaryControllerWebMvcTest` usan los imports
de `WebMvcTest` de Boot 4 y `MockitoBean`. Al actualizar con `origin/develop`,
los conflictos de B7 y del controlador de B6 se resolvieron conservando las
versiones ya integradas en `develop`. No se alteran sus comportamientos desde
esta entrega. La fusion del PR y la evidencia conjunta corresponden al cierre
de integracion de la celula; un push no demuestra que el PR este fusionado.

Verificacion del 09/10/2026 con JDK 25: las siete pruebas nuevas de H2 pasan.
La ejecucion conjunta de C5, A1 y `SummaryControllerWebMvcTest` compila desde
cero y ejecuta 119 pruebas: 0 fallos de asercion, 1 error, 0 omitidas. El error
es `SummaryControllerWebMvcTest.getSummary_responde500CuandoOcurreError` de B6:
la `RuntimeException` del escenario de prueba sale como `ServletException`.
Ese archivo se conserva igual que en `origin/develop`; no se corrige desde C5
ni se presenta la ejecucion conjunta como exitosa. Evidencia local:
`tmp/c5-week5-verification.log` y los reportes de Maven en `backend/target/surefire-reports`.

La comprobacion separada de C5 y A1 termina con **BUILD SUCCESS: 115 pruebas,
0 fallos, 0 errores, 0 omitidas** (76 de C5 y 39 de A1). Comando:
`.\mvnw.cmd -o test "-Dtest=gt.edu.uinsight.report.**,gt.edu.uinsight.academicperiod.**"`.
Evidencia local: `tmp/c5-week5-c5-a1.log`. El PR de Luis es el #129 y su destino
se corrige de `main` a `develop`, la rama de integracion del proyecto.
