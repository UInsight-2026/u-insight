package gt.edu.uinsight.teacher.model;

import jakarta.persistence.*;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_code", nullable = false, unique = true, length = 20)
    private String teacherCode;

    @Column(name = "teacher_name", nullable = false, length = 120)
    private String teacherName;

    /** El correo es opcional; cuando se envia debe tener formato valido. */
    @Column(name = "email", length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TeacherStatus status = TeacherStatus.ACTIVE;

    public Teacher() {}

    public Teacher(String teacherCode, String teacherName, String email, TeacherStatus status) {
        this.teacherCode = teacherCode;
        this.teacherName = teacherName;
        this.email = email;
        this.status = status != null ? status : TeacherStatus.ACTIVE;
    }

    public boolean isActive() {
        return TeacherStatus.ACTIVE.equals(this.status);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTeacherCode() { return teacherCode; }
    public void setTeacherCode(String teacherCode) { this.teacherCode = teacherCode; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public TeacherStatus getStatus() { return status; }
    public void setStatus(TeacherStatus status) { this.status = status; }
}
