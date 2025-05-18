package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.Lesson;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.service.CourseService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService ) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

@PostMapping("/{id}")
@PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #id)")
public ResponseEntity<Course> createCourse(@RequestBody Course course, @PathVariable Long id) {
    User instructor = userService.findById(id);
    course.setInstructor(instructor);
    Course createdCourse = courseService.createCourse(course);
    return ResponseEntity.ok(createdCourse);
}

        course.setInstructor(instructor);
        Course createdCourse = courseService.createCourse(course);
        return ResponseEntity.ok(createdCourse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #id)")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            Course updatedCourse = courseService.updateCourse(id, course);
            return ResponseEntity.ok(updatedCourse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #id) or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/instructor")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<Course>> getInstructorCourses(Principal principal) {
        return ResponseEntity.ok(courseService.getCoursesByInstructor(principal.getName()));
    }

    @GetMapping("/enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Course> getEnrolledCourses(Principal principal) {
        return courseService.getEnrolledCourses(principal.getName());
    }

    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> enrollInCourse(@PathVariable Long id, Principal principal) {
        try {
            courseService.enrollStudent(id, principal.getName());
            return ResponseEntity.ok("Enrolled successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/unenroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> unenrollFromCourse(@PathVariable Long id, Principal principal) {
        try {
            courseService.unenrollStudent(id, principal.getName());
            return ResponseEntity.ok("Unenrolled successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{courseId}/media")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId)")
    public ResponseEntity<Course> uploadMediaFiles(
            @PathVariable Long courseId,
            @RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(courseService.uploadMediaFiles(courseId, files));
    }

    @GetMapping("/media/{fileName}")
    public ResponseEntity<Resource> serveMediaFile(@PathVariable String fileName) {
        Path filePath = Paths.get("/MediaStorage", fileName);
        Resource resource = new FileSystemResource(filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @PostMapping("/{courseId}/lessons/{lessonId}/generate-otp")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Lesson> generateOtp(@PathVariable Long courseId, @PathVariable Long lessonId) {
        return ResponseEntity.ok(courseService.generateOtp(courseId, lessonId));
    }

    @PostMapping("/{courseId}/lessons/{lessonId}/validate-otp")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> validateOtp(@PathVariable Long courseId, @PathVariable Long lessonId, @RequestBody String otp) {
        return ResponseEntity.ok(courseService.validateOtp(courseId, lessonId, otp));
    }

    @GetMapping("/{courseId}/students")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #id) or hasRole('ADMIN')")
    public ResponseEntity<List<String>> getEnrolledStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getEnrolledStudents(courseId));
    }

    @PostMapping("/{courseId}/lessons")
    @PreAuthorize("hasRole('INSTRUCTOR') and @courseSecurityService.isInstructorOfCourse(authentication.principal.username, #courseId)")
    public ResponseEntity<Course> addLessonsToCourse(@PathVariable Long courseId, @RequestBody List<Lesson> lessons) {
        return ResponseEntity.ok(courseService.addLessonsToCourse(courseId, lessons));
    }
}
