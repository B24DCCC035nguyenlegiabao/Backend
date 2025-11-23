// Đặt tại: com.trungtam.LearningCenterApi.dto.CourseStatsDTO.java
package com.trungtam.LearningCenterApi.dto;

import lombok.Data;

@Data
public class CourseStatsDTO {
    private int year;
    private long totalCourses; // Tổng số khóa học
    private long totalStudentsEnrolled; // Tổng số lượt học viên
    private long totalPass; // Tổng số đạt
    private long totalFail; // Tổng số không đạt
}