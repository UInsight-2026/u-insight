# C7 -- Semana 5: parche y guardia

Archivos de la Tarea 0 que antes solo vivian en la carpeta `Context/` local del
coordinador. Se subieron a la rama para que los cuatro integrantes los tuvieran con
solo traer `feature/C7-week5-base`.

## Estado al cierre de la semana 5 (2026-10-09)

El bug de B7 **ya esta corregido en develop**: commit `4825cff`, PR #147. El import
de `@Id` en `alert/model/Alert.java` apunta a `jakarta.persistence` como debe.

En consecuencia:

- El arreglo temporal que C7 habia subido (commit `bf47249`) **se revirtio** en el
  commit `eb58c1a`. La rama de C7 ya no modifica ningun archivo de B7.
- El parche `C7-parche-local-B7-Alert-id.patch` **ya no hace falta**. Se conserva
  solo como registro de lo que se hizo durante la semana.
- La excepcion `EXCEPCION_B7` se retiro de la guardia. `C7-pre-push` volvio a ser
  estricto: bloquea cualquier cambio fuera de `gt/edu/uinsight/system` y `docs/c7-`.

Si traes `develop` actualizado, la aplicacion arranca sin aplicar nada.

## Cierre de la celula 7 (2026-10-12)

`feature/C7-week5-base` quedo con todo lo de la semana 5 integrado:

- merge de `origin/develop` hasta el PR #141 -- **sin conflictos**.
- merge de `feature/C7-week5-controller-tests` (commit `500ab57`) -- **sin conflictos**.
- la rama sigue sin tocar ningun archivo de otra celula; `C7-pre-push` pasa limpio.

### Pruebas del modulo system

44 pruebas, 0 fallos, 0 errores, 3 deshabilitadas -- BUILD SUCCESS. Se corrieron
con el pom oficial de develop (Spring Boot 4.1.1 / Java 25, PR #106).

### Bloqueador abierto: el pom de develop

El PR #141 (modulo AlertRule, celula B7) **reemplazo `backend/pom.xml` completo**
por una copia vieja de la celula C2: Spring Boot 4.1.1 -> 3.2.3 y Java 25 -> 17.
Es la tercera vez que pasa (ya se corrigio en los PR #83, #87 y #106).

Con ese pom **no compila el codigo de pruebas de develop**, y no es solo C7:

| Celula | Archivo que deja de compilar | Causa |
| --- | --- | --- |
| C7 | `system/controller/*Test.java`, `SystemEndToEndFlowTest` | `org.springframework.boot.webmvc.test.autoconfigure`, `MockitoBean` |
| A1 | `academicperiod/controller/AcademicPeriodControllerTest` | `MockitoBean` |
| A2 | `course/controller/CourseControllerTest` | paquete `webmvc.test.autoconfigure`, `MockitoBean` |
| B6 | `analytics/summary/.../SummaryControllerWebMvcTest` | `MockitoBean` |

Tambien rompe el codigo principal de A1:
`academicperiod/support/exception/AcademicExceptionHandler.java` importa
`org.springframework.data.core.PropertyReferenceException`, que es la ruta correcta
en Spring Data 4 (en 3.2.3 vivia en `org.springframework.data.mapping`). El codigo
de A1 esta bien; lo que esta mal es el pom.

C7 **no corrige el pom en su rama**: es un archivo compartido y la guardia nace
justo de ese problema (PR #97). Va en una rama aparte, como se hizo en el PR #106.

## Instalar la guardia pre-push

Paso 2 de la Tarea 0. Una sola vez por copia del repositorio:

```powershell
cp docs/c7-week5/C7-pre-push .git/hooks/pre-push
chmod +x .git/hooks/pre-push
```

Igual en PowerShell, Git Bash y WSL.

Quien la tenga instalada de antes **debe reinstalarla**: la version anterior llevaba
la excepcion para `Alert.java` y ya no corresponde.

## Para que sirve el parche (historico)

Entre el 9 de octubre y el PR #147, `Alert.java` importaba `@Id` de
`org.springframework.data.annotation` en vez de `jakarta.persistence`. Hibernate no
construia el `EntityManagerFactory` y **ninguna** aplicacion del proyecto arrancaba:

```
AnnotationException: Entity 'gt.edu.uinsight.alert.model.Alert' has no identifier
(every '@Entity' class must declare or inherit at least one '@Id' or '@EmbeddedId' property)
```

C7 lo parcheo en local para poder trabajar y lo reporto a B7 sin corregirlo en el
PR, porque es codigo de otra celula. B7 lo resolvio por su cuenta en el PR #147.
