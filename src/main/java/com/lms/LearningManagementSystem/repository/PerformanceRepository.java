package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    List<Performance> findByEnrollmentId(Long enrollmentId);


    List<Performance> findByQuizScoreGreaterThan(Double score);


    List<Performance> findByEnrollmentIdAndAttendanceMarkedTrue(Long enrollmentId);
    
    List<Performance> findByQuizScoreBetween(Double minScore, Double maxScore);
    
    List<Performance> findByAssignmentGradeBetween(Double minGrade, Double maxGrade);
    
    List<Performance> findByQuizScoreGreaterThanAndAssignmentGradeGreaterThan(Double minQuizScore, Double minAssignmentGrade);
    
    @Query("SELECT p FROM Performance p JOIN p.enrollment e WHERE e.course.id = :courseId")
    List<Performance> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT AVG(p.quizScore) FROM Performance p JOIN p.enrollment e WHERE e.course.id = :courseId")
    Double calculateAverageQuizScoreByCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT AVG(p.assignmentGrade) FROM Performance p JOIN p.enrollment e WHERE e.course.id = :courseId")
    Double calculateAverageAssignmentGradeByCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(p) FROM Performance p JOIN p.enrollment e WHERE e.course.id = :courseId AND p.attendanceMarked = true")
    Long countAttendancesByCourse(@Param("courseId") Long courseId);
}
