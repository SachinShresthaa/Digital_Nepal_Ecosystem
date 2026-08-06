package np.gov.digital.platformsync.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import np.gov.digital.platformsync.dto.SyncBatchRequestDTO;
import np.gov.digital.platformsync.dto.SyncResponseDTO;
import np.gov.digital.platformsync.service.SyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sync")
@Validated
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;


    @PostMapping("/submit")
    public ResponseEntity<SyncResponseDTO> submitSyncBatch(
            @Valid @RequestBody SyncBatchRequestDTO requestDTO) {

        SyncResponseDTO response = syncService.processBatch(requestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}