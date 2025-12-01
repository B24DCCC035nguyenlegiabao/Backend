package com.trungtam.LearningCenterApi.entity;
// Tạo 1 Enum để quản lý trạng thái
public enum CertificateStatus {
    PENDING, // Mới đăng ký, chưa có trạng thái
    IN_PROGRESS, // Đang học / tiến trình
    PASS,    // Đạt
    FAIL     // Không đạt
}