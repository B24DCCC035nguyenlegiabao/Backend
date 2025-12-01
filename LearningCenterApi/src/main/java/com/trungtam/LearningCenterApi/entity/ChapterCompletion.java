package com.trungtam.LearningCenterApi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chapter_completions")
public class ChapterCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Enrollment_ID", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Chapter_ID", nullable = false)
    private Chapter chapter;

    @Column(name = "CompletedAt")
    private LocalDateTime completedAt;
}

