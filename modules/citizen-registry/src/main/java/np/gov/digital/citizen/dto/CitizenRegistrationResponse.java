package np.gov.digital.citizen.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Output DTO for POST /api/v1/citizens/register
 * Returns only what the Ward Admin needs to confirm registration.
 * Does NOT expose encrypted fields, internal IDs, or audit data.
 * NEVER return the raw Citizen JPA entity — always map to this DTO.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitizenRegistrationResponse {
    private UUID citizenId;

    private String nameNp;
    private String nameEn;

    private UUID wardId;

    private Boolean nidVerified;

    private String syncStatus;

    private Instant registeredAt;

    private String message;
}
