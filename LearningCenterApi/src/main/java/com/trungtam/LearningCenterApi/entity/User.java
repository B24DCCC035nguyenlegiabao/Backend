// Đặt tại: com.trungtam.LearningCenterApi.entity.User.java
package com.trungtam.LearningCenterApi.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users") // Chỉ giữ lại tên Bảng
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Spring sẽ tự map sang cột 'id'

    @Column(unique = true, nullable = false)
    private String username; // Spring sẽ tự map sang cột 'username'

    private String password; // Spring sẽ tự map sang cột 'password'

    @Column(nullable = false)
    private String role; // Spring sẽ tự map sang cột 'role'

    @Enumerated(EnumType.STRING)
    private Provider provider; // Spring sẽ tự map sang cột 'provider'

    private String providerId; // Spring sẽ tự map sang cột 'provider_id'

    public enum Provider {
        LOCAL, GOOGLE, FACEBOOK
    }

    // --- Các phương thức của UserDetails (Giữ nguyên) ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority(this.role));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}