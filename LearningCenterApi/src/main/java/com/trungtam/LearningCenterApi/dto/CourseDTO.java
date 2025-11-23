// Đặt file này vào: src/main/java/com/trungtam/LearningCenterApi/dto/CourseDTO.java
package com.trungtam.LearningCenterApi.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data // Tự động tạo Getters, Setters, toString()...
public class CourseDTO {

    private Long id;
    private String courseCode; // Mã khóa
    private LocalDateTime startDate; // Thời gian bắt đầu
    private LocalDateTime endDate; // Thời gian kết thúc
    private String content; // Nội dung khóa học
}