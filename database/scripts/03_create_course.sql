-- =====================================================================
-- Celula A1 - Tabla course (contrato oficial del proyecto)
-- Responsable: Irvin Jose Gonzalez Mateo (0900-24-24648)
--
-- Sin llaves foraneas: las FK hacia course(id) las declara la celula
-- duena de la tabla que las necesita.
-- No renombrar ni cambiar el tipo de id, code, name ni status: otras
-- celulas dependen de ellas.
-- =====================================================================

CREATE TABLE IF NOT EXISTS course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    credits INT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_course_code UNIQUE (code)
);
