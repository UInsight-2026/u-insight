-- =====================================================================
-- Celula A1 - Datos semilla de academic_period
-- Inserta UNICAMENTE en academic_period. Exactamente un periodo ACTIVE (RN-03).
-- =====================================================================

INSERT INTO academic_period (name, `year`, start_date, end_date, status) VALUES
('Segundo Semestre', 2025, '2025-07-07', '2025-11-29', 'CLOSED'),
('Primer Semestre',  2026, '2026-01-12', '2026-05-30', 'ACTIVE'),
('Segundo Semestre', 2026, '2026-07-06', '2026-11-28', 'PLANNED');
