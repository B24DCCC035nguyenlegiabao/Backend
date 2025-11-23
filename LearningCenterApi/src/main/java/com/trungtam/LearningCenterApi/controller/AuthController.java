package com.trungtam.LearningCenterApi.controller;

import com.trungtam.LearningCenterApi.dto.JwtResponse;
import com.trungtam.LearningCenterApi.dto.LoginRequest;
import com.trungtam.LearningCenterApi.security.jwt.JwtTokenUtil;
import com.trungtam.LearningCenterApi.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// @RestController: Đánh dấu đây là một Controller xử lý REST API
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // Cho phép React gọi
public class AuthController {

    // 1. Các thành phần cần thiết cho Authentication/JWT
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * API Login chính thức.
     * @param loginRequest Chứa username và password (plaintext) từ client
     * @return JwtResponse chứa JWT Token nếu xác thực thành công
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {

        // 1. Xác thực username/password từ request
        //    - AuthenticationManager sẽ gọi UserDetailsServiceImpl để tải user.
        //    - Sau đó, nó dùng PasswordEncoder để so sánh mật khẩu.
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword() // Mật khẩu plaintext
                    )
            );
        } catch (Exception e) {
            // Ném lỗi Bad Credentials (hoặc lỗi chung) nếu xác thực thất bại
            throw new Exception("Sai tên đăng nhập hoặc mật khẩu", e);
        }

        // 2. Nếu xác thực thành công (không ném ra Exception), tải UserDetails
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginRequest.getUsername());

        // 3. Tạo JWT Token
        final String token = jwtTokenUtil.generateToken(userDetails);

        // 4. Trả về Token và thông tin user cho client
        return ResponseEntity.ok(new JwtResponse(
                token,
                userDetails.getUsername(),
                userDetails.getAuthorities().iterator().next().getAuthority() // Lấy role
        ));
    }

    // (Bạn có thể thêm API POST /api/auth/register ở đây)
}