package com.trungtam.LearningCenterApi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollRequest {
    @NotNull(message = "Student ID không được để trống")
    private Long studentId;

    @NotNull(message = "Course ID không được để trống")
    private Long courseId;
}