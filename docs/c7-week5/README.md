# C7 -- Semana 5: parche y guardia

Archivos de la Tarea 0 que antes solo vivian en la carpeta `Context/` local del
coordinador. Se suben a la rama para que los cuatro integrantes los tengan con
solo traer `feature/C7-week5-base`.

## No hace falta aplicar el parche

`C7-parche-local-B7-Alert-id.patch` esta aqui **solo como referencia**. El arreglo
ya viene en la rama, en el commit `bf47249`. Si acabas de hacer `git pull` de
`feature/C7-week5-base`, tu `Alert.java` ya esta correcto y `git apply` fallara con
`patch does not apply` -- eso es lo esperado, no un error tuyo.

Comprobarlo asi:

```powershell
git log --oneline -1 bf47249
grep "import jakarta.persistence.Id" backend/src/main/java/gt/edu/uinsight/alert/model/Alert.java
```

Si la segunda linea devuelve el import, no toques nada y sigue con el paso 3 de la
Tarea 0 (`docker compose -f docker-compose.dev.yml up -d`).

**No cambies imports a mano.** El archivo es de la celula B7; el unico cambio
autorizado es el del commit `bf47249`, que ya esta hecho.

El parche solo se usa si alguien necesita reconstruir el arreglo sobre una rama
que no lo trae:

```powershell
git apply "docs/c7-week5/C7-parche-local-B7-Alert-id.patch"
```

Sin commitear: es codigo de otra celula y no viaja al PR.

## Instalar la guardia pre-push

Paso 2 de la Tarea 0. Una sola vez por copia del repositorio:

```powershell
cp docs/c7-week5/C7-pre-push .git/hooks/pre-push
chmod +x .git/hooks/pre-push
```

Igual en PowerShell, Git Bash y WSL. Bloquea el push si la rama trae cambios fuera
de `gt/edu/uinsight/system`, con una excepcion temporal para `Alert.java`.

Quien ya la tenga instalada desde `Context/C7-pre-push` no necesita reinstalarla:
es el mismo archivo.

## Antes del PR a develop

Retirar el commit `bf47249` y la excepcion `EXCEPCION_B7` de la guardia. Cuando B7
corrija el bug en `develop`, esta carpeta entera sobra.
