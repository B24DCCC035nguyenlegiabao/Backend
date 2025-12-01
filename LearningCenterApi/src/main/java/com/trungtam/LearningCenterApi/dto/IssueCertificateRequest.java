package com.trungtam.LearningCenterApi.dto;

import com.trungtam.LearningCenterApi.entity.CertificateStatus; // Import Enum
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IssueCertificateRequest {
    @NotNull(message = "Trạng thái chứng chỉ không được để trống")
    private CertificateStatus status; // "PASS" hoặc "FAIL"
}