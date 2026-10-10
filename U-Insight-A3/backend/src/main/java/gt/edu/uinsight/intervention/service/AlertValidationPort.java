// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.service;

/**
 * Puerto que aísla al módulo de intervenciones de la forma concreta en que se
 * valida la existencia y el estado de una alerta (dominio de la célula C3).
 *
 * Implementación actual: {@link AlertValidationJdbcAdapter} (temporal, ver su
 * javadoc). En semana 3 (tarea BKL-07) se reemplaza por un cliente HTTP real
 * hacia la API de C3, sin tocar a los consumidores de este puerto.
 */
public interface AlertValidationPort {

    /**
     * RN-1: indica si existe una alerta con el id dado.
     */
    boolean exists(Long alertId);

    /**
     * RN-2: indica si la alerta está activa (no está RESOLVED ni DISMISSED).
     * El resultado con una alerta inexistente no está definido; llamar solo
     * después de confirmar {@link #exists(Long)}.
     */
    boolean isActive(Long alertId);
}
