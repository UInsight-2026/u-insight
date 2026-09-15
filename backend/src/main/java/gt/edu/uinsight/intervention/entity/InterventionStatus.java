package gt.edu.uinsight.intervention.entity;

/**
 * Estado del ciclo de vida de una intervención.
 *
 * RN-4 (aplicada por InterventionService en semana 3, PATCH /status): transiciones
 * válidas PLANNED -> IN_PROGRESS -> COMPLETED, y PLANNED/IN_PROGRESS -> CANCELLED.
 * Sin saltos ni retrocesos.
 */
public enum InterventionStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
