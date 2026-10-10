package gt.edu.uinsight.course.entity;

/**
 * Estados de un curso. La baja es logica (RN-05): un curso pasa a INACTIVE en lugar
 * de borrarse, y puede volver a ACTIVE. Un curso INACTIVE no debe ofrecerse para
 * nuevas secciones (RN-09); la celula de secciones decide con este estado.
 */
public enum CourseStatus {
    ACTIVE,
    INACTIVE;

    public boolean canTransitionTo(CourseStatus target) {
        return target != null && target != this;
    }
}
