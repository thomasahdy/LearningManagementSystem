package com.lms.LearningManagementSystem.repository;
import com.lms.LearningManagementSystem.model.ReadReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadReceiptRepository extends JpaRepository<ReadReceipt, Long> {
    Optional<ReadReceipt> findByAnnouncementIdAndUserId(Long announcementId, Long userId);

    List<ReadReceipt> findAllByAnnouncementId(Long announcementId);

    List<ReadReceipt> findAllByUserId(Long userId);
}
