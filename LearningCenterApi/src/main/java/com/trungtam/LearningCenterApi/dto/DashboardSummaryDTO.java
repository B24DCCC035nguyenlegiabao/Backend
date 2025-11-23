// Đặt tại: com.trungtam.LearningCenterApi.dto.DashboardSummaryDTO.java
package com.trungtam.LearningCenterApi.dto;

import lombok.Data;

@Data
public class DashboardSummaryDTO {
    private long totalStudents;
    private long totalCourses;
    private long totalEnrollments;
    private long pendingCertificates; // Số chứng chỉ đang chờ
}