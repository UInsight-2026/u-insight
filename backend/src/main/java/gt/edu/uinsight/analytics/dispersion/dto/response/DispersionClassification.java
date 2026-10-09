
package gt.edu.uinsight.analytics.dispersion.dto.response;

/**
 * Clasificación del nivel de dispersión de las calificaciones.
 *
 * Los umbrales se definen mediante configuración externa
 * y son evaluados por la capa de servicio.
 */
public enum DispersionClassification {

    LOW_DISPERSION,

    MODERATE_DISPERSION,

    HIGH_DISPERSION
}
