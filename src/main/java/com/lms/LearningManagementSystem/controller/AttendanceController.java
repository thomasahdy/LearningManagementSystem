package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.model.Attendance;
import com.lms.LearningManagementSystem.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestParam Long studentId, @RequestParam boolean present) {
        try {
            Attendance attendance = attendanceService.markAttendance(studentId, present);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Attendance marked successfully");
            response.put("attendance", attendance);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }
}