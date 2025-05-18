package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    // Find lessons by course ID
    List<Lesson> findByCourseId(Long courseId);
    
    // Find lesson by name and course ID
    Optional<Lesson> findByNameAndCourseId(String name, Long courseId);
    
    // Find lesson by OTP
    Optional<Lesson> findByOtp(String otp);
    
    // Find lessons by name containing the keyword
    List<Lesson> findByNameContainingIgnoreCase(String keyword);
    
    // Count lessons by course ID
    Long countByCourseId(Long courseId);
    
    // Find lessons by course instructor username
    @Query("SELECT l FROM Lesson l WHERE l.course.instructor.username = :username")
    List<Lesson> findByCourseInstructorUsername(@Param("username") String username);
    
    // Find lessons by course ID, ordered by ID
    List<Lesson> findByCourseIdOrderById(Long courseId);
    
    // Delete lessons by course ID
    void deleteByCourseId(Long courseId);
}
