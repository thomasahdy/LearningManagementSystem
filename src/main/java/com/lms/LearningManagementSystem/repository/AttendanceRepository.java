package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByCourseIdAndStudentId(Long courseId, Long studentId); 
    List<Attendance> findByCourseId(Long courseId);
}
