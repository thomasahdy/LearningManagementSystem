package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Assignment;
import com.lms.LearningManagementSystem.model.CalendarEvent;
import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.repository.CalendarEventRepository;
import com.lms.LearningManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CalendarService {

    private final CalendarEventRepository calendarEventRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Autowired
    public CalendarService(CalendarEventRepository calendarEventRepository, 
                          UserRepository userRepository,
                          NotificationService notificationService,
                          EmailService emailService) {
        this.calendarEventRepository = calendarEventRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    // Get a specific event by ID
    public CalendarEvent getEvent(Long eventId) {
        return calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    // Create a new calendar event
    public CalendarEvent createEvent(CalendarEvent event, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("Creator not found"));
        event.setCreator(creator);
        
        return calendarEventRepository.save(event);
    }
    
    // Create event specifically for an assignment deadline
    public CalendarEvent createAssignmentDeadlineEvent(Assignment assignment, String creatorUsername) {
        CalendarEvent event = new CalendarEvent();
        event.setTitle("Deadline: " + assignment.getTitle());
        event.setDescription("Deadline for assignment: " + assignment.getTitle());
        event.setStartTime(LocalDateTime.now().plusDays(7)); // Default 1 week deadline
        event.setEventType(CalendarEvent.EventType.ASSIGNMENT_DEADLINE);
        event.setAssignment(assignment);
        event.setCourse(assignment.getCourse());
        
        // All students enrolled in the course should get reminders
        if (assignment.getCourse() != null && assignment.getCourse().getEnrolledStudents() != null) {
            event.setTargetUsernames(assignment.getCourse().getEnrolledStudents());
        }
        
        return createEvent(event, creatorUsername);
    }
    
    // Get all events for a specific user (created by them or targeted at them)
    public List<CalendarEvent> getUserEvents(String username) {
        // Find events created by this user
        List<CalendarEvent> createdEvents = calendarEventRepository.findByCreatorUsername(username);
        // Find events targeting this user
        List<CalendarEvent> targetedEvents = calendarEventRepository.findEventsByTargetUsername(username);
        
        // Combine the lists, removing duplicates
        createdEvents.removeAll(targetedEvents);
        createdEvents.addAll(targetedEvents);
        
        return createdEvents;
    }
    
    // Get user events in a specific date range
    public List<CalendarEvent> getUserEventsInDateRange(String username, LocalDateTime start, LocalDateTime end) {
        return calendarEventRepository.findUserEventsInDateRange(username, start, end);
    }
    
    // Get events for a specific course
    public List<CalendarEvent> getCourseEvents(Long courseId) {
        return calendarEventRepository.findByCourseId(courseId);
    }
    
    // Update an existing event
    public CalendarEvent updateEvent(Long eventId, CalendarEvent updatedEvent) {
        CalendarEvent existingEvent = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setStartTime(updatedEvent.getStartTime());
        existingEvent.setEndTime(updatedEvent.getEndTime());
        existingEvent.setReminderDaysBefore(updatedEvent.getReminderDaysBefore());
        
        // Don't update creator, but can update target users
        if (updatedEvent.getTargetUsernames() != null) {
            existingEvent.setTargetUsernames(updatedEvent.getTargetUsernames());
        }
        
        // Reset reminder sent status if the date changed significantly
        if (existingEvent.isReminderSent() && 
            existingEvent.getStartTime().isAfter(LocalDateTime.now().plusDays(existingEvent.getReminderDaysBefore()))) {
            existingEvent.setReminderSent(false);
        }
        
        return calendarEventRepository.save(existingEvent);
    }
    
    // Delete an event
    public void deleteEvent(Long eventId) {
        calendarEventRepository.deleteById(eventId);
    }
    
    // Get upcoming events
    public List<CalendarEvent> getUpcomingEvents(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        return calendarEventRepository.findByStartTimeBetween(now, future);
    }
    
    // Scheduled task that runs daily to send reminders for upcoming events
    @Scheduled(cron = "0 0 8 * * ?") // Run every day at 8:00 AM
    public void sendEventReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekFromNow = now.plusDays(7);
        
        List<CalendarEvent> upcomingEvents = calendarEventRepository.findUpcomingEventsForReminders(now, oneWeekFromNow);
        
        for (CalendarEvent event : upcomingEvents) {
            if (shouldSendReminder(event)) {
                sendReminderNotifications(event);
                event.setReminderSent(true);
                calendarEventRepository.save(event);
            }
        }
    }
    
    // Determine if a reminder should be sent (based on days before the event)
    private boolean shouldSendReminder(CalendarEvent event) {
        LocalDate eventDate = event.getStartTime().toLocalDate();
        LocalDate reminderDate = LocalDate.now().plusDays(event.getReminderDaysBefore());
        
        return !event.isReminderSent() && (eventDate.isEqual(reminderDate) || eventDate.isBefore(reminderDate));
    }
    
    // Send reminder notifications to all target users
    private void sendReminderNotifications(CalendarEvent event) {
        String title = "Reminder: " + event.getTitle();
        String message = "This is a reminder for the upcoming event: " + event.getTitle() + 
                         "\nDate: " + event.getStartTime() +
                         "\nDescription: " + event.getDescription();
        
        // Send notification to each target user
        for (String username : event.getTargetUsernames()) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Send in-app notification
                notificationService.createNotification(user, title, message);
                
                // Send email if user has an email address
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    emailService.sendMail(user.getEmail(), title, message);
                }
            }
        }
    }
} 