// File: src/main/java/com/trungtam/dto/JwtResponse.java

package com.trungtam.LearningCenterApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO (Data Transfer Object) để trả về JWT Token cho client.
 * Chúng ta không dùng @Data vì nó tạo ra cả Setter (không cần thiết).
 */
@Getter // Chỉ tạo các hàm getter (getToken(), getUsername(), getRole())
// Jackson (trình chuyển đổi JSON) sẽ dùng các hàm này.
@AllArgsConstructor // Chỉ tạo 1 constructor duy nhất nhận TẤT CẢ các trường
// (public JwtResponse(String token, String username, String role))
public class JwtResponse {

    private String token;
    private String username;
    private String role;

    // --- KHÔNG CẦN VIẾT BẤT KỲ CODE NÀO BÊN TRONG NỮA ---
    // Lombok sẽ tự động làm hết.
}