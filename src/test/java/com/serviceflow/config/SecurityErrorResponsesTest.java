package com.serviceflow.config;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.serviceflow.auth.AuthController;
import com.serviceflow.auth.AuthService;
import com.serviceflow.auth.JwtTokenService;
import com.serviceflow.auth.LoginResponse;
import com.serviceflow.customer.CustomerController;
import com.serviceflow.customer.CustomerService;
import com.serviceflow.customer.CustomerTimelineService;
import com.serviceflow.user.AppUser;
import com.serviceflow.user.AppUserRepository;
import com.serviceflow.user.UserRole;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({CustomerController.class, AuthController.class})
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "serviceflow.jwt.secret=test-secret-that-is-long-enough-for-hs256",
        "serviceflow.cors.allowed-origins=http://localhost:5173"
})
class SecurityErrorResponsesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private CustomerTimelineService customerTimelineService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    void missingTokenReturnsJsonUnauthorized() throws Exception {
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, containsString("Bearer")))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication is required"))
                .andExpect(jsonPath("$.path").value("/api/customers/1"));
    }

    @Test
    void invalidTokenReturnsJsonUnauthorized() throws Exception {
        mockMvc.perform(get("/api/customers/1").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Access token is invalid or expired"));
    }

    @Test
    void loginIgnoresStaleToken() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse("new-token", "Bearer", null, null));

        mockMvc.perform(post("/api/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer stale-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@serviceflow.local\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-token"));
    }

    @Test
    void disabledUserWithValidTokenReturnsJsonUnauthorized() throws Exception {
        givenUser("staff@serviceflow.local", UserRole.STAFF, false);

        mockMvc.perform(get("/api/customers/1").header(HttpHeaders.AUTHORIZATION, bearer("staff@serviceflow.local", UserRole.STAFF)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User account is disabled"));
    }

    @Test
    void staffOnAdminEndpointReturnsJsonForbidden() throws Exception {
        givenUser("staff@serviceflow.local", UserRole.STAFF, true);

        mockMvc.perform(get("/api/users").header(HttpHeaders.AUTHORIZATION, bearer("staff@serviceflow.local", UserRole.STAFF)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void unknownSortFieldReturnsBadRequest() throws Exception {
        givenUser("admin@serviceflow.local", UserRole.ADMIN, true);
        when(customerService.findAll(any(), any()))
                .thenThrow(new InvalidDataAccessApiUsageException("Could not resolve attribute 'string'"));

        mockMvc.perform(get("/api/customers")
                        .param("sort", "string")
                        .header(HttpHeaders.AUTHORIZATION, bearer("admin@serviceflow.local", UserRole.ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid sort parameter 'string'")));
    }

    private void givenUser(String email, UserRole role, boolean enabled) {
        when(userDetailsService.loadUserByUsername(email)).thenReturn(User.withUsername(email)
                .password("unused")
                .roles(role.name())
                .disabled(!enabled)
                .build());
    }

    private String bearer(String email, UserRole role) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setRole(role);
        return "Bearer " + new JwtTokenService(jwtEncoder, Duration.ofMinutes(5)).issueToken(user).accessToken();
    }
}
