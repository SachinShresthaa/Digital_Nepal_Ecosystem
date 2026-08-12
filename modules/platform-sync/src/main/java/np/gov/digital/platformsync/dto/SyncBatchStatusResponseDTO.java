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
public class SyncBatchStatusResponseDTO {

    private UUID batchId;

    private UUID wardId;

    private String deviceId;

    private Integer recordCount;

    private Integer conflictCount;

    private String status;

    private Instant submittedAt;

    private Instant completedAt;

    private String errorMessage;
}

