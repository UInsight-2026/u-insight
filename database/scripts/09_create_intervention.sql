-- Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
-- Script: 09_create_intervention.sql
-- Módulo: C4 - Intervenciones y Seguimiento
-- Responsabilidad: crear la tabla intervention.
--
-- La FK alert_id referencia alert(id), tabla de la célula C3. Este script asume que
-- 08_*.sql (o el número que corresponda) ya creó `alert` antes de ejecutarse.

CREATE TABLE IF NOT EXISTS intervention (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_id      BIGINT NOT NULL,
    type          VARCHAR(30) NOT NULL,
    description   VARCHAR(500) NOT NULL,
    responsible   VARCHAR(150) NOT NULL,
    start_date    DATE NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT(1) NOT NULL DEFAULT 0,

    CONSTRAINT fk_intervention_alert
        FOREIGN KEY (alert_id) REFERENCES alert (id),

    CONSTRAINT chk_intervention_type
        CHECK (type IN ('TUTORING', 'MEETING', 'PARENT_CONTACT', 'ACADEMIC_PLAN', 'REFERRAL', 'OTHER')),

    CONSTRAINT chk_intervention_status
        CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_intervention_alert_id ON intervention (alert_id);
