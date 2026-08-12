package np.gov.digital.platformgrievance.util;

import lombok.RequiredArgsConstructor;
import np.gov.digital.platformgrievance.repository.GrievanceRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Year;

@Component
@RequiredArgsConstructor
public class TrackingCodeGenerator {

    private static final int SUFFIX_DIGITS = 6;
    private static final int MAX_ATTEMPTS = 10;

    private final GrievanceRepository grievanceRepository;
    private final SecureRandom random = new SecureRandom();

    public String generate() {
        String year = String.valueOf(Year.now().getValue());

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String suffix = String.format("%0" + SUFFIX_DIGITS + "d",
                    random.nextInt(1_000_000));
            String candidate = "GRV-" + year + "-" + suffix;

            if (!grievanceRepository.existsByTrackingCode(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "TrackingCodeGenerator: failed to generate a unique tracking code after "
                        + MAX_ATTEMPTS + " attempts");
    }
}
