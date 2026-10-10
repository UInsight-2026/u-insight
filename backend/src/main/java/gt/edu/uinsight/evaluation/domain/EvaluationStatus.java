// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.domain;

import java.util.EnumSet;
import java.util.Set;

/**
 * Ciclo de vida de una evaluación (RN6):
 * DRAFT -> ACTIVE -> CLOSED, o DRAFT/ACTIVE -> CANCELLED.
 * CLOSED y CANCELLED son estados finales.
 */
public enum EvaluationStatus {
    DRAFT, ACTIVE, CLOSED, CANCELLED;

    /** Expresión regular usada por Bean Validation en ChangeEvaluationStatusRequest. */
    public static final String REGEX = "(?i)DRAFT|ACTIVE|CLOSED|CANCELLED";

    /** Estados a los que se puede pasar desde el estado actual (RN6). */
    public Set<EvaluationStatus> allowedNext() {
        return switch (this) {
            case DRAFT -> EnumSet.of(ACTIVE, CANCELLED);
            case ACTIVE -> EnumSet.of(CLOSED, CANCELLED);
            case CLOSED, CANCELLED -> EnumSet.noneOf(EvaluationStatus.class);
        };
    }

    public boolean canTransitionTo(EvaluationStatus next) {
        return allowedNext().contains(next);
    }

    /** HU3: solo se pueden editar los datos en DRAFT o ACTIVE. */
    public boolean isEditable() {
        return this == DRAFT || this == ACTIVE;
    }
}
