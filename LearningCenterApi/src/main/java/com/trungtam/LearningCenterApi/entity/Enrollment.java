package com.trungtam.LearningCenterApi.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "enrollments")
public class Enrollment {
    public enum CertificateStatus {
        PENDING, // Mới đăng ký
        PASS,    // Đạt
        FAIL     // Không đạt
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") // Khớp tên cột "ID"
    private Long id;

    // Nhiều Enrollment thuộc về 1 Student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Student_ID", nullable = false)
    private Student student;

    // Nhiều Enrollment thuộc về 1 Course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Course_ID", nullable = false)
    private Course course;

    @Column(name = "NgayDangKy", nullable = false) // Khớp "NgayDangKy"
    private LocalDateTime enrollmentDate; // Ngày đăng ký

    @Enumerated(EnumType.STRING) // Lưu chữ "PASS", "FAIL" vào DB
    @Column(name = "TrangThaiChungChi", nullable = false) // Khớp "TrangThaiChungChi"
    private CertificateStatus status = CertificateStatus.PENDING;
}

