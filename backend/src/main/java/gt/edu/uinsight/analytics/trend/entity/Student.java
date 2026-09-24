package gt.edu.uinsight.analytics.trend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Parche de arranque aportado por C7: la clase estaba vacia y sin @Entity, pero
// StudentRepository la declara como tipo de JpaRepository, asi que Spring fallaba con
// "Not a managed type: Student" y el contexto no levantaba. Se le da el minimo para ser
// una entidad valida. Ni la entidad ni StudentRepository se usan en ningun servicio.
// Pendiente: la celula B4 decide si modela la entidad de verdad o elimina el repositorio.
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
