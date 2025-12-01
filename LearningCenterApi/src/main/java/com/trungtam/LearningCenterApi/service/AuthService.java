// Đặt tại: com.trungtam.LearningCenterApi.service.AuthService.java
package com.trungtam.LearningCenterApi.service;

import com.trungtam.LearningCenterApi.dto.LoginRequest;
import com.trungtam.LearningCenterApi.dto.JwtResponse;
import com.trungtam.LearningCenterApi.dto.RegisterRequest; // Lỗi 1: Phải tạo file này
import com.trungtam.LearningCenterApi.entity.User;
import com.trungtam.LearningCenterApi.entity.Provider;
import com.trungtam.LearningCenterApi.repository.UserRepository;
import com.trungtam.LearningCenterApi.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Logic nghiệp vụ cho chức năng Login
     */
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // FIX 1: Lỗi Inconvertible types (cast)
        // Lý do: userRepository.findByUsername() trả về Optional<User>, không phải User.
        // Cách sửa: Dùng .orElseThrow() để lấy User ra khỏi Optional.
        User userDetails = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user (lỗi logic)"));

        // FIX 2: Lỗi generateToken cannot be applied to (String)
        // Lý do: Hàm generateToken của bạn cần 1 đối tượng UserDetails (chính là User),
        // nhưng bạn lại truyền vào 1 chuỗi String (userDetails.getUsername()).
        // Cách sửa: Truyền cả đối tượng userDetails.
        String jwt = jwtTokenUtil.generateToken(userDetails);

        return new JwtResponse(
                jwt,
                userDetails.getUsername(),
                userDetails.getRole()
        );
    }

    /**
     * Logic nghiệp vụ cho chức năng Register
     */
    public User register(RegisterRequest registerRequest) {
        // FIX 3: Lỗi logic kiểm tra
        // Lý do: findByUsername trả về Optional, nó không bao giờ là null.
        // Cách sửa: Dùng hàm existsByUsername() (chúng ta sẽ thêm hàm này ở bước 3)
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Lỗi: Tên đăng nhập đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        user.setRole("ROLE_USER"); // Gán quyền mặc định (giảm STAFF -> USER)
        user.setProvider(Provider.LOCAL); // Đánh dấu đây là tài khoản local

        return userRepository.save(user);
    }
}