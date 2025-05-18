package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByCourseId(Long courseId);
}