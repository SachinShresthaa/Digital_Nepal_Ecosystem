package np.gov.digital.platformgis.service;

import np.gov.digital.platformaudit.audit.AuditLogService;
import np.gov.digital.platformgis.dto.GpsCaptureRequest;
import np.gov.digital.platformgis.entity.CitizenGis;
import np.gov.digital.platformgis.exception.InvalidGpsAccuracyException;
import np.gov.digital.platformgis.repository.CitizenGisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitizenGisServiceTest {

    @Mock
    private CitizenGisRepository citizenGisRepository;

    @Mock
    private AuditLogService auditLogService;

    private CitizenGisService service;

    private final UUID citizenId = UUID.randomUUID();
    private final UUID wardId = UUID.randomUUID();
    private final UUID capturedBy = UUID.randomUUID();

    private CitizenGisService newService() {
        return new CitizenGisService(citizenGisRepository, auditLogService);
    }

    @Test
    void acceptsReadingAtExactly500m() {
        service = newService();
        when(citizenGisRepository.save(any(CitizenGis.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        GpsCaptureRequest request = GpsCaptureRequest.builder()
                .latitude(27.7)
                .longitude(85.3)
                .accuracyM(500)
                .build();

        CitizenGis result = service.captureAndStore(citizenId, wardId, capturedBy, request);

        assertThat(result.getLocationAccuracyM()).isEqualTo((short) 500);
        verify(citizenGisRepository).save(any(CitizenGis.class));
    }

    @Test
    void rejectsReadingWorseThan500m() {
        service = newService();

        GpsCaptureRequest request = GpsCaptureRequest.builder()
                .latitude(27.7)
                .longitude(85.3)
                .accuracyM(501)
                .build();

        assertThatThrownBy(() ->
                service.captureAndStore(citizenId, wardId, capturedBy, request))
                .isInstanceOf(InvalidGpsAccuracyException.class);

        verify(citizenGisRepository, never()).save(any());
    }

    @Test
    void rejectsNullAccuracy() {
        service = newService();

        GpsCaptureRequest request = GpsCaptureRequest.builder()
                .latitude(27.7)
                .longitude(85.3)
                .accuracyM(null)
                .build();

        assertThatThrownBy(() ->
                service.captureAndStore(citizenId, wardId, capturedBy, request))
                .isInstanceOf(InvalidGpsAccuracyException.class);
    }

    @Test
    void wardIdOnEntityAlwaysMatchesPassedInWardId() {
        service = newService();
        ArgumentCaptor<CitizenGis> captor = ArgumentCaptor.forClass(CitizenGis.class);
        when(citizenGisRepository.save(any(CitizenGis.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        GpsCaptureRequest request = GpsCaptureRequest.builder()
                .latitude(27.7)
                .longitude(85.3)
                .accuracyM(50)
                .build();

        service.captureAndStore(citizenId, wardId, capturedBy, request);

        verify(citizenGisRepository).save(captor.capture());
        assertThat(captor.getValue().getWardId()).isEqualTo(wardId);
        assertThat(captor.getValue().getCitizenId()).isEqualTo(citizenId);
    }

    @Test
    void coordinateOrderIsLongitudeThenLatitude() {
        service = newService();
        ArgumentCaptor<CitizenGis> captor = ArgumentCaptor.forClass(CitizenGis.class);
        when(citizenGisRepository.save(any(CitizenGis.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        GpsCaptureRequest request = GpsCaptureRequest.builder()
                .latitude(27.7172)   // Kathmandu-ish
                .longitude(85.3240)
                .accuracyM(20)
                .build();

        service.captureAndStore(citizenId, wardId, capturedBy, request);

        verify(citizenGisRepository).save(captor.capture());
        // JTS Point: x = longitude, y = latitude — do not swap
        assertThat(captor.getValue().getLocation().getX()).isEqualTo(85.3240);
        assertThat(captor.getValue().getLocation().getY()).isEqualTo(27.7172);
    }
}
