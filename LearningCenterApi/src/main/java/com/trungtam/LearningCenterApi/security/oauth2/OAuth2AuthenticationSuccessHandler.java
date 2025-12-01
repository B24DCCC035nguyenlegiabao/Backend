package com.trungtam.LearningCenterApi.security.oauth2;

import com.trungtam.LearningCenterApi.entity.User;
import com.trungtam.LearningCenterApi.entity.Provider;
import com.trungtam.LearningCenterApi.repository.UserRepository;
import com.trungtam.LearningCenterApi.security.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken; // THÊM IMPORT NÀY
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    // URL Redirect đến Frontend (Frontend sẽ xử lý Token)
    private static final String FRONTEND_REDIRECT_URL = "http://localhost:3000/oauth-redirect";
    // ^ Đã đổi tên biến để rõ ràng hơn

    public OAuth2AuthenticationSuccessHandler(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 1. Lấy thông tin cơ bản từ Authentication Object
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> attrs = oauth2User.getAttributes();

        String email = (String) attrs.get("email"); // Email là trường bắt buộc
        String providerName = oauthToken.getAuthorizedClientRegistrationId().toUpperCase(); // Ví dụ: GOOGLE, FACEBOOK

        // ID của nhà cung cấp (Google: sub, Facebook: id)
        String providerId = getProviderId(attrs);

        // 2. Tìm User trong Database
        // Dùng Optional để xử lý trường hợp user không tồn tại
        Optional<User> userOpt = userRepository.findByUsername(email);

        User user;
        if (userOpt.isEmpty()) {
            // 3. User không tồn tại: TẠO MỚI (First-time registration)
            user = registerNewUser(email, providerName, providerId);
        } else {
            // 4. User đã tồn tại: Cập nhật thông tin provider (Nếu cần)
            user = updateExistingUser(userOpt.get(), providerName, providerId);
        }

        // 5. Tạo JWT Token của hệ thống
        String token = jwtTokenUtil.generateToken(user);
        String role = user.getRole();

        // 6. Redirect về Frontend với Token (dùng Query Parameter cho dễ xử lý)
        String target = FRONTEND_REDIRECT_URL + "?token=" + token + "&username=" + email + "&role=" + role;
        response.sendRedirect(target);
    }

    // --- HÀM HỖ TRỢ LOGIC ---

    private String getProviderId(Map<String, Object> attrs) {
        if (attrs.containsKey("sub")) {
            return String.valueOf(attrs.get("sub")); // Google
        } else if (attrs.containsKey("id")) {
            return String.valueOf(attrs.get("id")); // Facebook
        }
        return null;
    }

    private User registerNewUser(String email, String providerName, String providerId) {
        User user = new User();
        user.setUsername(email);
        user.setPassword(null); // Mật khẩu là null vì dùng Social Login
        user.setRole("ROLE_USER");
        user.setProvider(Provider.valueOf(providerName));
        user.setProviderId(providerId);

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, String providerName, String providerId) {
        if (existingUser.getProvider() == null || existingUser.getProvider() == Provider.LOCAL) {
            existingUser.setProvider(Provider.valueOf(providerName));
            existingUser.setProviderId(providerId);
            // user.setRole(existingUser.getRole()); // Giữ vai trò cũ
            return userRepository.save(existingUser);
        }
        return existingUser;
    }
}