package np.gov.digital.platformsync.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardSyncStatusResponseDTO {

    private UUID wardId;

    private Integer totalBatches;

    private Integer processingBatches;

    private Integer completedBatches;

    private Integer failedBatches;

    private Integer conflictBatches;
}


