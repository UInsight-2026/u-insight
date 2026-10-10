package gt.edu.uinsight.analytics.trend.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    @OneToMany (mappedBy = "student", fetch = FetchType.LAZY)
    private List<Grade> grades = new ArrayList<>();
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Grade> getGrades() {
        return grades;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 1ee7dac03082d19815951d48f50eb659e87dae52
