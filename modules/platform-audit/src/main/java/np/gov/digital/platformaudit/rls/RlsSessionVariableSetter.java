package np.gov.digital.platformaudit.rls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * RlsSessionVariableSetter
 *
 * Reads the current JWT from Spring SecurityContext and calls
 * SET LOCAL on the database connection so PostgreSQL RLS policies
 * (V9 migration) know which ward / municipality / province this
 * request belongs to.
 *
 * Flow:
 *   JWT arrives  →  Spring parses it  →  this class reads claims
 *   →  SET LOCAL app.current_ward_id = '<uuid>'
 *   →  PostgreSQL RLS filters citizen rows automatically
 *
 * SET LOCAL is transaction-scoped — resets when the transaction
 * ends, so HikariCP connection recycling cannot leak one user's
 * data scope to another user.
 */
@Slf4j
@Component
public class RlsSessionVariableSetter {
    public void setFromSecurityContext(Connection connection) throws SQLException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            log.debug("RLS: no authenticated user — skipping SET LOCAL");
            return;
        }

        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            log.debug("RLS: auth type {} is not JWT — skipping", auth.getClass().getSimpleName());
            return;
        }

        Jwt jwt = jwtAuth.getToken();
        Map<String, Object> claims = jwt.getClaims();

        String wardId         = claimAsString(claims, "ward_id");
        String municipalityId = claimAsString(claims, "municipality_id");
        String provinceId     = claimAsString(claims, "province_id");
        String role           = claimAsString(claims, "role");

        log.debug("RLS: role={} ward={} municipality={} province={}",
                role, wardId, municipalityId, provinceId);

        try (Statement stmt = connection.createStatement()) {
            if (wardId != null) {
                stmt.execute("SET LOCAL app.current_ward_id = '"+ validated(wardId) + "'");
            }
            if (municipalityId != null) {
                stmt.execute("SET LOCAL app.current_municipality_id = '"+ validated(municipalityId) + "'");
            }
            if (provinceId != null) {
                stmt.execute("SET LOCAL app.current_province_id = '"+ validated(provinceId) + "'");
            }
            // CENTRAL_ADMIN: no scope — USING (true) policy applies, no SET LOCAL needed
        }
    }

    /**
     * Convenience overload for tests or non-HTTP flows where you
     * already have the IDs available directly.
     */
    public void setExplicit(Connection connection,
                            String wardId,
                            String municipalityId,
                            String provinceId) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            if (wardId != null)
                stmt.execute("SET LOCAL app.current_ward_id = '"+ validated(wardId) + "'");
            if (municipalityId != null)
                stmt.execute("SET LOCAL app.current_municipality_id = '"+ validated(municipalityId) + "'");
            if (provinceId != null)
                stmt.execute("SET LOCAL app.current_province_id = '"+ validated(provinceId) + "'");
        }
    }

    /**
     * Resets all session variables back to defaults.
     * Call this inside Spring TransactionSynchronization.afterCompletion()
     * when returning a connection to HikariCP after an abnormal
     * transaction end — prevents scope from bleeding to the next user
     * who receives the same physical connection from the pool.
     */
    public void reset(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("RESET app.current_ward_id");
            stmt.execute("RESET app.current_municipality_id");
            stmt.execute("RESET app.current_province_id");
        } catch (SQLException e) {
            // Variable was never set — safe to ignore
            log.trace("RLS reset: {}", e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private String claimAsString(Map<String, Object> claims, String key) {
        Object val = claims.get(key);
        if (val == null) return null;
        String s = val.toString().trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * Validates that value is a UUID before embedding in SQL.
     * Prevents SQL injection through a tampered JWT claim.
     * UUID = 32 hex digits + 4 dashes = exactly 36 characters.
     */
    private String validated(String value) {
        if (value == null) return null;
        if (!value.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            throw new IllegalArgumentException(
                    "RLS: claim value is not a valid UUID: [" + value + "]");
        }
        return value;
    }
}