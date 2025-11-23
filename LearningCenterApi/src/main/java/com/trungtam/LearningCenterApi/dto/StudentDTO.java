// Đặt tại: com.trungtam.LearningCenterApi.dto.StudentDTO.java
package com.trungtam.LearningCenterApi.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentDTO {
    private Long id;
    private String msv; // Mã học viên
    private String fullName;
    private String ho; // Thêm trường Ho
    private String ten; // Thêm trường Ten
    private LocalDate dateOfBirth;
    private String hometown;
    private String residenceProvince;
}