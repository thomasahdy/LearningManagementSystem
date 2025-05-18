package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Assignment;
import com.lms.LearningManagementSystem.repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final NotificationService notificationService;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             NotificationService notificationService) {
        this.assignmentRepository = assignmentRepository;
        this.notificationService = notificationService;
    }

    public Assignment saveAssignment(Assignment assignment) {
        String subject = "New Assignment";
        String message = assignment.getContent();
        notificationService.createNotification(assignment.getStudent(), subject, message);
        return assignmentRepository.save(assignment);
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
}
