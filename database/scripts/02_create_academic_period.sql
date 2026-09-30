-- =====================================================================
-- Celula A1 - Tabla academic_period (contrato oficial del proyecto)
-- Responsable: Irvin Jose Gonzalez Mateo (0900-24-24648)
--
-- Sin llaves foraneas: las FK hacia academic_period(id) las declara la
-- celula duena de la tabla que las necesita.
-- No renombrar ni cambiar el tipo de id, name ni status: otras celulas
-- dependen de ellas.
-- =====================================================================

CREATE TABLE IF NOT EXISTS academic_period (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    `year` INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
