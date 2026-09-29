package gt.edu.uinsight.analytics.dispersion.dto.response;

/**
 * Clasificación del nivel de dispersión de las calificaciones.
 * Los límites que determinan cada categoría son configurables
 * y se definen en la capa de servicio/calculator (no aquí).
 */
public enum DispersionClassification {
    LOW_DISPERSION,
    MODERATE_DISPERSION,
    HIGH_DISPERSION
}
