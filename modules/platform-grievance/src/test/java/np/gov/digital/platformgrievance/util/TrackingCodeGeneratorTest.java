package np.gov.digital.platformgrievance.util;

import np.gov.digital.platformgrievance.repository.GrievanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackingCodeGeneratorTest {

    @Mock
    private GrievanceRepository grievanceRepository;

    @Test
    void generatesCodeInGrvYearFormat() {
        when(grievanceRepository.existsByTrackingCode(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(false);

        TrackingCodeGenerator generator = new TrackingCodeGenerator(grievanceRepository);
        String code = generator.generate();

        String expectedPrefix = "GRV-" + Year.now().getValue() + "-";
        assertThat(code).startsWith(expectedPrefix);
        assertThat(code).matches("GRV-\\d{4}-\\d{6}");
    }

    @Test
    void retriesOnCollisionUntilUniqueCodeFound() {
        // First two attempts collide, third succeeds
        when(grievanceRepository.existsByTrackingCode(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(true, true, false);

        TrackingCodeGenerator generator = new TrackingCodeGenerator(grievanceRepository);
        String code = generator.generate();

        assertThat(code).matches("GRV-\\d{4}-\\d{6}");
    }

    @Test
    void givesUpAfterMaxAttemptsAllColliding() {
        when(grievanceRepository.existsByTrackingCode(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(true);

        TrackingCodeGenerator generator = new TrackingCodeGenerator(grievanceRepository);

        assertThatThrownBy(generator::generate)
                .isInstanceOf(IllegalStateException.class);
    }
}
