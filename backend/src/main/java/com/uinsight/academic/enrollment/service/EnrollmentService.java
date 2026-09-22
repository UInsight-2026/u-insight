package com.uinsight.academic.enrollment.service;

import com.uinsight.academic.enrollment.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {

    Enrollment enrollStudent(Long studentId, Long sectionId);

    List<Enrollment> getAllEnrollments();

    List<Enrollment> getEnrollmentsByStudent(Long studentId);

    List<Enrollment> getEnrollmentsBySection(Long sectionId);

    void cancelEnrollment(Long enrollmentId);
}