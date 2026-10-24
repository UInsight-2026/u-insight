package gt.edu.uinsight.alert.model;

/**
 * Estados del ciclo de vida de una alerta (Celula C3).
 *
 * Transiciones permitidas (sin saltos ni retrocesos, segun la guia del proyecto):
 * NEW -> UNDER_REVIEW -> IN_PROGRESS -> RESOLVED
 * NEW, UNDER_REVIEW, IN_PROGRESS -> DISMISSED
 *
 * RESOLVED y DISMISSED son estados terminales.
 */
public enum AlertStatus {
    NEW,
    UNDER_REVIEW,
    IN_PROGRESS,
    RESOLVED,
    DISMISSED
}
