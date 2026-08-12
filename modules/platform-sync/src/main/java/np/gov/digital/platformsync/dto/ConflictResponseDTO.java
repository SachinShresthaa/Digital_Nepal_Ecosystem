package np.gov.digital.platformsync.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConflictResponseDTO {

    private UUID id;

    private UUID citizenId;

    private UUID submittingUserId;

    private String deviceId;

    private Integer serverVersion;

    private Integer deviceVersion;

    private String conflictingData;

    private String resolutionStatus;

    private UUID resolvedBy;

    private Instant resolvedAt;
}
