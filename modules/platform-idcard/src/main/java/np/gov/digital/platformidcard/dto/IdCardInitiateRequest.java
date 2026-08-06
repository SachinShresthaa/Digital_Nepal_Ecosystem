package np.gov.digital.platformidcard.dto;

import java.util.UUID;

public record IdCardInitiateRequest(
        UUID   citizenId,
        String cardType    // DISABILITY or UNEMPLOYMENT
) {}
