package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    // Find resources by course ID
    List<Resource> findByCourseId(Long courseId);
    
    // Find resources by type
    List<Resource> findByType(String type);
    
    // Find resources by name containing keyword
    List<Resource> findByNameContainingIgnoreCase(String keyword);
    
    // Find resources by course ID and type
    List<Resource> findByCourseIdAndType(Long courseId, String type);
    
    // Find paginated resources by course ID
    Page<Resource> findByCourseId(Long courseId, Pageable pageable);
    
    // Find resources by URL containing a specific path
    List<Resource> findByUrlContaining(String path);
    
    // Count resources by course ID
    Long countByCourseId(Long courseId);
    
    // Count resources by type
    Long countByType(String type);
    
    // Find resources by course instructor username
    @Query("SELECT r FROM Resource r WHERE r.course.instructor.username = :username")
    List<Resource> findByCourseInstructorUsername(@Param("username") String username);
    
    // Find the largest resources (by size) for a course
    @Query("SELECT r FROM Resource r WHERE r.course.id = :courseId ORDER BY r.size DESC")
    List<Resource> findLargestResourcesByCourseId(@Param("courseId") Long courseId, Pageable pageable);
    
    // Calculate total size of resources for a course
    @Query("SELECT SUM(r.size) FROM Resource r WHERE r.course.id = :courseId")
    Long calculateTotalResourceSizeByCourse(@Param("courseId") Long courseId);
    
    // Delete resources by course ID
    void deleteByCourseId(Long courseId);
}