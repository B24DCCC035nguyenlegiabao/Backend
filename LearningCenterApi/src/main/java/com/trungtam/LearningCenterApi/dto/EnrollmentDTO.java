package com.trungtam.LearningCenterApi.dto;
import com.trungtam.LearningCenterApi.entity.Enrollment.CertificateStatus; // Giả sử bạn có Entity này
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {
    private Long id;
    private Long studentId;
    private String studentFullName;
    private Long courseId;
    private String courseCode; // Thêm thông tin này để hiển thị
    private LocalDateTime enrollmentDate;
    private CertificateStatus status;
}