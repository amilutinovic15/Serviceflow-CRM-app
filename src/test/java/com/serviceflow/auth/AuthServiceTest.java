package com.serviceflow.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.serviceflow.user.AppUser;
import com.serviceflow.user.AppUserRepository;
import com.serviceflow.user.UserRole;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

class AuthServiceTest {

    private AppUserRepository appUserRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        appUserRepository = mock(AppUserRepository.class);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        authService = new AuthService(
                appUserRepository,
                NoOpPasswordEncoder.getInstance(),
                mock(AuthenticationManager.class),
                mock(JwtTokenService.class)
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void firstUserIsAlwaysAdmin() {
        when(appUserRepository.count()).thenReturn(0L);

        UserResponse response = authService.register(request(UserRole.STAFF));

        assertThat(response.role()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void anonymousCannotRegisterOnceUsersExist() {
        when(appUserRepository.count()).thenReturn(1L);

        assertThatThrownBy(() -> authService.register(request(UserRole.ADMIN)))
                .isInstanceOf(AccessDeniedException.class);
        verify(appUserRepository, never()).save(any());
    }

    @Test
    void staffCannotRegisterUsers() {
        when(appUserRepository.count()).thenReturn(1L);
        authenticateAs("ROLE_STAFF");

        assertThatThrownBy(() -> authService.register(request(UserRole.STAFF)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void adminCanRegisterUserWithRequestedRole() {
        when(appUserRepository.count()).thenReturn(1L);
        authenticateAs("ROLE_ADMIN");

        assertThat(authService.register(request(UserRole.STAFF)).role()).isEqualTo(UserRole.STAFF);
        assertThat(authService.register(request(null)).role()).isEqualTo(UserRole.STAFF);
    }

    private RegisterRequest request(UserRole role) {
        return new RegisterRequest("New User", "New.User@Example.com", "password123", role);
    }

    private void authenticateAs(String authority) {
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                "someone@example.com", null, List.of(new SimpleGrantedAuthority(authority))));
    }
}
