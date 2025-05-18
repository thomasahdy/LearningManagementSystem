package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.model.CalendarEvent;
import com.lms.LearningManagementSystem.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "*")
public class CalendarController {

    private final CalendarService calendarService;

    @Autowired
    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("/events")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CalendarEvent> createEvent(@RequestBody CalendarEvent event, Principal principal) {
        CalendarEvent createdEvent = calendarService.createEvent(event, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping("/events")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CalendarEvent>> getUserEvents(Principal principal) {
        List<CalendarEvent> events = calendarService.getUserEvents(principal.getName());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CalendarEvent>> getUserEventsInDateRange(
            Principal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<CalendarEvent> events = calendarService.getUserEventsInDateRange(
                principal.getName(), start, end);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{eventId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CalendarEvent> getEventById(@PathVariable Long eventId) {
        try {
            CalendarEvent event = calendarService.getEvent(eventId);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/events/{eventId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CalendarEvent> updateEvent(
            @PathVariable Long eventId,
            @RequestBody CalendarEvent updatedEvent,
            Principal principal) {
        
        try {
            // Verify the user is authorized to update this event
            CalendarEvent existingEvent = calendarService.getEvent(eventId);
            if (existingEvent.getCreator() != null && 
                !existingEvent.getCreator().getUsername().equals(principal.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            CalendarEvent event = calendarService.updateEvent(eventId, updatedEvent);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/events/{eventId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> deleteEvent(
            @PathVariable Long eventId,
            Principal principal) {
        
        try {
            // Verify the user is authorized to delete this event
            CalendarEvent existingEvent = calendarService.getEvent(eventId);
            if (existingEvent.getCreator() != null && 
                !existingEvent.getCreator().getUsername().equals(principal.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            calendarService.deleteEvent(eventId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Event deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/events/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CalendarEvent>> getCourseEvents(@PathVariable Long courseId) {
        List<CalendarEvent> events = calendarService.getCourseEvents(courseId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/upcoming")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CalendarEvent>> getUpcomingEvents(
            @RequestParam(defaultValue = "7") int days, 
            Principal principal) {
        
        List<CalendarEvent> events = calendarService.getUserEventsInDateRange(
                principal.getName(), 
                LocalDateTime.now(), 
                LocalDateTime.now().plusDays(days));
        
        return ResponseEntity.ok(events);
    }
} 