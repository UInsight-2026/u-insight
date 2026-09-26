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

    @Column(name = "academic_period_id", nullable = false)
    private Long periodId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(name = "section_code", nullable = false, length = 20)
    private String sectionCode;

    @Column(nullable = false, length = 20)
    private String status;

    protected Section() {
        // Requerido por JPA.
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
