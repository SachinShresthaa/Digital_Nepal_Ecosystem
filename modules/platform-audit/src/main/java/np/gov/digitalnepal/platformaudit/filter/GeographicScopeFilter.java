package np.gov.digitalnepal.platformaudit.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digitalnepal.platformaudit.rls.RlsSessionVariableSetter;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;

@Slf4j
@Component
@Order(2) // runs after Spring Security filter (Order 1)
@RequiredArgsConstructor
public class GeographicScopeFilter extends OncePerRequestFilter {

    private final DataSource           dataSource;
    private final RlsSessionVariableSetter rlsSetter;

    @Override
    protected void doFilterInternal(HttpServletRequest  request,
                                    HttpServletResponse response,
                                    FilterChain         filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Only set RLS vars for authenticated JWT requests
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();

            String wardId         = getClaimString(jwt, "ward_id");
            String municipalityId = getClaimString(jwt, "municipality_id");
            String provinceId     = getClaimString(jwt, "province_id");
            String role           = getClaimString(jwt, "role");

            log.debug("GeographicScopeFilter: role={} ward={} municipality={} province={}",
                    role, wardId, municipalityId, provinceId);

            // Get connection from pool and set RLS session variables
            try (Connection conn = dataSource.getConnection()) {
                rlsSetter.setExplicit(conn, wardId, municipalityId, provinceId);
            } catch (Exception e) {
                log.error("GeographicScopeFilter: failed to set RLS session variables: {}",
                        e.getMessage(), e);
                // Do NOT block the request — log and continue
                // RLS will return zero rows if vars not set (safe fail)
            }
        }

        // Always continue the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip RLS setup for public endpoints — no JWT, no scope needed
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/refresh")
                || path.startsWith("/api/v1/idcards/verify")
                || path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    // ----------------------------------------------------------------

    private String getClaimString(Jwt jwt, String key) {
        Object val = jwt.getClaims().get(key);
        if (val == null) return null;
        String s = val.toString().trim();
        return s.isEmpty() ? null : s;
    }
}