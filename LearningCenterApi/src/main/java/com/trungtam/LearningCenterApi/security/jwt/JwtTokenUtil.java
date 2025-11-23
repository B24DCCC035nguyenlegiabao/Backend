package com.trungtam.LearningCenterApi.security.jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtTokenUtil {

    @Value("${jwt.expiration}")
    private long jwtExpirationMs; // Biến này sẽ chứa giá trị 36000000 (10 giờ)

    // Lấy khóa bí mật từ file application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Lấy Key đã mã hóa để ký
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. Trích xuất username từ token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Trích xuất ngày hết hạn
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 3. Kiểm tra token hết hạn chưa
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 4. Hàm validate chính: kiểm tra username có khớp VÀ token còn hạn không
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 5. Hàm TẠO TOKEN
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // (Có thể thêm claims khác, ví dụ: role)
        // claims.put("role", userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) // Subject là username
                .setIssuedAt(new Date(System.currentTimeMillis())) // Ngày phát hành
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Ngày hết hạn
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Ký bằng khóa bí mật
                .compact();
    }

    // (Hàm helper để giải mã claims)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}