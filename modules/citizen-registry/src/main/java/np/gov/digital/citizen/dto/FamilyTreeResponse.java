package np.gov.digital.citizen.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

// Response DTO for GET /api/v1/citizens/{id}/family
// Returns the citizen + all their linked family members.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyTreeResponse {
    // The citizen whose family tree was requested
    private UUID citizenId;
    private String nameNp;
    private String nameEn;

    // All family links - parents, spouse, children
    private List<FamilyLinkDto> familyLinks;

    // Total number of linked relatives
    private int totalLinks;

    // How many links are still pending (related person not registered yet)
    private long pendingLinks;
}
