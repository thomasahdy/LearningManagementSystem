package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Assignment;
import com.lms.LearningManagementSystem.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final NotificationService notificationService;
    private final CalendarService calendarService;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository,
                             NotificationService notificationService,
                             CalendarService calendarService) {
        this.assignmentRepository = assignmentRepository;
        this.notificationService = notificationService;
        this.calendarService = calendarService;
    }

    public Assignment saveAssignment(Assignment assignment) {
        // Save the assignment first
        Assignment savedAssignment = assignmentRepository.save(assignment);
        
        // Create notification
        String subject = "New Assignment";
        String message = assignment.getContent();
        notificationService.createNotification(assignment.getStudent(), subject, message);
        
        // Create a calendar event for the assignment deadline if there's a teacher/instructor
        if (assignment.getCourse() != null && assignment.getCourse().getInstructor() != null) {
            calendarService.createAssignmentDeadlineEvent(
                savedAssignment, 
                assignment.getCourse().getInstructor().getUsername()
            );
        }
        
        return savedAssignment;
    }

    public Assignment gradeAssignment(Long id, Double grade, String feedback) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setGrade(grade);
        assignment.setFeedback(feedback);

        String subject = "Assignment Grade";
        String message = "Result for " + assignment.getTitle() + " assignment\n" +
                "Grade: " + grade + "\nFeedback: " + feedback;

        notificationService.createNotification(assignment.getStudent(), subject, message);
        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
    
    public List<Assignment> getAssignmentsByCourseId(Long courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }
}
