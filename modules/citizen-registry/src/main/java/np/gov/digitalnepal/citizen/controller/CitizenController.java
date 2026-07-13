package np.gov.digitalnepal.citizen.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.gov.digitalnepal.citizen.dto.CitizenRegistrationRequest;
import np.gov.digitalnepal.citizen.dto.CitizenRegistrationResponse;
import np.gov.digitalnepal.citizen.exception.DuplicateNidException;
import np.gov.digitalnepal.citizen.exception.WardNotFoundException;
import np.gov.digitalnepal.citizen.service.CitizenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/citizens")
@RequiredArgsConstructor
@Slf4j
public class CitizenController {
    private final CitizenService citizenService;

    // POST /api/v1/citizens/register
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('WARD_ADMIN', 'LOCAL_BODY_ADMIN')")
    public ResponseEntity<?> registerCitizen(
            @Valid @RequestBody CitizenRegistrationRequest request) {

        log.info("Registration request received for ward: {}", request.getWardId());

        CitizenRegistrationResponse response = citizenService.registerCitizen(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // EXCEPTION HANDLERS
    @ExceptionHandler(DuplicateNidException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateNid(DuplicateNidException ex) {
        log.warn("Duplicate NID registration blocked");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "DUPLICATE_NID",
                "message", "An active citizen is already registered with this NID",
                "status", "409"
        ));
    }

    // 404 Not Found — ward does not exist.
    @ExceptionHandler(WardNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleWardNotFound(WardNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "WARD_NOT_FOUND",
                "message", ex.getMessage(),
                "status", "404"
        ));
    }
}
