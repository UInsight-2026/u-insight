package gt.edu.uinsight.analytics.dispersion.exception;
public class CursoNoEncontradoException extends RuntimeException {
    public CursoNoEncontradoException(Long courseId) {
        super("No se encontró el curso con id " + courseId + ".");
