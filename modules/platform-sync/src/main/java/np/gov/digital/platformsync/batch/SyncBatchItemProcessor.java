
        package np.gov.digital.platformsync.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import np.gov.digital.citizen.dto.CitizenRegistrationRequest;
import np.gov.digital.citizen.entity.Citizen;
import np.gov.digital.citizen.enums.SyncStatus;
import np.gov.digital.citizen.repository.CitizenRepository;
import np.gov.digital.citizen.service.CitizenService;
import np.gov.digital.platformsync.dto.CitizenRecordDTO;
import np.gov.digital.platformsync.entity.SyncConflictRegistry;
import np.gov.digital.platformsync.entity.SyncRecord;
import np.gov.digital.platformsync.enums.SyncRecordStatus;
import np.gov.digital.platformsync.mapper.CitizenMapper;
import np.gov.digital.platformsync.repository.SyncConflictRegistryRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SyncBatchItemProcessor
        implements ItemProcessor<SyncRecord, SyncRecord> {

    private final CitizenRepository citizenRepository;
    private final SyncConflictRegistryRepository conflictRepository;
    private final ObjectMapper objectMapper;
    private final CitizenMapper citizenMapper;
    private final CitizenService citizenService;

    @Override
    public SyncRecord process(SyncRecord syncRecord) throws Exception {

        // ----------------------------------------------------
        // READ SYNC PAYLOAD
        // ----------------------------------------------------
        CitizenRecordDTO dto =
                objectMapper.readValue(
                        syncRecord.getPayload(),
                        CitizenRecordDTO.class
                );

        // ----------------------------------------------------
        // FIND EXISTING CITIZEN
        // ----------------------------------------------------
        Optional<Citizen> existingCitizen =
                citizenRepository.findByLocalRecordId(
                        dto.getLocalRecordId()
                );

        // ----------------------------------------------------
        // NEW CITIZEN
        // ----------------------------------------------------
        if (existingCitizen.isEmpty()) {

            CitizenRegistrationRequest request =
                    citizenMapper.toRegistrationRequest(
                            dto,
                            dto.getDeviceId()
                    );

            citizenService.registerCitizen(request);

            syncRecord.setStatus(SyncRecordStatus.SYNCED);
            syncRecord.setProcessedAt(Instant.now());

            return syncRecord;
        }

        // Existing citizen found
        Citizen citizen = existingCitizen.get();

        // ----------------------------------------------------
        // VERSION MATCH
        // ----------------------------------------------------
        if (citizen.getVersionNumber().equals(dto.getVersionNumber())) {

            /*
             * The citizen already exists.
             *
             * DO NOT call registerCitizen() here because it performs
             * duplicate-NID validation and may reject this record.
             *
             * Actual citizen update functionality will be implemented
             * through citizenService.updateCitizen(...) later.
             */

            citizen.setSyncStatus(SyncStatus.SYNCED);
            citizenRepository.save(citizen);

            syncRecord.setStatus(SyncRecordStatus.SYNCED);
            syncRecord.setProcessedAt(Instant.now());

            return syncRecord;
        }

        // ----------------------------------------------------
        // VERSION CONFLICT
        // ----------------------------------------------------
        SyncConflictRegistry conflict =
                new SyncConflictRegistry();

        conflict.setCitizenId(citizen.getId());
        conflict.setSubmittingUserId(null);
        conflict.setDeviceId(dto.getDeviceId());
        conflict.setServerVersion(citizen.getVersionNumber());
        conflict.setDeviceVersion(dto.getVersionNumber());
        conflict.setConflictingData(syncRecord.getPayload());
        conflict.setResolutionStatus("PENDING_REVIEW");

        conflictRepository.save(conflict);

        // Mark the actual citizen as CONFLICT
        citizen.setSyncStatus(SyncStatus.CONFLICT);
        citizenRepository.save(citizen);

        // Mark the sync record as CONFLICT
        syncRecord.setStatus(SyncRecordStatus.CONFLICT);
        syncRecord.setProcessedAt(Instant.now());

        return syncRecord;
    }
}

