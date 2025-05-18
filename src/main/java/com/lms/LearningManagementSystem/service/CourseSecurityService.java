package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.Enrollment;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.repository.CourseRepository;
import com.lms.LearningManagementSystem.repository.EnrollmentRepository;
import com.lms.LearningManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseSecurityService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseSecurityService(CourseRepository courseRepository,
                                 UserRepository userRepository,
                                 EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public boolean isInstructorOfCourse(String username, Long courseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return course.getInstructor() != null &&
                course.getInstructor().getId().equals(user.getId());
    }

    public boolean isEnrolledInCourse(String username, Long courseId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return course.getEnrolledStudents().contains(user.getUsername());
    }

    public boolean isInstructorOfAnyEnrolledCourse(String username, Long studentId) {
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentId(studentId);

        return studentEnrollments.stream()
                .map(Enrollment::getCourse)
                .anyMatch(course -> course.getInstructor() != null &&
                        course.getInstructor().getId().equals(instructor.getId()));
    }
}
