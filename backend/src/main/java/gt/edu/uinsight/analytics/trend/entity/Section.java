package gt.edu.uinsight.analytics.trend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Vista mínima de la sección necesaria para validar las consultas de B4. */
@Entity(name = "TrendSection")
@Table(name = "section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "academic_period_id")
    private Long periodId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "section_code")
    private String sectionCode;

    private String status;

    // Constructor sin argumentos agregado por C7: Hibernate lo necesita para instanciar
    // la entidad y sin el el contexto de Spring no arranca.
    protected Section() {
    }

    public Section(Long periodId, Long courseId, Long teacherId, String sectionCode, String status) {
        this.periodId = periodId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.sectionCode = sectionCode;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getStatus() {
        return status;
    }
}
