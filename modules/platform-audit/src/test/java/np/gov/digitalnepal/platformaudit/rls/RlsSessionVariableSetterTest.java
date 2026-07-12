package np.gov.digitalnepal.platformaudit.rls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RlsSessionVariableSetter")
class RlsSessionVariableSetterTest {

    @Mock private Connection connection;
    @Mock private Statement  statement;

    private RlsSessionVariableSetter setter;

    private static final String WARD_UUID         = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
    private static final String MUNICIPALITY_UUID = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";
    private static final String PROVINCE_UUID     = "cccccccc-cccc-cccc-cccc-cccccccccccc";

    @BeforeEach
    void setUp() throws Exception {
        setter = new RlsSessionVariableSetter();
        lenient().when(connection.createStatement()).thenReturn(statement);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Ward admin — sets ward_id only")
    void wardAdmin_setsWardIdOnly() throws Exception {
        mockJwt("WARD_ADMIN", WARD_UUID, null, null);
        setter.setExplicit(connection, WARD_UUID, null, null);
        verify(statement).execute(contains("app.current_ward_id = '" + WARD_UUID));
        verify(statement, never()).execute(contains("app.current_municipality_id"));
        verify(statement, never()).execute(contains("app.current_province_id"));
    }

    @Test
    @DisplayName("Local body admin — sets municipality_id only")
    void localBodyAdmin_setsMunicipalityOnly() throws Exception {
        mockJwt("LOCAL_BODY_ADMIN", null, MUNICIPALITY_UUID, null);
        setter.setExplicit(connection, null, MUNICIPALITY_UUID, null);
        verify(statement, never()).execute(contains("app.current_ward_id"));
        verify(statement).execute(contains("app.current_municipality_id = '" + MUNICIPALITY_UUID));
        verify(statement, never()).execute(contains("app.current_province_id"));
    }

    @Test
    @DisplayName("Province admin — sets province_id only")
    void provinceAdmin_setsProvinceOnly() throws Exception {
        mockJwt("PROVINCE_ADMIN", null, null, PROVINCE_UUID);
        setter.setExplicit(connection, null, null, PROVINCE_UUID);
        verify(statement, never()).execute(contains("app.current_ward_id"));
        verify(statement, never()).execute(contains("app.current_municipality_id"));
        verify(statement).execute(contains("app.current_province_id = '" + PROVINCE_UUID));
    }

    @Test
    @DisplayName("Central admin — no SET LOCAL calls")
    void centralAdmin_noSetLocalCalls() throws Exception {
        mockJwt("CENTRAL_ADMIN", null, null, null);
        setter.setExplicit(connection, null, null, null);
        verify(statement, never()).execute(anyString());
    }

    @Test
    @DisplayName("Invalid UUID — throws IllegalArgumentException")
    void invalidUuid_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> setter.setExplicit(connection, "'; DROP TABLE citizen; --", null, null));
    }

    @Test
    @DisplayName("No auth in SecurityContext — skips gracefully")
    void noAuth_skipsGracefully() throws Exception {
        // No connection needed — just verify no exception
        RlsSessionVariableSetter noAuthSetter = new RlsSessionVariableSetter();
        Connection mockConn = mock(Connection.class);
        Statement mockStmt  = mock(Statement.class);
        lenient().when(mockConn.createStatement()).thenReturn(mockStmt);
        assertDoesNotThrow(() -> noAuthSetter.setFromSecurityContext(mockConn));
    }

    // ----------------------------------------------------------------

    private void mockJwt(String role, String wardId,
                         String municipalityId, String provinceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub",  "test-user-uuid");
        claims.put("role", role);
        if (wardId         != null) claims.put("ward_id",         wardId);
        if (municipalityId != null) claims.put("municipality_id", municipalityId);
        if (provinceId     != null) claims.put("province_id",     provinceId);

        Jwt jwt = new Jwt("token", Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "RS256"), claims);

        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthenticationToken(jwt));
    }
}