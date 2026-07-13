package np.gov.digitalnepal.citizen.dto;

import lombok.*;
import np.gov.digitalnepal.citizen.enums.LinkStatus;
import np.gov.digitalnepal.citizen.enums.RelationType;

import java.util.UUID;

// Response DTO for a single family link node.
// Returned by GET /api/v1/citizens/{id}/family
// Never exposes encrypted fields or internal JPA entities.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyLinkDto {

    private UUID linkId;
    private RelationType relationType;

    // UUID of the related citizen - null if not registered yet
    private UUID relatedCitizenId;

    // Name as provided during registration
    private String relatedNameText;

    // LINKED — related citizen is registered, link confirmed
    // PENDING — related citizen not registered yet
    // UNRESOLVABLE — could not be matched
    private LinkStatus linkStatus;
}
