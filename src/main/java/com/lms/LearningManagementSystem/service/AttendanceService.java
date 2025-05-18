package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.exception.StudentNotFoundException;
import com.lms.LearningManagementSystem.model.Attendance;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.repository.AttendanceRepository;
import com.lms.LearningManagementSystem.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserService userService;

    public AttendanceService(AttendanceRepository attendanceRepository, UserService userService) {
        this.attendanceRepository = attendanceRepository;
        this.userService = userService;
    }

    public Attendance markAttendance(Long studentId, boolean present) {
        User student = userService.getUserById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setDate(LocalDate.now());
        attendance.setPresent(present);

        return attendanceRepository.save(attendance);
    }
}