package gt.edu.uinsight.grade.model;

import jakarta.persistence.*;

/**
 * Copia de referencia de una matricula, minima para validar que el
 * estudiante pertenece a la seccion de la evaluacion. La tabla se nombra
 * "grade_enrollment_ref" -- separada de la tabla "enrollments" que
 * implementara la celula A4 -- para no chocar con su esquema real.
 * Cuando A4 este integrada, este modulo deberia consultarla a ella en vez
 * de mantener esta copia.
 */
@Entity
@Table(
    name = "grade_enrollment_ref",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_enrollment_ref_student_section",
        columnNames = {"student_id", "section_id"}
    )
)
public class EnrollmentRef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    public EnrollmentRef() {}

    public EnrollmentRef(Long studentId, Long sectionId) {
        this.studentId = studentId;
        this.sectionId = sectionId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
}
