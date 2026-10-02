package gt.edu.uinsight.indicatorconfiguration.exception;

public class IndicatorConfigurationConflictException extends RuntimeException {

    public IndicatorConfigurationConflictException(String key) {
        super("Ya existe una configuracion de indicador con la clave: " + key);
    }
}
