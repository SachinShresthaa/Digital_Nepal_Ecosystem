package np.gov.digital.platformaudit.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final JdbcTemplate jdbcTemplate;
    public void log(AuditEventType eventType, UUID entityId, String details) {
        try {
            String actorId    = extractActorId();
            String entityType = resolveEntityType(eventType);

            jdbcTemplate.update(
                    """
                    INSERT INTO reporting.audit_logs
                        (user_id, action, entity_type, entity_id, changes)
                    VALUES
                        (?::uuid, ?, ?, ?::uuid, ?::jsonb)
                    """,
                    actorId,
                    eventType.name(),
                    entityType,
                    entityId != null ? entityId.toString() : null,
                    buildChangesJson(details)
            );

            log.debug("Audit: {} on {} [{}] by {}",
                    eventType, entityType, entityId, actorId);

        } catch (Exception e) {
            // NEVER let audit failure break the main request
            // Log the failure but do not rethrow
            log.error("AuditLogService: failed to write audit event {} — {}",
                    eventType, e.getMessage(), e);
        }
    }

    public void log(AuditEventType eventType, String details) {
        log(eventType, null, details);
    }
    private String extractActorId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();
                Object sub = jwt.getClaims().get("sub");
                return sub != null ? sub.toString() : "anonymous";
            }
        } catch (Exception e) {
            log.trace("AuditLogService: could not extract actor from JWT: {}", e.getMessage());
        }
        return "anonymous";
    }
    private String resolveEntityType(AuditEventType eventType) {
        return switch (eventType) {
            case CITIZEN_REGISTERED, CITIZEN_UPDATED,
                 CITIZEN_ARCHIVED, CITIZEN_RESTORED,
                 EDIT_SUBMITTED, EDIT_APPROVED, EDIT_REJECTED,
                 DUPLICATE_NID_ATTEMPT, NID_VERIFIED       -> "CITIZEN";

            case LOGIN_SUCCESS, FAILED_LOGIN, LOGOUT,
                 PASSWORD_CHANGED, ACCOUNT_LOCKED          -> "AUTH";

            case ID_CARD_INITIATED, ID_CARD_APPROVED,
                 ID_CARD_REJECTED, ID_CARD_COLLECTED,
                 ID_CARD_REVOKED                           -> "ID_CARD";

            case SYNC_BATCH_SUBMITTED,
                 SYNC_CONFLICT_DETECTED,
                 SYNC_CONFLICT_RESOLVED                    -> "SYNC";

            case GRIEVANCE_SUBMITTED,
                 GRIEVANCE_RESOLVED                        -> "GRIEVANCE";

            default                                        -> "SYSTEM";
        };
    }
    private String buildChangesJson(String details) {
        if (details == null || details.isBlank()) {
            return "{}";
        }
        // Simple safe JSON — details is a controlled internal string,
        // not user input, so basic escaping is sufficient here
        String escaped = details.replace("\"", "\\\"");
        return "{\"details\": \"" + escaped + "\"}";
    }
}