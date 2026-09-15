-- Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
-- Script: stub_alert_for_c4_testing.sql
-- Carpeta: database/scripts/local-dev/  (NO va en database/scripts/, a proposito)
--
-- SOLO PARA DESARROLLO LOCAL. Este NO es el script oficial de la tabla `alert`
-- de la celula C3 (ese todavia no existe en el repo). Es un stub minimo con
-- unicamente lo que mi modulo (intervention) necesita para funcionar:
--   - id     (referenciado por la FK intervention.alert_id)
--   - status (consultado por AlertValidationJdbcAdapter para RN-2)
--
-- Cuando C3 publique su script real de `alert`, hay que:
--   1. Dejar de usar este stub (borrar la tabla o el schema de prueba local).
--   2. Ejecutar el script real de C3 en su lugar.
-- No commitear datos generados con este stub como si fueran de C3.
--
-- ORDEN DE EJECUCION para probar C4 en local sin esperar a C3:
--   1. Este archivo (crea `alert` + 2 filas de prueba)
--   2. database/scripts/09_create_intervention.sql
--   3. database/scripts/10_create_follow_up.sql
--   (NO ejecutes database/scripts/seed_c4_test_data.sql junto con este stub:
--    ese seed asume academic_period/teacher/course/section/alert completos,
--    que este stub no crea. Son dos caminos alternativos, no combinables.)

CREATE TABLE IF NOT EXISTS alert (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Alerta activa: debe permitir crear intervenciones (201).
INSERT INTO alert (id, status) VALUES (10, 'NEW');

-- Alerta ya resuelta: debe rechazar intervenciones con 409 ALERT_NOT_ACTIVE.
INSERT INTO alert (id, status) VALUES (11, 'RESOLVED');
