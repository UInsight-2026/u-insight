package gt.edu.uinsight.alert.b7.model;

public class AlertRule {
    private Long id;
    private String nombre;
    private String conditionExp;
    private String severidad;
    private boolean active;


    public AlertRule(){}
    
    public AlertRule(Long id, String nombre, String conditionExp, String severidad, boolean active){
        this.id = id;
        this.nombre = nombre;
        this.conditionExp = conditionExp;
        this.severidad = severidad;
        this.active = active;
    }

    public Long getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public String getConditionExp(){
        return conditionExp;
    }

    public String getSeveridad(){
        return severidad;
    }

    public boolean active(){
        return active;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setConditionExp(String conditionExp){
        this.conditionExp = conditionExp;
    }

    public void setSeveridad(String severidad){
        this.severidad = severidad;
    }

    public void setActive(boolean active){
        this.active = active;
    }
}
