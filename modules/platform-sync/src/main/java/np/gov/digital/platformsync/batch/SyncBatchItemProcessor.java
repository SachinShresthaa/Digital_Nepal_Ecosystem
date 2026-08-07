package np.gov.digital.platformsync.batch;



import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import np.gov.digital.citizen.dto.CitizenRegistrationRequest;
import np.gov.digital.citizen.entity.Citizen;
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

        CitizenRecordDTO dto =
                objectMapper.readValue(
                        syncRecord.getPayload(),
                        CitizenRecordDTO.class
                );

        Optional<Citizen> existingCitizen =
                citizenRepository.findByLocalRecordId(dto.getLocalRecordId());

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

        Citizen citizen = existingCitizen.get();

        // ----------------------------------------------------
        // VERSION MATCH
        // ----------------------------------------------------
        if (citizen.getVersionNumber().equals(dto.getVersionNumber())) {

            CitizenRegistrationRequest request =
                    citizenMapper.toRegistrationRequest(
                            dto,
                            dto.getDeviceId()
                    );

            // TODO: Replace with citizenService.updateCitizen(request)
            // when update functionality is implemented.
            citizenService.registerCitizen(request);

            syncRecord.setStatus(SyncRecordStatus.SYNCED);
            syncRecord.setProcessedAt(Instant.now());

            return syncRecord;
        }

        // ----------------------------------------------------
        // VERSION CONFLICT
        // ----------------------------------------------------
        SyncConflictRegistry conflict = new SyncConflictRegistry();

        conflict.setCitizenId(citizen.getId());
        conflict.setSubmittingUserId(null);
        conflict.setDeviceId(dto.getDeviceId());
        conflict.setServerVersion(citizen.getVersionNumber());
        conflict.setDeviceVersion(dto.getVersionNumber());
        conflict.setConflictingData(syncRecord.getPayload());
        conflict.setResolutionStatus("PENDING_REVIEW");

        conflictRepository.save(conflict);

        syncRecord.setStatus(SyncRecordStatus.CONFLICT);
        syncRecord.setProcessedAt(Instant.now());

        return syncRecord;
    }
}