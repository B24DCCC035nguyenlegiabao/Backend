// Đặt file này vào: src/main/java/com/trungtam/LearningCenterApi/dto/UpdateCourseRequest.java
package com.trungtam.LearningCenterApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateCourseRequest {

    // Khi update, mã khóa có thể không cho sửa, nhưng để đơn giản ta vẫn giữ
    @NotBlank(message = "Mã khóa học không được để trống")
    private String courseCode;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endDate;

    private String content;
}