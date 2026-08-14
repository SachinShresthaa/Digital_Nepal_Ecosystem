package np.gov.digital.platformgrievance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digital.platformaudit.audit.AuditEventType;
import np.gov.digital.platformaudit.audit.AuditLogService;
import np.gov.digital.platformgrievance.dto.GrievanceFileRequest;
import np.gov.digital.platformgrievance.dto.GrievanceResponse;
import np.gov.digital.platformgrievance.entity.Grievance;
import np.gov.digital.platformgrievance.enums.GrievanceStatus;
import np.gov.digital.platformgrievance.repository.GrievanceRepository;
import np.gov.digital.platformgrievance.util.TrackingCodeGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class GrievanceService {

    // 48-hour SLA from filed_at (SDD 5.3 / Day 7 task doc)
    private static final Duration SLA_WINDOW = Duration.ofHours(48);

    private final GrievanceRepository grievanceRepository;
    private final TrackingCodeGenerator trackingCodeGenerator;
    private final AuditLogService auditLogService;

    @Transactional
    public GrievanceResponse fileGrievance(GrievanceFileRequest request) {

        UUID filedBy = extractActorId();
        String trackingCode = trackingCodeGenerator.generate();
        Instant now = Instant.now();

        Grievance grievance = Grievance.builder()
                .citizenId(request.getCitizenId())
                .filedBy(filedBy)
                .category(request.getCategory())
                .description(request.getDescription())
                .attachmentUrls(request.getAttachmentUrls())
                .trackingCode(trackingCode)
                .filedAt(now)
                .status(GrievanceStatus.RECEIVED)
                .slaDueAt(now.plus(SLA_WINDOW))
                .slaBreached(false)
                .reopenCount((short) 0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Grievance saved = grievanceRepository.save(grievance);

        auditLogService.log(
                AuditEventType.GRIEVANCE_SUBMITTED,
                saved.getCitizenId(),
                "Grievance filed — trackingCode=" + trackingCode
                        + " category=" + request.getCategory()
        );

        log.info("GrievanceService: filed grievance {} for citizen={} category={}",
                trackingCode, request.getCitizenId(), request.getCategory());

        return GrievanceResponse.builder()
                .id(saved.getId())
                .citizenId(saved.getCitizenId())
                .category(saved.getCategory())
                .trackingCode(saved.getTrackingCode())
                .status(saved.getStatus())
                .filedAt(saved.getFiledAt())
                .slaDueAt(saved.getSlaDueAt())
                .message("Grievance filed successfully. Tracking code: " + trackingCode)
                .build();
    }

    private UUID extractActorId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();
                Object sub = jwt.getClaims().get("sub");
                if (sub != null) {
                    return UUID.fromString(sub.toString());
                }
            }
        } catch (Exception e) {
            log.warn("GrievanceService: could not extract actor from JWT: {}", e.getMessage());
        }
        return null;
    }
}
