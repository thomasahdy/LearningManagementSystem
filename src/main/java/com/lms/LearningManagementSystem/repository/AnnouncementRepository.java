package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    // Find announcements posted by a specific user
    List<Announcement> findByPostedById(Long userId);
    
    // Find announcements created after a certain date
    List<Announcement> findByCreatedAtAfter(LocalDateTime date);
    
    // Find announcements by title containing keyword
    List<Announcement> findByTitleContainingIgnoreCase(String keyword);
    
    // Find announcements by content containing keyword
    List<Announcement> findByContentContainingIgnoreCase(String keyword);
    
    // Custom query to find announcements with search in title or content
    @Query("SELECT a FROM Announcement a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Announcement> searchAnnouncements(@Param("keyword") String keyword);
}