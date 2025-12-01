// Đặt tại: com.trungtam.LearningCenterApi.security.jwt.JwtRequestFilter.java
package com.trungtam.LearningCenterApi.security.jwt;

import com.trungtam.LearningCenterApi.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // --- ĐOẠN CODE SỬA LỖI 401 ---
        // Lấy đường dẫn (URI) của request
        final String path = request.getRequestURI();

        // Nếu request là API login hoặc OAuth2, BỎ QUA (SKIP)
        // và cho nó đi tiếp mà không kiểm tra Token.
        if (path.startsWith("/api/auth/") || path.startsWith("/oauth2/") || path.startsWith("/login/")) {
            filterChain.doFilter(request, response);
            return; // Dừng lại, không làm gì thêm
        }
        // --- KẾT THÚC ĐOẠN CODE SỬA LỖI ---


        // 1. Lấy Header 'Authorization' từ request
        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // 2. Kiểm tra Header có tồn tại và bắt đầu bằng "Bearer " không
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7); // Lấy phần Token (bỏ "Bearer ")
            try {
                // (Sử dụng tên hàm extractUsername mà code của bạn đã dùng)
                username = jwtTokenUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                System.out.println("Invalid JWT Token: " + e.getMessage());
            }
        }

        // 3. Nếu lấy được username VÀ user này chưa được xác thực
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. Tải thông tin user từ DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 5. Validate token (Sử dụng tên hàm validateToken)
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {


                // 6. ĐỌC AUTHORITIES TỪ JWT TOKEN (không dùng từ DB nữa)
                io.jsonwebtoken.Claims claims = jwtTokenUtil.extractAllClaims(jwtToken);
                @SuppressWarnings("unchecked")
                java.util.List<String> authoritiesList = claims.get("authorities", java.util.List.class);

                java.util.List<org.springframework.security.core.GrantedAuthority> authorities =
                        authoritiesList.stream()
                                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                                .collect(java.util.stream.Collectors.toList());


                // 6. Tự tay tạo 1 đối tượng Authentication
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Quan trọng: "Báo" cho Spring Security biết user này đã OK
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Cho phép request đi tiếp
        filterChain.doFilter(request, response);
    }
}