package gt.edu.uinsight.analytics.individual.exception;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Long studentId) {
        super("No se encontró el estudiante con id: " + studentId);
    }
}