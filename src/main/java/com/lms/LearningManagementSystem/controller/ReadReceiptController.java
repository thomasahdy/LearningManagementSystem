package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.model.ReadReceipt;
import com.lms.LearningManagementSystem.service.ReadReceiptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/read-receipts")
public class ReadReceiptController {


    private final ReadReceiptService readReceiptService;
    public ReadReceiptController(ReadReceiptService readReceiptService){
        this.readReceiptService = readReceiptService;
    }

    // API endpoint to mark an announcement as read
    @PostMapping("/{announcementId}/read")
    public void markAsRead(@PathVariable Long announcementId, @RequestParam Long userId) {
        readReceiptService.markAsRead(userId, announcementId);
    }

    // API endpoint to get all read receipts for a specific announcement
    @GetMapping("/announcements/{announcementId}")
    public List<ReadReceipt> getReadReceiptsForAnnouncement(@PathVariable Long announcementId) {
        return readReceiptService.getAllReadReceiptsForAnnouncement(announcementId);
    }

    // API endpoint to get all announcements read by a specific user
    @GetMapping("/users/{userId}")
    public List<ReadReceipt> getReadReceiptsForUser(@PathVariable Long userId) {
        return readReceiptService.getAllReadReceiptsForUser(userId);
    }
}

