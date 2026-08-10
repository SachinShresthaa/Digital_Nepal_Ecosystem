package np.gov.digital.platformsync.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import np.gov.digital.platformsync.dto.SyncBatchRequestDTO;
import np.gov.digital.platformsync.dto.SyncBatchStatusResponseDTO;
import np.gov.digital.platformsync.dto.SyncResponseDTO;
import np.gov.digital.platformsync.dto.WardSyncStatusResponseDTO;
import np.gov.digital.platformsync.service.SyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @GetMapping("/batch/{id}/status")
    public ResponseEntity<SyncBatchStatusResponseDTO> getBatchStatus(
            @PathVariable UUID id) {

        SyncBatchStatusResponseDTO response =
                syncService.getBatchStatus(id);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/status/{wardId}")
    public ResponseEntity<WardSyncStatusResponseDTO> getWardSyncStatus(
            @PathVariable UUID wardId) {

        WardSyncStatusResponseDTO response =
                syncService.getWardSyncStatus(wardId);

        return ResponseEntity.ok(response);
    }
}