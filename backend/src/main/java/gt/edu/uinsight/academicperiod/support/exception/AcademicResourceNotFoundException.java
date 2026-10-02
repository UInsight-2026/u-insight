package gt.edu.uinsight.academicperiod.support.exception;

/**
 * Recurso de la celula A1 (periodo academico o curso) que no existe. Se traduce a 404.
 */
public class AcademicResourceNotFoundException extends RuntimeException {

    public AcademicResourceNotFoundException(String message) {
        super(message);
    }
}
