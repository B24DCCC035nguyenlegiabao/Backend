package com.trungtam.LearningCenterApi.security.config;

import com.trungtam.LearningCenterApi.security.jwt.JwtRequestFilter;
import com.trungtam.LearningCenterApi.security.JwtAuthenticationEntryPoint;
import com.trungtam.LearningCenterApi.security.oauth2.CustomOAuth2UserService; // Import mới
import com.trungtam.LearningCenterApi.security.oauth2.OAuth2AuthenticationSuccessHandler; // Import mới
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Cho phép dùng @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.Arrays;

@Configuration
@EnableWebSecurity // Kích hoạt Spring Security
@EnableMethodSecurity // Bật @PreAuthorize ở Controller
public class SecurityConfig {

    @Autowired private JwtRequestFilter jwtRequestFilter;
    @Autowired private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    @Autowired private CustomOAuth2UserService customOAuth2UserService;
    @Autowired private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF vì sử dụng JWT (Stateleess)
                .csrf(csrf -> csrf.disable())

                // 2. Cấu hình CORS
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // URL của React
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(Arrays.asList("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                // 3. Xử lý lỗi khi truy cập trái phép (401)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // 4. Cấu hình stateless session (quan trọng cho JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Định nghĩa quyền truy cập các URL
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập tất cả API Auth (login, register) và OAuth endpoints
                        // Đã chỉnh: dùng "/**" để bao gồm cả /login và /register
                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                        // Tất cả các request còn lại đều phải được xác thực bằng JWT
                        .anyRequest().authenticated()
                )

                // 6. Cấu hình OAuth2 login
                .oauth2Login(oauth2 -> oauth2
                        // Định nghĩa Custom Service để xử lý thông tin User từ Google/Facebook
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // Định nghĩa Handler để tạo JWT Token sau khi login thành công
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )

                // 7. Thêm bộ lọc JWT của chúng ta VÀO TRƯỚC bộ lọc mặc định của Spring Security
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}