package com.trungtam.LearningCenterApi.security; // Chú ý package

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Đây là lớp xử lý lỗi khi một request KHÔNG ĐƯỢC XÁC THỰC
 * (unauthenticated) cố gắng truy cập tài nguyên cần xác thực.
 * Nó sẽ trả về lỗi 401 Unauthorized thay vì redirect sang trang login.
 */
@Component // Đánh dấu đây là một Bean để SecurityConfig có thể @Autowired
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.error("Lỗi xác thực không thành công (Unauthorized): {}", authException.getMessage());

        // Set kiểu response trả về là JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Set mã lỗi là 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Tạo một đối tượng JSON để trả về cho client
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Bạn cần đăng nhập để truy cập tài nguyên này");
        body.put("path", request.getServletPath());

        // Dùng ObjectMapper (của thư viện Jackson) để ghi JSON vào response
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}