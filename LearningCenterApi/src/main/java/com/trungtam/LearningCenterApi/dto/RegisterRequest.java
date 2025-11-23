// Tạo file này tại: com.trungtam.LearningCenterApi.dto.RegisterRequest.java
package com.trungtam.LearningCenterApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username; // Phải là email

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}