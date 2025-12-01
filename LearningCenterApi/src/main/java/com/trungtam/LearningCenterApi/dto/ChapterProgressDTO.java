package com.trungtam.LearningCenterApi.dto;

import lombok.Data;

@Data
public class ChapterProgressDTO {
    private Long chapterId;
    private Integer number;
    private String title;
    private boolean completed;
    private String completedAt; // ISO string optional
}

