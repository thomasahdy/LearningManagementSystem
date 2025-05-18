package com.lms.LearningManagementSystem.repository;
import com.lms.LearningManagementSystem.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // Find assignments by course ID
    List<Assignment> findByCourseId(Long courseId);
    
    // Find assignments by student
    List<Assignment> findByStudentUsername(String username);
    
    // Find graded assignments
    List<Assignment> findByGradeIsNotNull();
    
    // Find ungraded assignments
    List<Assignment> findByGradeIsNull();
    
    // Find assignments with grades higher than a threshold
    List<Assignment> findByGradeGreaterThanEqual(Double minGrade);
    
    // Find assignments by course ID and student username
    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId AND a.student.username = :username")
    List<Assignment> findByCourseIdAndStudentUsername(@Param("courseId") Long courseId, @Param("username") String username);
    
    // Count assignments by course
    Long countByCourseId(Long courseId);
}
