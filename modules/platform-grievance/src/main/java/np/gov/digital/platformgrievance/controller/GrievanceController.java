package np.gov.digital.platformgrievance.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digital.platformgrievance.dto.GrievanceFileRequest;
import np.gov.digital.platformgrievance.dto.GrievanceResponse;
import np.gov.digital.platformgrievance.service.GrievanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/grievances")
@RequiredArgsConstructor
public class GrievanceController {

    private final GrievanceService grievanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('WARD_ADMIN', 'LOCAL_BODY_ADMIN')")
    public ResponseEntity<GrievanceResponse> file(@Valid @RequestBody GrievanceFileRequest request) {
        log.info("GrievanceController: filing grievance for citizen={} category={}",
                request.getCitizenId(), request.getCategory());

        GrievanceResponse response = grievanceService.fileGrievance(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
