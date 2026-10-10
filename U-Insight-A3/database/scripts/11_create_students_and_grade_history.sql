-- Sección A, Célula A3 — Gestión de estudiantes.
-- `grades` es la tabla compartida con A6 para que el historial académico
-- y la captura de calificaciones usen la misma fuente de datos.

CREATE TABLE IF NOT EXISTS students (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_code  VARCHAR(30)  NOT NULL,
    student_name  VARCHAR(120) NOT NULL,
    email         VARCHAR(254) NULL,
    status        VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT uq_students_student_code UNIQUE (student_code),
    CONSTRAINT chk_students_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS grades (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    evaluation_id  BIGINT        NOT NULL,
    student_id     BIGINT        NOT NULL,
    score          DECIMAL(8, 2) NOT NULL,
    registered_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status         VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT uq_grade_student_evaluation UNIQUE (student_id, evaluation_id),
    CONSTRAINT fk_grade_student
        FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE RESTRICT,
    CONSTRAINT chk_grade_score CHECK (score >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_grades_student_registered
    ON grades (student_id, registered_at);