package com.trungtam.LearningCenterApi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterCompletionRequest {
    @NotNull
    private Long enrollmentId;

    @NotNull
    private Long chapterId;
}

