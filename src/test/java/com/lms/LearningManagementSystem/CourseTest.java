package com.lms.LearningManagementSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.lms.LearningManagementSystem.controller.CourseController;
import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.service.CourseService;
import com.lms.LearningManagementSystem.service.UserService;

import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CourseTest {

    // Constants for duplicated strings
    private static final String INSTRUCTOR = "instructor";
    private static final String STUDENT = "student";
    private static final String COURSE_1 = "Course 1";
    private static final String JSON_PATH_TITLE = "$.title";
    private static final String JSON_PATH_ARRAY_TITLE = "$[0].title";
    private static final String API_COURSES_ID = "/api/courses/{id}";
    private static final String VIDEO_FILE = "video1.mp4";
    private static final String DOCUMENT_FILE = "document1.pdf";

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }

    @Test
    @WithMockUser(username = INSTRUCTOR, roles = {"INSTRUCTOR"})
    void createCourse() throws Exception {
        Course course = new Course();
        course.setTitle("New Course");
        User instructor = new User();
        instructor.setUsername(INSTRUCTOR);
        course.setInstructor(instructor);

        when(courseService.createCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"New Course\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_TITLE).value("New Course"));

        verify(courseService, times(1)).createCourse(any(Course.class));
    }

    @Test
    @WithMockUser(username = INSTRUCTOR, roles = {"INSTRUCTOR"})
    void updateCourse() throws Exception {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Updated Course");
        User instructor = new User();
        instructor.setUsername(INSTRUCTOR);
        course.setInstructor(instructor);

        when(courseService.updateCourse(eq(courseId), any(Course.class))).thenReturn(course);

        mockMvc.perform(put(API_COURSES_ID, courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Course\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_TITLE).value("Updated Course"));

        verify(courseService, times(1)).updateCourse(eq(courseId), any(Course.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCourse() throws Exception {
        Long courseId = 1L;

        doNothing().when(courseService).deleteCourse(courseId);

        mockMvc.perform(delete(API_COURSES_ID, courseId))
                .andExpect(status().isOk());

        verify(courseService, times(1)).deleteCourse(courseId);
    }

    @Test
    void getAllCourses() throws Exception {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle(COURSE_1);

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Course 2");

        when(courseService.getAllCourses()).thenReturn(Arrays.asList(course1, course2));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ARRAY_TITLE).value(COURSE_1))
                .andExpect(jsonPath("$[1].title").value("Course 2"));

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void getCourseById() throws Exception {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);
        course.setTitle(COURSE_1);

        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(course));

        mockMvc.perform(get(API_COURSES_ID, courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_TITLE).value(COURSE_1));

        verify(courseService, times(1)).getCourseById(courseId);
    }

    @Test
    void getInstructorCourses() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Instructor Course");

        when(courseService.getCoursesByInstructor(INSTRUCTOR))
                .thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/api/courses/instructor").principal(() -> INSTRUCTOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ARRAY_TITLE).value("Instructor Course"));

        verify(courseService, times(1)).getCoursesByInstructor(INSTRUCTOR);
    }

    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void getEnrolledCourses() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");

        when(courseService.getEnrolledCourses(STUDENT))
                .thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/api/courses/enrolled")
                        .principal(() -> STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ARRAY_TITLE).value("Test Course"));

        verify(courseService, times(1)).getEnrolledCourses(STUDENT);
    }


    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void enrollInCourse() throws Exception {
        Long courseId = 1L;

        doNothing().when(courseService).enrollStudent(courseId, STUDENT);

        mockMvc.perform(post("/api/courses/{id}/enroll", courseId)
                        .principal(() -> STUDENT))
                .andExpect(status().isOk());

        verify(courseService, times(1)).enrollStudent(courseId, STUDENT);
    }

    @Test
    @WithMockUser(username = STUDENT, roles = {"STUDENT"})
    void unenrollFromCourse() throws Exception {
        Long courseId = 1L;

        doNothing().when(courseService).unenrollStudent(courseId, STUDENT);

        mockMvc.perform(post("/api/courses/{id}/unenroll", courseId)
                        .principal(() -> STUDENT))
                .andExpect(status().isOk());

        verify(courseService, times(1)).unenrollStudent(courseId, STUDENT);
    }

    @Test
    void uploadMediaFiles() throws Exception {
        Long courseId = 1L;

        // Mock file data
        MockMultipartFile file1 = new MockMultipartFile("files", VIDEO_FILE, MediaType.MULTIPART_FORM_DATA_VALUE, "Dummy video content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", DOCUMENT_FILE, MediaType.MULTIPART_FORM_DATA_VALUE, "Dummy document content".getBytes());

        Course course = new Course();
        course.setId(courseId);
        course.setMediaFiles(Arrays.asList(VIDEO_FILE, DOCUMENT_FILE));

        when(courseService.uploadMediaFiles(eq(courseId), anyList())).thenReturn(course);

        mockMvc.perform(multipart("/api/courses/{courseId}/media", courseId)
                        .file(file1)
                        .file(file2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.mediaFiles[0]").value(VIDEO_FILE))
                .andExpect(jsonPath("$.mediaFiles[1]").value(DOCUMENT_FILE));

        verify(courseService, times(1)).uploadMediaFiles(eq(courseId), anyList());
    }
}
