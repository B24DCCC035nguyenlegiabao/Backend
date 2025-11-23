package com.trungtam.LearningCenterApi.repository;

import com.trungtam.LearningCenterApi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Đánh dấu đây là một Bean Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA sẽ tự "hiểu" hàm này.
    // "Find" (tìm) "By" (bằng) "Username" (trường username)
    // Nó sẽ tự động sinh ra câu lệnh: "SELECT * FROM users WHERE username = ?"
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    // Optional<User> an toàn hơn User, vì nó xử lý được trường hợp
    // tìm không thấy user (trả về Optional.empty thay vì null)

    // Find by provider and providerId (for OAuth2 users)
    Optional<User> findByProviderAndProviderId(User.Provider provider, String providerId);
}