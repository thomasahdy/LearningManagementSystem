package com.lms.LearningManagementSystem.controller;


import com.lms.LearningManagementSystem.model.Announcement;
import com.lms.LearningManagementSystem.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {


    private final   AnnouncementService announcementService;
    public AnnouncementController(AnnouncementService announcementService){
        this.announcementService = announcementService;
    }

    // API endpoint to create a new announcement
    @PostMapping
    public Announcement createAnnouncement(@RequestParam String title,
                                           @RequestParam String content,
                                           @RequestParam Long postedBy) {
        return announcementService.postAnnouncement(title, content, postedBy);
    }

    // API endpoint to get all announcements
    @GetMapping
    public List<Announcement> getAllAnnouncements() {
        return announcementService.getAllAnnouncements();
    }
}