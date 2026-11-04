package com.serviceflow.user;

import com.serviceflow.auth.UserResponse;
import com.serviceflow.common.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(String search, Pageable pageable) {
        return appUserRepository.search(blankToNull(search), pageable)
                .map(UserResponse::from);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserResponse.from(getUser(id));
    }

    public UserResponse update(Long id, UserUpdateRequest request) {
        AppUser user = getUser(id);
        String email = request.email().trim().toLowerCase();

        appUserRepository.findByEmail(email)
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException("User with email %s already exists".formatted(email));
                });

        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setRole(request.role());
        user.setEnabled(request.enabled());

        return UserResponse.from(user);
    }

    public UserResponse changeEnabled(Long id, boolean enabled) {
        AppUser user = getUser(id);
        user.setEnabled(enabled);
        return UserResponse.from(user);
    }

    public UserResponse resetPassword(Long id, AdminPasswordResetRequest request) {
        AppUser user = getUser(id);
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        return UserResponse.from(user);
    }

    private AppUser getUser(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id %d was not found".formatted(id)));
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
