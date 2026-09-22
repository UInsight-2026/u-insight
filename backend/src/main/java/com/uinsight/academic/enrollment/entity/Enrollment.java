package com.uinsight.academic.enrollment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDateTime enrollmentDate;

    @Column(name = "status", nullable = false)
    private String status; // Ej: "ACTIVE", "CANCELLED"

    @PrePersist
    public void prePersist() {
        this.enrollmentDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }
}