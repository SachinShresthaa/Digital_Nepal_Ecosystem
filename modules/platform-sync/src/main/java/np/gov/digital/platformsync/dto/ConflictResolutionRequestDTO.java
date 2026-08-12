package np.gov.digital.platformsync.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class ConflictResolutionRequestDTO {

    /**
     * User who is resolving the conflict.
     */
    private UUID resolvedBy;

    /**
     * Resolution selected by the Local Body Admin.
     *
     * Allowed values:
     * SERVER - keep server version
     * DEVICE - accept device version
     * MERGE  - merge conflicting data
     */
    @NotBlank(message = "Resolution choice is required")
    @Pattern(
            regexp = "SERVER|DEVICE|MERGE",
            message = "Resolution must be SERVER, DEVICE, or MERGE"
    )
    private String resolution;

    /**
     * Required only when resolution = MERGE.
     * Contains the final merged citizen data as JSON.
     */
    private String mergedData;
}
