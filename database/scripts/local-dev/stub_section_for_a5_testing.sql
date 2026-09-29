-- Celula A5 - Gestión de evaluaciones
-- Script: stub_section_for_a5_testing.sql
-- Carpeta: database/scripts/local-dev/  (NO va en database/scripts/, a proposito)
--
-- SOLO PARA DESARROLLO LOCAL. Este NO es el script oficial de la tabla
-- `section` de la célula A4 (ese todavía no existe en el repo). Es un stub
-- mínimo con únicamente lo que mi módulo (evaluation) necesita para
-- funcionar:
--   - id     (referenciado por evaluation.section_id)
--   - status (consultado por SectionValidationJdbcAdapter para RN1)
--
-- Cuando A4 publique su script real de `section`, hay que:
--   1. Dejar de usar este stub (borrar la tabla o el schema de prueba local).
--   2. Ejecutar el script real de A4 en su lugar.
-- No commitear datos generados con este stub como si fueran de A4.
--
-- ORDEN DE EJECUCION para probar A5 en local sin esperar a A4:
--   1. Este archivo (crea `section` + 2 filas de prueba)
--   2. Levantar la app y usar sectionId=10 (activa) o sectionId=11 (inactiva)

CREATE TABLE IF NOT EXISTS section (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL
);

-- Sección activa: debe permitir crear/actualizar evaluaciones (201/200).
INSERT INTO section (id, status) VALUES (10, 'ACTIVE');

-- Sección cerrada: debe rechazar con 422 SECTION_NOT_ACTIVE.
INSERT INTO section (id, status) VALUES (11, 'CLOSED');
