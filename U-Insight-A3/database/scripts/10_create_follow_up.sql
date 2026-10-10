-- Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
-- Script: 10_create_follow_up.sql
-- Módulo: C4 - Intervenciones y Seguimiento
-- Responsabilidad: crear la tabla follow_up.
--
-- El código Java de este módulo (entity/service/controller de FollowUp) es de la
-- semana 3, pero el esquema se crea ahora para no bloquear al resto del equipo que
-- dependa del modelo de datos completo.

CREATE TABLE IF NOT EXISTS follow_up (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    intervention_id   BIGINT NOT NULL,
    follow_up_date    DATE NOT NULL,
    observation       VARCHAR(500) NOT NULL,
    result            VARCHAR(20) NULL,
    deleted           TINYINT(1) NOT NULL DEFAULT 0,

    CONSTRAINT fk_follow_up_intervention
        FOREIGN KEY (intervention_id) REFERENCES intervention (id),

    CONSTRAINT chk_follow_up_result
        CHECK (result IS NULL OR result IN ('IMPROVED', 'NO_CHANGE', 'WORSENED', 'PENDING'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_follow_up_intervention_id ON follow_up (intervention_id);
