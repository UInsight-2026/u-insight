package gt.edu.uinsight.exception;

public class AnalizadorTendencias {

    public double calcularTendenciaLineal(double[] datos) {
        if (datos == null) {
            throw new TrendCalculationException("No se puede calcular la tendencia: el arreglo de datos es nulo.");
        }

        if (datos.length < 2) {
            throw new TrendCalculationException("Se requieren al menos 2 puntos de datos para calcular una tendencia.");
        }

        try {
            return (datos[datos.length - 1] - datos[0]) / datos.length;
        } catch (ArithmeticException e) {
            throw new TrendCalculationException("Error aritmetico durante el calculo de la tendencia.", e);
        }
    }
}