package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Announcement;
import com.lms.LearningManagementSystem.model.ReadReceipt;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.repository.AnnouncementRepository;
import com.lms.LearningManagementSystem.repository.ReadReceiptRepository;
import com.lms.LearningManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;


    private final ReadReceiptRepository readReceiptRepository;

    private final UserRepository userRepository;
    public AnnouncementService(AnnouncementRepository announcementRepository, ReadReceiptRepository readReceiptRepository, UserRepository userRepository){
        this.announcementRepository = announcementRepository;
        this.readReceiptRepository = readReceiptRepository;
        this.userRepository = userRepository;
    }

    // Post a new announcement
    public Announcement postAnnouncement(String title, String content, Long userId) {
        Announcement announcement = new Announcement();
         User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setPostedBy(user);
        return announcementRepository.save(announcement);
    }

    // Get all announcements (with optional read receipts per user)
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    // Mark an announcement as read
    public void markAsRead(Long announcementId, Long userId) {
        // Check if the user has already read the announcement
        if (readReceiptRepository.findByAnnouncementIdAndUserId(announcementId, userId).isEmpty()) {
            ReadReceipt receipt = new ReadReceipt();
            receipt.setAnnouncement(announcementRepository.findById(announcementId).orElseThrow());
            receipt.setUserId(userId);
            readReceiptRepository.save(receipt);
        }
    }
}
