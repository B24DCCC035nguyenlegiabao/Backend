// Đặt file này tại: com/trungtam/LearningCenterApi/exception/ResourceNotFoundException.java

package com.trungtam.LearningCenterApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Đây là một lớp Exception tùy chỉnh.
 * Khi Service ném (throw) lỗi này, Spring Boot sẽ tự động
 * bắt lấy và trả về một response HTTP 404 (NOT_FOUND) cho client.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND) // Báo cho Spring Boot đây là lỗi 404
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message); // Gửi message (ví dụ: "Course not found") cho lớp cha
    }
}