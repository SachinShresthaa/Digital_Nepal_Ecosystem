
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
public class SyncResponseDTO {

    /**
     * The client-generated batch ID.
     */
    private UUID batchId;

    /**
     * Sync result status.
     *
     * Examples:
     * SUCCESS
     * DUPLICATE_BATCH
     * FAILED
     */
    private String status;

    /**
     * Explicit API error code.
     *
     * Example:
     * ERR_SYNC_BATCH_DUPLICATE
     */
    private String errorCode;

    /**
     * Human-readable response message.
     */
    private String message;
}

