package gt.edu.uinsight.student.exception;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Object identifier) {
        super("No se encontró el estudiante: " + identifier);
    }
}