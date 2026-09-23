package gt.edu.uinsight.imports.entity;

/**
 * Ciclo de vida de una Importacion, según la cédula de la célula A7
 * (sección 5, Diseño de datos).
 */
public enum EstadoImportacion {
    PENDIENTE,
    VALIDADO,
    CONFIRMADO,
    PROCESADO,
    ERROR
}
