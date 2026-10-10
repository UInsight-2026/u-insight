package gt.edu.uinsight.student.exception;

public class InactiveStudentException extends RuntimeException {

    public InactiveStudentException(Long studentId) {
        super("El estudiante " + studentId + " está inactivo y no puede inscribirse");
    }
}