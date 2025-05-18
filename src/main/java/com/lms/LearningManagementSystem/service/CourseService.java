package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.exception.CourseNotFoundException;
import com.lms.LearningManagementSystem.exception.FileStorageException;
import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.Lesson;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.repository.CourseRepository;
import com.lms.LearningManagementSystem.repository.LessonRepository;
import com.lms.LearningManagementSystem.repository.UserRepository;
import com.lms.LearningManagementSystem.repository.AssignmentRepository;
import com.lms.LearningManagementSystem.model.Assignment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final AssignmentRepository assignmentRepository;

    private static final String COURSE_NOT_FOUND = "Course not found";
    private static final Random RANDOM = new Random();

    @Value("${media.storage.path}")
    private String mediaStoragePath;

    public CourseService(
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            AssignmentRepository assignmentRepository,
            UserService userService) {
            this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or hasRole('ADMIN')")
    public Course createCourse(Course course) {
        User instructor = userRepository.findByUsername(course.getInstructor().getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
        course.setInstructor(instructor);
        return courseRepository.save(course);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or hasRole('ADMIN')")
    public Course updateCourse(Long courseId, Course course) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(COURSE_NOT_FOUND));

        course.setId(courseId);
        course.setInstructor(existingCourse.getInstructor());
        course.setEnrolledStudents(existingCourse.getEnrolledStudents());

        String subject = "Course Update";
        String message = "Course has been updated";
        for (String studentName : course.getEnrolledStudents()) {
            notificationService.createNotification(userService.findByUsername(studentName), subject, message);
        }

        return courseRepository.save(course);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId) or hasRole('ADMIN')")
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(COURSE_NOT_FOUND);
        }
        courseRepository.deleteById(courseId);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public List<Course> getCoursesByInstructor(String instructorUsername) {
        return courseRepository.findByInstructorUsername(instructorUsername);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public List<Course> getEnrolledCourses(String username) {
        return courseRepository.findByEnrolledStudentsContaining(username);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public void enrollStudent(Long courseId, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(COURSE_NOT_FOUND));

        if (course.getEnrolledStudents().contains(username)) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        course.getEnrolledStudents().add(username);
        courseRepository.save(course);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public void unenrollStudent(Long courseId, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(COURSE_NOT_FOUND));

        if (!course.getEnrolledStudents().contains(username)) {
            throw new IllegalArgumentException("Student is not enrolled in this course");
        }

        course.getEnrolledStudents().remove(username);
        courseRepository.save(course);
    }

    public Course uploadMediaFiles(Long courseId, List<MultipartFile> files) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(COURSE_NOT_FOUND));

        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            String filePath = saveFile(file);
            filePaths.add(filePath);
        }

        course.getMediaFiles().addAll(filePaths);
        return courseRepository.save(course);
    }


    private String saveFile(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(mediaStoragePath, fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
            return filePath.toString();
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }


    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId)")
    public Lesson generateOtp(Long courseId, Long lessonId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(COURSE_NOT_FOUND));

        Lesson lesson = course.getLessons().stream()
                .filter(l -> l.getId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        lesson.setOtp(otp);

        courseRepository.save(course);
        return lesson;
    }

    @PreAuthorize("hasRole('STUDENT') and @courseSecurityService.isEnrolledInCourse(authentication.principal.username, #courseId)")
    public boolean validateOtp(Long courseId, Long lessonId, String otp) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(COURSE_NOT_FOUND));

        Lesson lesson = course.getLessons().stream()
                .filter(l -> l.getId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        return otp.equals(lesson.getOtp());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal, #courseId)")
    public List<String> getEnrolledStudents(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(COURSE_NOT_FOUND));
        return new ArrayList<>(course.getEnrolledStudents());
    }

    public Course addLessonsToCourse(Long courseId, List<Lesson> lessons) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(COURSE_NOT_FOUND));

        lessons.forEach(lesson -> {
            lesson.setCourse(course);
            lessonRepository.save(lesson);
        });

        course.getLessons().addAll(lessons);
        return courseRepository.save(course);
    }
        
        @PreAuthorize("hasRole('INSTRUCTOR')")
        public Assignment createAssignment(Long courseId, Assignment assignment) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(COURSE_NOT_FOUND));
        assignment.setCourse(course);
        return assignmentRepository.save(assignment);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public List<Assignment> getAssignments(Long courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public Assignment gradeAssignment(Long assignmentId, double grade) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        assignment.setGrade(grade);
        return assignmentRepository.save(assignment);
}

}
