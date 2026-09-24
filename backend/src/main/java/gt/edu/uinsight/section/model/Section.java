package gt.edu.uinsight.section.model;

import gt.edu.uinsight.teacher.model.Teacher;
import jakarta.persistence.*;

@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "section_code", nullable = false, unique = true, length = 20)
    private String sectionCode;

    @Column(name = "course_name", nullable = false, length = 120)
    private String courseName;

    /** Ciclo o periodo academico de la seccion, por ejemplo 2026-1. */
    @Column(name = "academic_term", nullable = false, length = 20)
    private String academicTerm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    public Section() {}

    public Section(String sectionCode, String courseName, String academicTerm, Teacher teacher) {
        this.sectionCode = sectionCode;
        this.courseName = courseName;
        this.academicTerm = academicTerm;
        this.teacher = teacher;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getAcademicTerm() { return academicTerm; }
    public void setAcademicTerm(String academicTerm) { this.academicTerm = academicTerm; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}
