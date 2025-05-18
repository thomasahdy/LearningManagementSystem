package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Enrollment;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.repository.CourseRepository;
import com.lms.LearningManagementSystem.repository.EnrollmentRepository;
import com.lms.LearningManagementSystem.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final NotificationService notificationService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository,
                             NotificationService notificationService) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @PreAuthorize("hasRole('STUDENT') and #studentId == authentication.principal.id or " +
            "hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfAnyEnrolledCourse(authentication.principal, #studentId) or " +
            "hasRole('ADMIN')")
    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal, #courseId) or " +
            "hasRole('ADMIN')")
    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @PreAuthorize("hasRole('STUDENT') and #studentId == authentication.principal.id")
    public Enrollment enrollStudentInCourse(Long studentId, Long courseId) {
        if (enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        String subject = "Course Enrollment";

        String studentMessage = "You have been successfully enrolled in the course: " + course.getTitle();
        notificationService.createNotification(student, subject, studentMessage);

        User instructor = course.getInstructor();
        String instructorMessage = "New student " + student.getUsername() + " has enrolled in your course: " + course.getTitle();
        notificationService.createNotification(instructor, subject, instructorMessage);

        return savedEnrollment;
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal, #courseId) or " +
            "hasRole('ADMIN')")
    public void unenrollStudent(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));
        enrollmentRepository.delete(enrollment);
    }
}
