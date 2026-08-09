
        package np.gov.digital.platformsync.batch;

import lombok.RequiredArgsConstructor;
import np.gov.digital.citizen.entity.Citizen;
import np.gov.digital.citizen.enums.SyncStatus;
import np.gov.digital.citizen.repository.CitizenRepository;
import np.gov.digital.platformsync.entity.SyncRecord;
import np.gov.digital.platformsync.enums.SyncRecordStatus;
import np.gov.digital.platformsync.repository.SyncRecordRepository;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SyncBatchRetryListener
        implements ItemProcessListener<SyncRecord, SyncRecord> {

    private final SyncRecordRepository syncRecordRepository;
    private final CitizenRepository citizenRepository;

    @Override
    public void beforeProcess(SyncRecord item) {
        // Nothing to do before processing
    }

    @Override
    public void afterProcess(
            SyncRecord item,
            SyncRecord result) {

        // Nothing to do after successful processing
    }

    @Override
    public void onProcessError(
            SyncRecord item,
            Exception e) {

        // ----------------------------------------------------
        // UPDATE RETRY INFORMATION
        // ----------------------------------------------------
        int retryCount = item.getRetryCount() + 1;

        item.setRetryCount(retryCount);
        item.setErrorMessage(e.getMessage());

        // ----------------------------------------------------
        // RETRY LIMIT REACHED
        // ----------------------------------------------------
        if (retryCount >= 3) {

            item.setStatus(SyncRecordStatus.FAILED);

            // Find the corresponding citizen
            Citizen citizen =
                    citizenRepository
                            .findByLocalRecordId(
                                    item.getLocalRecordId()
                            )
                            .orElse(null);

            // Mark citizen as FAILED
            if (citizen != null) {

                citizen.setSyncStatus(SyncStatus.FAILED);

                citizenRepository.save(citizen);
            }
        }

        // Save SyncRecord regardless of whether retry
        // limit has been reached.
        syncRecordRepository.save(item);
    }
}

