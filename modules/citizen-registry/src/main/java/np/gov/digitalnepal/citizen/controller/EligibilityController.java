package np.gov.digitalnepal.citizen.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digitalnepal.citizen.dto.EligibilityResponse;
import np.gov.digitalnepal.citizen.service.EligibilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for benefit eligibility.
 * Base path: /api/v1/citizens/{id}/eligibility
 */
@RestController
@RequestMapping("/v1/citizens")
@RequiredArgsConstructor
@Slf4j
public class EligibilityController {

    private final EligibilityService eligibilityService;

    /**
     * GET /api/v1/citizens/{id}/eligibility
     * Runs the eligibility engine and returns all benefit programmes
     * and ID card types the citizen qualifies for.
     *
     * Access: WARD_ADMIN, LOCAL_BODY_ADMIN
     * RLS ensures citizen is within the admin's geographic scope.
     */
    @GetMapping("/{id}/eligibility")
    @PreAuthorize("hasAnyRole('WARD_ADMIN', 'LOCAL_BODY_ADMIN')")
    public ResponseEntity<EligibilityResponse> getEligibility(@PathVariable UUID id) {
        log.info("Eligibility check requested for citizen: {}", id);
        EligibilityResponse response = eligibilityService.evaluate(id);
        return ResponseEntity.ok(response);
    }
}
