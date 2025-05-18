package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find unread notifications by user ID
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
    // Find all notifications by user ID
    List<Notification> findByUserId(Long userId);
    
    // Find notifications by user ID with pagination
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    
    // Find notifications by user username
    @Query("SELECT n FROM Notification n WHERE n.user.username = :username")
    List<Notification> findByUserUsername(@Param("username") String username);
    
    // Find notifications by subject containing keyword
    List<Notification> findBySubjectContainingIgnoreCase(String keyword);
    
    // Find notifications by timestamp range
    List<Notification> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find notifications by user ID and subject
    List<Notification> findByUserIdAndSubjectContainingIgnoreCase(Long userId, String subject);
    
    // Count unread notifications by user ID
    Long countByUserIdAndIsReadFalse(Long userId);
    
    // Mark all notifications as read for a specific user
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllNotificationsAsRead(@Param("userId") Long userId);
    
    // Delete old notifications (for cleanup)
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.timestamp < :cutoffDate AND n.isRead = true")
    int deleteOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}

