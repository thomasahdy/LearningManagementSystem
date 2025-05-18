package com.lms.LearningManagementSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user; // maps relationship to the User entity

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId; // foreign key column, marked as non-insertable and non-updatable

    @ManyToOne
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;
    // The user who read the announcement
    private LocalDateTime readAt = LocalDateTime.now();
}