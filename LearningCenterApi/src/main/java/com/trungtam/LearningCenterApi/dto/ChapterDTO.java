package com.trungtam.LearningCenterApi.dto;

import lombok.Data;

@Data
public class ChapterDTO {
    private Long id;
    private Integer number;
    private String title;
    private String content;
}

