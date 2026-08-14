package np.gov.digital.platformgis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digital.platformaudit.audit.AuditEventType;
import np.gov.digital.platformaudit.audit.AuditLogService;
import np.gov.digital.platformgis.dto.GpsCaptureRequest;
import np.gov.digital.platformgis.entity.CitizenGis;
import np.gov.digital.platformgis.exception.InvalidGpsAccuracyException;
import np.gov.digital.platformgis.repository.CitizenGisRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitizenGisService {

    private static final int MAX_ACCEPTED_ACCURACY_M = 500;

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    private final CitizenGisRepository citizenGisRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public CitizenGis captureAndStore(UUID citizenId,
                                      UUID wardId,
                                      UUID capturedBy,
                                      GpsCaptureRequest request) {

        if (request.getAccuracyM() == null || request.getAccuracyM() > MAX_ACCEPTED_ACCURACY_M) {
            log.warn("CitizenGisService: rejecting GPS capture for citizen={} — accuracy={}m",
                    citizenId, request.getAccuracyM());
            throw new InvalidGpsAccuracyException(
                    request.getAccuracyM() == null ? -1 : request.getAccuracyM(),
                    MAX_ACCEPTED_ACCURACY_M
            );
        }

        Point location = GEOMETRY_FACTORY.createPoint(
                new Coordinate(request.getLongitude(), request.getLatitude())
        );
        // JTS coordinate order is (x=longitude, y=latitude) — do not swap.

        CitizenGis gis = CitizenGis.builder()
                .citizenId(citizenId)
                .wardId(wardId)
                .location(location)
                .locationAccuracyM(request.getAccuracyM().shortValue())
                .elevationM(request.getElevationM() != null
                        ? request.getElevationM().shortValue() : null)
                .riskZone(request.getRiskZone())
                .capturedBy(capturedBy)
                .capturedAt(Instant.now())
                .build();

        CitizenGis saved = citizenGisRepository.save(gis);

        auditLogService.log(
                AuditEventType.CITIZEN_UPDATED,
                citizenId,
                "GPS location captured — accuracy=" + request.getAccuracyM() + "m"
        );

        log.info("CitizenGisService: GPS captured for citizen={} accuracy={}m",
                citizenId, request.getAccuracyM());

        return saved;
    }
}