package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // Find enrollments by student ID
    List<Enrollment> findByStudentId(Long studentId);

    // Find enrollments by course ID
    List<Enrollment> findByCourseId(Long courseId);

    // Find enrollment by student ID and course ID
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    // Find active enrollments by student ID
    List<Enrollment> findByStudentIdAndIsActiveTrue(Long studentId);
    
    // Find active enrollments by course ID
    List<Enrollment> findByCourseIdAndIsActiveTrue(Long courseId);
    
    // Find enrollments by student username
    @Query("SELECT e FROM Enrollment e WHERE e.student.username = :username")
    List<Enrollment> findByStudentUsername(@Param("username") String username);
    
    // Find enrollments by course instructor ID
    @Query("SELECT e FROM Enrollment e WHERE e.course.instructor.id = :instructorId")
    List<Enrollment> findByCourseInstructorId(@Param("instructorId") Long instructorId);
    
    // Find enrollments after a specific date
    List<Enrollment> findByEnrollmentDateAfter(LocalDateTime date);
    
    // Count enrollments by course
    Long countByCourseId(Long courseId);
    
    // Count active enrollments by course
    Long countByCourseIdAndIsActiveTrue(Long courseId);
}
