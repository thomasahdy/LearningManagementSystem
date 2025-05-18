package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.model.Enrollment;
import com.lms.LearningManagementSystem.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT') and #studentId == authentication.principal.id or " +
            "hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfAnyEnrolledCourse(authentication.principal.username, #studentId) or " +
            "hasRole('ADMIN')")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudentId(studentId));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or " +
            "hasRole('ADMIN')")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourseId(courseId));
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Enrollment> enrollStudentInCourse(Authentication authentication, @RequestParam Long courseId) {
        try {
            Long studentId = Long.parseLong(authentication.getName());
            Enrollment enrollment = enrollmentService.enrollStudentInCourse(studentId, courseId);
            return ResponseEntity.ok(enrollment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/unenroll")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or " +
            "hasRole('ADMIN')")
    public ResponseEntity<Void> unenrollStudent(@RequestParam Long studentId, @RequestParam Long courseId) {
        try {
            enrollmentService.unenrollStudent(studentId, courseId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
