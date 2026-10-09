#!/usr/bin/env bash
# Coleccion de peticiones de la celula A1: 12 endpoints + casos 200/201/400/404/409.
# Uso: BASE=http://localhost:8080 bash a1-smoke.sh
# Requiere la base con los seeds de A1 recien cargados (3 periodos, 5 cursos).
BASE="${BASE:-http://localhost:8080}/api/v1"
J='Content-Type: application/json'

call() {  # call <esperado> <metodo> <ruta> [json]
  local expected=$1 method=$2 path=$3 body=$4
  local out code
  out=$(curl -s -w '\n%{http_code}' -X "$method" "$BASE$path" -H "$J" ${body:+-d "$body"})
  code=$(tail -n1 <<<"$out")
  local mark="OK "; [ "$code" = "$expected" ] || mark="XX "
  printf '%s %-6s %-40s -> %s (esperado %s) %s\n' "$mark" "$method" "$path" "$code" "$expected" \
    "$(head -n -1 <<<"$out" | head -c 150)"
}

echo "===== PERIODOS ACADEMICOS ====="
call 200 GET   "/academic-periods"
call 200 GET   "/academic-periods?status=ACTIVE&page=0&size=5&sort=year,desc"
call 400 GET   "/academic-periods?status=ABIERTO"
call 200 GET   "/academic-periods/active"
call 200 GET   "/academic-periods/2"
call 404 GET   "/academic-periods/999"
call 400 GET   "/academic-periods/abc"
call 201 POST  "/academic-periods" '{"name":"Primer Semestre","year":2027,"startDate":"2027-01-15","endDate":"2027-05-30"}'
call 400 POST  "/academic-periods" '{"name":"","year":2027}'
call 400 POST  "/academic-periods" '{"name":"Invertido","year":2027,"startDate":"2027-06-01","endDate":"2027-01-01"}'   # RN-02
call 409 POST  "/academic-periods" '{"name":"primer semestre","year":2027,"startDate":"2027-01-15","endDate":"2027-05-30"}' # RN-07
call 200 PUT   "/academic-periods/3" '{"name":"Segundo Semestre","startDate":"2026-07-13","endDate":"2026-12-05"}'
call 409 PUT   "/academic-periods/1" '{"name":"Segundo Semestre","startDate":"2025-07-07","endDate":"2025-11-29"}'     # RN-04
call 409 PATCH "/academic-periods/3/status" '{"status":"ACTIVE"}'   # RN-03 (ya hay un ACTIVE)
call 409 PATCH "/academic-periods/3/status" '{"status":"CLOSED"}'   # RN-06 (PLANNED -> CLOSED)
call 409 PATCH "/academic-periods/1/status" '{"status":"ACTIVE"}'   # RN-04/RN-06 (reabrir CLOSED)
call 400 PATCH "/academic-periods/3/status" '{"status":"FINISHED"}'
call 200 PATCH "/academic-periods/2/status" '{"status":"CLOSED"}'   # ACTIVE -> CLOSED
call 200 PATCH "/academic-periods/3/status" '{"status":"ACTIVE"}'   # PLANNED -> ACTIVE
call 405 DELETE "/academic-periods/3"                               # RN-05

echo "===== CURSOS ====="
call 200 GET   "/courses"
call 200 GET   "/courses?status=ACTIVE&page=0&size=2&sort=code,asc"
call 400 GET   "/courses?status=BORRADO"
call 200 GET   "/courses/2"
call 404 GET   "/courses/999"
call 200 GET   "/courses/code/prog2"
call 404 GET   "/courses/code/NOEXISTE"
call 201 POST  "/courses" '{"code":"PROG-II","name":"Programacion II Avanzada","description":"Curso de POO y APIs REST","credits":5}'
call 409 POST  "/courses" '{"code":"prog-ii","name":"Duplicado"}'                  # RN-01
call 400 POST  "/courses" '{"code":"EST1","name":"Estadistica","credits":0}'       # RN-08
call 400 POST  "/courses" '{"code":"","name":""}'
call 200 PUT   "/courses/3" '{"name":"Bases de Datos I","description":"Modelado relacional, SQL y normalizacion.","credits":4}'
call 400 PUT   "/courses/3" '{"name":"Bases de Datos I","credits":-1}'           # RN-08
call 200 PATCH "/courses/5/status" '{"status":"INACTIVE"}'                        # RN-05 / RN-09
call 409 PATCH "/courses/5/status" '{"status":"INACTIVE"}'
call 405 DELETE "/courses/5"                                                      # RN-05
