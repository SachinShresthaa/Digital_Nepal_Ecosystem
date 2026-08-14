package np.gov.digital.platformgrievance.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digital.platformgrievance.dto.GrievanceFileRequest;
import np.gov.digital.platformgrievance.dto.GrievanceResponse;
import np.gov.digital.platformgrievance.dto.GrievanceTransitionRequest;
import np.gov.digital.platformgrievance.service.GrievanceService;
import np.gov.digital.platformgrievance.service.GrievanceStateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/grievances")
@RequiredArgsConstructor
public class GrievanceController {

    private final GrievanceService grievanceService;
    private final GrievanceStateService grievanceStateService;

    @PostMapping
    @PreAuthorize("hasAnyRole('WARD_ADMIN','LOCAL_BODY_ADMIN')")
    public ResponseEntity<GrievanceResponse> file(
            @Valid @RequestBody GrievanceFileRequest request) {
        log.info("POST /api/v1/grievances citizen={} category={}",
                request.getCitizenId(), request.getCategory());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(grievanceService.fileGrievance(request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('WARD_ADMIN','LOCAL_BODY_ADMIN')")
    public ResponseEntity<GrievanceResponse> transition(
            @PathVariable UUID id,
            @Valid @RequestBody GrievanceTransitionRequest request) {
        log.info("PATCH /api/v1/grievances/{}/status → {}", id, request.getTargetStatus());
        return ResponseEntity.ok(grievanceStateService.transition(id, request));
    }
}