package com.serviceflow.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.serviceflow.auth.JwtTokenService;
import com.serviceflow.auth.LoginResponse;
import com.serviceflow.user.AppUser;
import com.serviceflow.user.UserRole;
import java.time.Duration;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

class SecurityConfigJwtTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void issuedTokenIsAcceptedByDecoder() {
        SecretKey key = securityConfig.jwtSecretKey("test-secret-that-is-long-enough-for-hs256");
        JwtTokenService tokenService = new JwtTokenService(securityConfig.jwtEncoder(key), Duration.ofHours(1));
        JwtDecoder decoder = securityConfig.jwtDecoder(key);

        LoginResponse response = tokenService.issueToken(user());
        Jwt jwt = decoder.decode(response.accessToken());

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(jwt.getSubject()).isEqualTo("admin@serviceflow.local");
        assertThat(jwt.getClaimAsStringList(JwtTokenService.ROLES_CLAIM)).containsExactly("ADMIN");
    }

    @Test
    void tokenSignedWithOtherSecretIsRejected() {
        SecretKey issuingKey = securityConfig.jwtSecretKey("test-secret-that-is-long-enough-for-hs256");
        SecretKey otherKey = securityConfig.jwtSecretKey("another-secret-that-is-long-enough-hs256");
        JwtTokenService tokenService = new JwtTokenService(securityConfig.jwtEncoder(issuingKey), Duration.ofHours(1));

        String token = tokenService.issueToken(user()).accessToken();

        assertThatThrownBy(() -> securityConfig.jwtDecoder(otherKey).decode(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shortSecretIsRejected() {
        assertThatThrownBy(() -> securityConfig.jwtSecretKey("too-short"))
                .isInstanceOf(IllegalStateException.class);
    }

    private AppUser user() {
        AppUser user = new AppUser();
        user.setEmail("admin@serviceflow.local");
        user.setFullName("Admin User");
        user.setRole(UserRole.ADMIN);
        user.setEnabled(true);
        return user;
    }
}
