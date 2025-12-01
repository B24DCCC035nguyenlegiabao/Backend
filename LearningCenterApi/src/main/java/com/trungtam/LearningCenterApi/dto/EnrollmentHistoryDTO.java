package com.trungtam.LearningCenterApi.dto;

import com.trungtam.LearningCenterApi.entity.CertificateStatus; // Import Enum
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrollmentHistoryDTO {
    private Long enrollmentId; // ID của lần đăng ký
    private String courseCode; // Mã khóa học
    private Long courseId;  // ← THÊM DÒNG NÀY
    private String courseContent; // Nội dung khóa học
    private LocalDateTime enrollmentDate;
    private LocalDateTime courseStartDate; // Ngày bắt đầu khóa
    private LocalDateTime courseEndDate;
    private CertificateStatus status;
}
