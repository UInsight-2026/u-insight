package gt.edu.uinsight.analytics.trend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "section")
public class Section {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    Long periodId, courseId, teacherId;
    String sectionCode;
    String status;

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

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    

    
}
