package gt.edu.uinsight.academicperiod.entity;

/**
 * Estados de un periodo academico. RN-06: las unicas transiciones validas son
 * PLANNED -> ACTIVE y ACTIVE -> CLOSED. No se puede saltar estados ni reabrir un
 * periodo cerrado. La regla vive en el enum para que no pueda evadirse desde otro
 * punto del codigo.
 */
public enum PeriodStatus {
    PLANNED,
    ACTIVE,
    CLOSED;

    public boolean canTransitionTo(PeriodStatus target) {
        return switch (this) {
            case PLANNED -> target == ACTIVE;
            case ACTIVE -> target == CLOSED;
            case CLOSED -> false;
        };
    }
}
