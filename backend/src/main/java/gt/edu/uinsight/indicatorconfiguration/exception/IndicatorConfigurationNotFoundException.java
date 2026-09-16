package gt.edu.uinsight.indicatorconfiguration.exception;

public class IndicatorConfigurationNotFoundException extends RuntimeException {

    public IndicatorConfigurationNotFoundException(String key) {
        super("No existe una configuracion de indicador con la clave: " + key);
    }
}
