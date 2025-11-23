
// Đặt file này vào: src/main/java/com/trungtam/LearningCenterApi/dto/CreateCourseRequest.java
package com.trungtam.LearningCenterApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateCourseRequest {

    @NotBlank(message = "Mã khóa học không được để trống")
    private String courseCode;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endDate;

    private String content;
}