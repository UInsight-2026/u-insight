package gt.edu.uinsight.student.exception;

public class DuplicateStudentCodeException extends RuntimeException {

    public DuplicateStudentCodeException(String studentCode) {
        super("Ya existe un estudiante con el código " + studentCode);
    }
}