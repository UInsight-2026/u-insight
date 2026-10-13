-- C5 / Cesar Luis Aguilon Pascual - Semana 4.
-- SOLO DESARROLLO LOCAL, sobre un esquema de pruebas vacio creado por Hibernate.
-- No es un script oficial de A1, A2, A4 o C3. Ejecutar una sola vez.
-- No combinar con stub_alert_for_c4_testing.sql: ese stub no tiene section_id
-- ni created_at. Usar un esquema separado; este script no altera tablas ajenas.

INSERT INTO academic_period (id, name, year, start_date, end_date, status)
VALUES (1, '2026-1', 2026, '2026-01-15', '2026-06-30', 'ACTIVE');

INSERT INTO teachers (id, teacher_code, teacher_name, email, status)
VALUES (1, 'DOC-01', 'Ana Morales', 'ana.morales@example.test', 'ACTIVE'),
       (2, 'DOC-02', 'Luis Carrera', 'luis.carrera@example.test', 'ACTIVE');

-- A1 aun no publica Course: se conservan los IDs, sin inventar codigos/nombres.
INSERT INTO section (id, academic_period_id, course_id, teacher_id, section_code, status)
VALUES (10, 1, 5, 1, 'A', 'ACTIVE'),
       (11, 1, 5, 2, 'B', 'ACTIVE'),
       (12, 1, 6, 1, 'A', 'ACTIVE');

INSERT INTO enrollment (id, section_id, student_id, enrollment_date, status)
VALUES (1, 10, 101, '2026-01-20', 'ACTIVE'),
       (2, 10, 102, '2026-01-20', 'ACTIVE'),
       (3, 11, 103, '2026-01-21', 'ACTIVE');

-- Stub temporal de C3. Se consume cuando se integre el puerto JDBC de Allan.
CREATE TABLE IF NOT EXISTS alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    section_id BIGINT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP
);

INSERT INTO alert (id, section_id, status, created_at)
VALUES (1, 10, 'NEW', '2026-09-28 10:00:00'),
       (2, 10, 'IN_PROGRESS', '2026-09-29 09:30:00'),
       (3, 11, 'RESOLVED', '2026-09-20 15:00:00');
