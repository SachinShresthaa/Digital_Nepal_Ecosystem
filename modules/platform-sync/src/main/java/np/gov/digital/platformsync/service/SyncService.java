package np.gov.digital.platformsync.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import np.gov.digital.citizen.dto.CitizenRegistrationRequest;
import np.gov.digital.citizen.service.CitizenService;
import np.gov.digital.platformsync.dto.CitizenRecordDTO;
import np.gov.digital.platformsync.dto.SyncBatchRequestDTO;
import np.gov.digital.platformsync.dto.SyncResponseDTO;
import np.gov.digital.platformsync.entity.SyncBatch;
import np.gov.digital.platformsync.repository.SyncBatchRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final SyncBatchRepository syncBatchRepository;
    private final CitizenService citizenService;

    @Transactional
    public SyncResponseDTO processBatch(SyncBatchRequestDTO requestDTO) {

        // ----------------------------------------------------
        // IDEMPOTENCY CHECK
        // ----------------------------------------------------
        if (syncBatchRepository.existsById(requestDTO.getBatchId())) {

            return SyncResponseDTO.builder()
                    .batchId(requestDTO.getBatchId())
                    .status("DUPLICATE_BATCH")
                    .message("Batch already processed.")
                    .build();
        }

        // ----------------------------------------------------
        // CREATE SYNC BATCH
        // ----------------------------------------------------
        SyncBatch batch = SyncBatch.builder()
                .batchId(requestDTO.getBatchId())
                .wardId(requestDTO.getWardId())
                .submittedBy(requestDTO.getSubmittedBy())
                .deviceId(requestDTO.getDeviceId())
                .recordCount(requestDTO.getRecords().size())
                .conflictCount(0)
                .status("PROCESSING")
                .submittedAt(Instant.now())
                .build();

        syncBatchRepository.save(batch);

        try {

            // ------------------------------------------------
            // PROCESS ALL RECORDS
            // ------------------------------------------------
            for (CitizenRecordDTO record : requestDTO.getRecords()) {

                CitizenRegistrationRequest citizenRequest =
                        CitizenRegistrationRequest.builder()

                                // Geographic Scope
                                .wardId(record.getWardId())

                                // Identity
                                .nid(record.getNid())
                                .citizenshipNo(record.getCitizenshipNo())
                                .passportNo(record.getPassportNo())

                                // Name
                                .nameNp(record.getNameNp())
                                .nameEn(record.getNameEn())

                                // Demographics
                                .dob(record.getDob())
                                .sex(record.getSex())
                                .bloodGroup(record.getBloodGroup())
                                .religion(record.getReligion())
                                .ethnicity(record.getEthnicity())
                                .motherTongue(record.getMotherTongue())
                                .tole(record.getTole())

                                // Contact
                                .phone(record.getPhone())
                                .phoneAlt(record.getPhoneAlt())
                                .email(record.getEmail())

                                // Digital Profile
                                .digitalLiteracy(record.getDigitalLiteracy())
                                .hasSmartphone(record.getHasSmartphone())
                                .photoUrl(record.getPhotoUrl())

                                // Consent
                                .consentChannel(record.getConsentChannel())

                                // Registration
                                .registrationChannel(record.getRegistrationChannel())

                                // Offline Sync
                                .localRecordId(record.getLocalRecordId())
                                .deviceId(requestDTO.getDeviceId())

                                .build();

                citizenService.registerCitizen(citizenRequest);
            }

            // ------------------------------------------------
            // MARK SUCCESS
            // ------------------------------------------------
            batch.setStatus("COMPLETED");
            batch.setCompletedAt(Instant.now());

            syncBatchRepository.save(batch);

            return SyncResponseDTO.builder()
                    .batchId(requestDTO.getBatchId())
                    .status("SUCCESS")
                    .message("Batch synchronized successfully.")
                    .build();

        } catch (Exception ex) {

            // ------------------------------------------------
            // MARK FAILURE
            // ------------------------------------------------
            batch.setStatus("FAILED");
            batch.setCompletedAt(Instant.now());
            batch.setErrorMessage(ex.getMessage());

            syncBatchRepository.save(batch);

            throw ex;
        }
    }
}