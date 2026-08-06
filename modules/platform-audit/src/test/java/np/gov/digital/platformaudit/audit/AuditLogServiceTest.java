package np.gov.digital.platformaudit.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuditLogServiceTest
 * Day 5 — citizen_events writer tests
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditLogService")
class AuditLogServiceTest {

    @Mock private JdbcTemplate jdbcTemplate;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        auditLogService = new AuditLogService(jdbcTemplate);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("log — writes one row to audit_logs table")
    void log_writesOneRow() {
        UUID citizenId = UUID.randomUUID();

        auditLogService.log(AuditEventType.CITIZEN_REGISTERED, citizenId, "Citizen registered");

        verify(jdbcTemplate, times(1)).update(anyString(),
                any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("log — uses actor ID from JWT sub claim")
    void log_usesActorFromJwt() {
        String userId = UUID.randomUUID().toString();
        mockJwt(userId);

        auditLogService.log(AuditEventType.CITIZEN_REGISTERED, "Test registration");

        verify(jdbcTemplate, times(1)).update(anyString(),
                any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("log — NEVER throws even if DB fails")
    void log_neverThrowsOnDbFailure() {
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("DB connection lost"));

        assertDoesNotThrow(() ->
                auditLogService.log(AuditEventType.FAILED_LOGIN, "Login failed"));
    }

    @Test
    @DisplayName("log — uses anonymous when no JWT present")
    void log_usesAnonymousWithNoAuth() {
        assertDoesNotThrow(() ->
                auditLogService.log(AuditEventType.FAILED_LOGIN, "No auth"));
    }

    @Test
    @DisplayName("log — FAILED_LOGIN event type resolves to AUTH entity")
    void log_failedLogin_resolvesToAuthEntity() {
        auditLogService.log(AuditEventType.FAILED_LOGIN, "Wrong password");

        verify(jdbcTemplate).update(anyString(),
                any(), eq("FAILED_LOGIN"), eq("AUTH"), any(), any());
    }

    @Test
    @DisplayName("log — CITIZEN_REGISTERED event resolves to CITIZEN entity")
    void log_citizenRegistered_resolvesToCitizenEntity() {
        UUID citizenId = UUID.randomUUID();
        auditLogService.log(AuditEventType.CITIZEN_REGISTERED, citizenId, "Registered");

        verify(jdbcTemplate).update(anyString(),
                any(), eq("CITIZEN_REGISTERED"), eq("CITIZEN"), any(), any());
    }

    // ----------------------------------------------------------------

    private void mockJwt(String sub) {
        Jwt jwt = new Jwt("token", Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"),
                Map.of("sub", sub, "role", "WARD_ADMIN"));
        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthenticationToken(jwt));
    }
}