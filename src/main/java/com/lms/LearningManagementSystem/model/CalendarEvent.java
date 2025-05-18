package com.lms.LearningManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String title;
    
    private String description;
    
    @NotNull
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    
    // The user who created the event
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    
    // Course associated with this event (optional)
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    
    // Assignment associated with this event (optional)
    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
    
    // Users who should receive reminders for this event
    @ElementCollection
    private List<String> targetUsernames = new ArrayList<>();
    
    // Whether reminders have been sent already
    private boolean reminderSent = false;
    
    // How many days before the event should the reminder be sent
    private Integer reminderDaysBefore = 1;
    
    // Type of events in the calendar
    public enum EventType {
        ASSIGNMENT_DEADLINE,
        COURSE_START,
        COURSE_END,
        EXAM,
        CLASS_SESSION,
        CUSTOM
    }
} 