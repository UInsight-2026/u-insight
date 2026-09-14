package gt.edu.uinsight.alert.b7.model;

public class RiskInput {
    private double media;
    private double mediana;
    private double desviacion;
    private double tendencia;
    private double percentil90;

    public double getMedia(){
        return media;
    }

    public double getMediana(){
        return mediana;
    }

    public double getDesviacion(){
        return desviacion;
    }

    public double getTendencia(){
        return tendencia;
    }

    public double getPercentil90(){
        return percentil90;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    public void setMediana(double mediana) {
        this.mediana = mediana;
    }

    public void setDesviacion(double desviacion) {
        this.desviacion = desviacion;
    }

    public void setTendencia(double tendencia) {
        this.tendencia = tendencia;
    }

    public void setPercentil90(double percentil90) {
        this.percentil90 = percentil90;
    }

    public static RiskInput pruebas(){
        RiskInput r = new RiskInput();
        r.media = 12.5;
        r.mediana = 10.0;
        r.desviacion = 4.2;
        r.tendencia = 1.8;
        r.percentil90 = 22.0;
        return r;
    }
}