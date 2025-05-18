package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}