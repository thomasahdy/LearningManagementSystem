package com.lms.LearningManagementSystem.repository;
import com.lms.LearningManagementSystem.model.ReadReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadReceiptRepository extends JpaRepository<ReadReceipt, Long> {
    // Find read receipt by announcement ID and user ID
    Optional<ReadReceipt> findByAnnouncementIdAndUserId(Long announcementId, Long userId);

    // Find all read receipts for an announcement
    List<ReadReceipt> findAllByAnnouncementId(Long announcementId);

    // Find all read receipts for a user
    List<ReadReceipt> findAllByUserId(Long userId);
    
    // Find read receipts created after a specific date
    List<ReadReceipt> findByReadAtAfter(LocalDateTime dateTime);
    
    // Find read receipts within a date range
    List<ReadReceipt> findByReadAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Count read receipts for a specific announcement
    Long countByAnnouncementId(Long announcementId);
    
    // Check if an announcement has been read by all users in a course
    @Query("SELECT COUNT(r) FROM ReadReceipt r WHERE r.announcement.id = :announcementId AND r.user.id IN " +
           "(SELECT e.student.id FROM Enrollment e WHERE e.course.id = :courseId)")
    Long countReadReceiptsForAnnouncementInCourse(@Param("announcementId") Long announcementId, @Param("courseId") Long courseId);
    
    // Get announcements read by a user in a specific time period
    @Query("SELECT r FROM ReadReceipt r WHERE r.userId = :userId AND r.readAt BETWEEN :startDate AND :endDate")
    List<ReadReceipt> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    // Delete all read receipts for an announcement
    void deleteByAnnouncementId(Long announcementId);
    
    // Find users who have not read a specific announcement
    @Query("SELECT u.id FROM User u WHERE u.id NOT IN " +
           "(SELECT r.userId FROM ReadReceipt r WHERE r.announcement.id = :announcementId)")
    List<Long> findUsersWhoHaveNotReadAnnouncement(@Param("announcementId") Long announcementId);
}
