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
    private long totalPending;
    private long totalInProgress;
    // Constructor với tất cả tham số
    public CourseStatsDTO(int year, long totalCourses, long totalStudentsEnrolled,
                          long totalPending, long totalInProgress,
                          long totalPass, long totalFail) {
        this.year = year;
        this.totalCourses = totalCourses;
        this.totalStudentsEnrolled = totalStudentsEnrolled;
        this.totalPending = totalPending;
        this.totalInProgress = totalInProgress;  // ← Thêm dòng này
        this.totalPass = totalPass;
        this.totalFail = totalFail;
    }

    // Getters và Setters
    public long getTotalInProgress() {
        return totalInProgress;
    }

    public void setTotalInProgress(long totalInProgress) {
        this.totalInProgress = totalInProgress;
    }
}