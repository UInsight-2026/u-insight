package gt.edu.uinsight.alert.b7.model;

import java.util.HashMap;
import java.util.Map;

public class RiskInput {
    private double media;
    private double mediana;
    private double desviacion;
    private double tendencia;
    private double percentil90;

    public RiskInput(){
    }
    
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

    public Map<String, Double> toMap(){
        Map<String, Double> map = new HashMap<>();
        map.put("media", media);
        map.put("mediana", mediana);
        map.put("desviacion", desviacion);
        map.put("tendencia", tendencia);
        map.put("percentil90", percentil90);
        return map;
    }
}