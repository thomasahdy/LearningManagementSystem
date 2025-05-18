package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE c.instructor.username = :username")
    List<Course> findByInstructorUsername(@Param("username") String instructorUsername);
    
    @Query("SELECT c FROM Course c WHERE :username MEMBER OF c.enrolledStudents")
    List<Course> findByEnrolledStudentsContaining(@Param("username") String username);
    
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId")
    List<Course> findByInstructorId(@Param("instructorId") Long instructorId);
}
