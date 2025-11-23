// Đặt tại: com.trungtam.LearningCenterApi.security.UserDetailsServiceImpl.java
package com.trungtam.LearningCenterApi.security;

import com.trungtam.LearningCenterApi.entity.User;
import com.trungtam.LearningCenterApi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm user trong DB và "mở hộp" Optional
        // Dùng .orElseThrow() để lấy User ra, nếu không có thì tự động ném ra Exception
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        // 2. Trả về chính đối tượng user đó
        // (Vì User.java đã implement UserDetails,
        // nó tự biết cách lấy password và getAuthorities)
        return user;
    }
}