package gt.edu.uinsight.analytics.dispersion.exception;
public class SeccionNoEncontradaException extends RuntimeException {
}
    public SeccionNoEncontradaException(Long sectionId) {
        super("No se encontró la sección con id " + sectionId + ".");
    }
