package np.gov.digitalnepal.citizen.dto;

import lombok.*;
import np.gov.digitalnepal.citizen.enums.IdCardType;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for GET /api/v1/citizens/{id}/eligibility
 * Returns all benefit programmes and ID card types the citizen qualifies for.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityResponse {

    private UUID citizenId;
    private String nameNp;
    private String nameEn;

    /** List of ID card types this citizen is eligible for */
    private List<EligibilityResult> eligibleCards;

    /** True if citizen qualifies for at least one benefit */
    private boolean hasEligibility;

    /** Total number of programmes citizen qualifies for */
    private int totalEligible;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EligibilityResult {

        private IdCardType cardType;

        /** Human readable explanation of why citizen qualifies */
        private String reason;

        /** True = eligible, False = not eligible */
        private boolean eligible;

        /** Why citizen does NOT qualify — null if eligible */
        private String ineligibilityReason;
    }
}
