package com.trungtam.LearningCenterApi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateChaptersRequest {
    @NotNull
    private Long courseId;

    @Min(1)
    private Integer chapterCount;
}

