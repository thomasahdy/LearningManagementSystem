package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    
    // Find events by creator's username
    List<CalendarEvent> findByCreatorUsername(String username);
    
    // Find events for a specific course
    List<CalendarEvent> findByCourseId(Long courseId);
    
    // Find upcoming events that haven't had reminders sent yet
    @Query("SELECT e FROM CalendarEvent e WHERE e.startTime BETWEEN :now AND :future AND e.reminderSent = false")
    List<CalendarEvent> findUpcomingEventsForReminders(@Param("now") LocalDateTime now, @Param("future") LocalDateTime future);
    
    // Find events where a user is a target for reminders
    @Query("SELECT e FROM CalendarEvent e WHERE :username MEMBER OF e.targetUsernames")
    List<CalendarEvent> findEventsByTargetUsername(@Param("username") String username);
    
    // Find events in a date range
    List<CalendarEvent> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // Find events for assignment deadlines
    List<CalendarEvent> findByAssignmentIdAndEventType(Long assignmentId, CalendarEvent.EventType eventType);
    
    // Find events for a specific user in date range (either created by or targeted at)
    @Query("SELECT e FROM CalendarEvent e WHERE " +
           "(e.creator.username = :username OR :username MEMBER OF e.targetUsernames) AND " +
           "e.startTime BETWEEN :start AND :end")
    List<CalendarEvent> findUserEventsInDateRange(
        @Param("username") String username, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );
} 