// Đặt tại: com.trungtam.LearningCenterApi.dto.CreateStudentRequest.java
package com.trungtam.LearningCenterApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateStudentRequest {
    @NotBlank(message = "Họ không được để trống")
    private String ho; // Sửa từ fullName

    @NotBlank(message = "Tên không được để trống")
    private String ten; // Sửa từ fullName

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate dateOfBirth;

    private String hometown;
    private String residenceProvince;
}