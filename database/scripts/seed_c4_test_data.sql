-- Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
-- Script: seed_c4_test_data.sql
-- Módulo: C4 - Intervenciones y Seguimiento
-- Datos mínimos de prueba para ejercer los endpoints de intervención desde Postman
-- mientras la célula C3 (alertas) no expone su API real.
--
-- ADVERTENCIA: las tablas academic_period, teacher, course, section y alert
-- pertenecen a otras células. Al momento de escribir este seed no existen scripts
-- 01-08 en el repo que las creen, así que las columnas usadas abajo son una
-- SUPOSICIÓN razonable basada en el documento del proyecto, sin confirmar con esas
-- células. Ajustar nombres/tipos de columna cuando esos scripts existan.
-- Ver docs/c4-pendientes-coordinacion.md, punto 5.

INSERT INTO academic_period (id, name, start_date, end_date)
VALUES (1, '2026-II', '2026-07-01', '2026-11-30');

INSERT INTO teacher (id, first_name, last_name, email)
VALUES (1, 'Ana', 'Pérez', 'ana.perez@uinsight.edu.gt');

INSERT INTO course (id, name, code)
VALUES (1, 'Matemática I', 'MAT101');

INSERT INTO section (id, course_id, teacher_id, academic_period_id, name)
VALUES (1, 1, 1, 1, 'Sección A');

-- Alerta activa: debe permitir crear intervenciones (201).
INSERT INTO alert (id, section_id, status, created_at)
VALUES (10, 1, 'NEW', NOW());

-- Alerta ya resuelta: debe rechazar intervenciones con 409 ALERT_NOT_ACTIVE.
INSERT INTO alert (id, section_id, status, created_at)
VALUES (11, 1, 'RESOLVED', NOW());
