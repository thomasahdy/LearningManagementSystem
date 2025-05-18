package com.lms.LearningManagementSystem.service;


import com.lms.LearningManagementSystem.model.Announcement;
import com.lms.LearningManagementSystem.model.ReadReceipt;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.repository.AnnouncementRepository;
import com.lms.LearningManagementSystem.repository.ReadReceiptRepository;
import com.lms.LearningManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadReceiptService {

    private final ReadReceiptRepository readReceiptRepository;

    private final  AnnouncementRepository announcementRepository;

    private final  UserRepository userRepository;
    public ReadReceiptService(ReadReceiptRepository readReceiptRepository, AnnouncementRepository announcementRepository, UserRepository userRepository){
        this.readReceiptRepository = readReceiptRepository;
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
    }

    // Mark an announcement as read
    public void markAsRead(Long userId, Long announcementId) {
        // Check user and announcement existence
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (readReceiptRepository.findByAnnouncementIdAndUserId(announcementId, userId).isEmpty()) {
            ReadReceipt receipt = new ReadReceipt();
            receipt.setUser(user);
            receipt.setUserId(userId);
            receipt.setAnnouncement(announcement);
            receipt.setReadAt(java.time.LocalDateTime.now());
            readReceiptRepository.save(receipt);
        }
    }

    // Get all read receipts for an announcement
    public List<ReadReceipt> getAllReadReceiptsForAnnouncement(Long announcementId) {
        return readReceiptRepository.findAllByAnnouncementId(announcementId);
    }

    // Get all announcements read by a specific user
    public List<ReadReceipt> getAllReadReceiptsForUser(Long userId) {
        return readReceiptRepository.findAllByUserId(userId);
    }
}