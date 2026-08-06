package np.gov.digital.citizen.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digital.citizen.dto.FamilyTreeResponse;
import np.gov.digital.citizen.service.FamilyLinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/citizens")
@RequiredArgsConstructor
@Slf4j
public class FamilyController {
    private final FamilyLinkService familyLinkService;

    // GET /api/v1/citizens/{id}/family
    // Returns the family tree for a citizen with all linked relatives and link statuses.
    // Access: All roles (Central, Province, Local Body, Ward)
    // RLS ensures each role only sees citizens within their geographic scope.

    @GetMapping("/{id}/family")
    @PreAuthorize("hasAnyRole('WARD_ADMIN', 'LOCAL_BODY_ADMIN', 'PROVINCE_ADMIN', 'CENTRAL_ADMIN')")
    public ResponseEntity<FamilyTreeResponse> getFamilyTree(@PathVariable UUID id) {
        log.info("Family tree requested for citizen: {}", id);
        FamilyTreeResponse response = familyLinkService.getFamilyTree(id);
        return ResponseEntity.ok(response);
    }
}
