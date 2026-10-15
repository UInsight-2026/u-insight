package gt.edu.uinsight.student.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "student")
public class StudentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_code")
    private String studentCode;

    protected StudentRecord() {
    }

    public StudentRecord(String studentCode) {
        this.studentCode = studentCode;
    }

    public Long getId() {
        return id;
    }

    public String getStudentCode() {
        return studentCode;
    }

}
