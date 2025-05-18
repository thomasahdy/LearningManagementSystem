package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find users by role
    List<User> findByRole(User.Role role);
    
    // Find users whose usernames contain the given string
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    // Find users whose first names contain the given string
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    
    // Find users whose last names contain the given string
    List<User> findByLastNameContainingIgnoreCase(String lastName);
    
    // Find users by role with pagination
    Page<User> findByRole(User.Role role, Pageable pageable);
    
    // Find users created after a specific date
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    // Find users with names (first or last) matching a pattern
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Find users enrolled in a specific course
    @Query("SELECT u FROM User u JOIN u.enrolledCourses c WHERE c.id = :courseId")
    List<User> findUsersByCourseId(@Param("courseId") Long courseId);
    
    // Find instructors teaching any course
    @Query("SELECT DISTINCT u FROM User u WHERE u.role = 'INSTRUCTOR' AND SIZE(u.coursesTaught) > 0")
    List<User> findActiveInstructors();
    
    // Find students not enrolled in any course
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND SIZE(u.enrolledCourses) = 0")
    List<User> findStudentsNotEnrolledInAnyCourse();
    
    // Count users by role
    Long countByRole(User.Role role);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
}
