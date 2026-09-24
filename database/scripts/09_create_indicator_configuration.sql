CREATE TABLE IF NOT EXISTS indicator_configuration (
    id BIGINT NOT NULL AUTO_INCREMENT,
    config_key VARCHAR(80) NOT NULL,
    config_value VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_indicator_configuration PRIMARY KEY (id),
    CONSTRAINT uk_indicator_configuration_key UNIQUE (config_key)
);
