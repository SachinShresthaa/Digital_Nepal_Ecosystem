package np.gov.digital.platformsync.batch;



import lombok.RequiredArgsConstructor;
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

    @Override
    public void beforeProcess(SyncRecord item) {
        // nothing
    }

    @Override
    public void afterProcess(SyncRecord item, SyncRecord result) {
        // nothing
    }

    @Override
    public void onProcessError(SyncRecord item, Exception e) {

        item.setRetryCount(item.getRetryCount() + 1);
        item.setErrorMessage(e.getMessage());

        if (item.getRetryCount() >= 3) {
            item.setStatus(SyncRecordStatus.FAILED);
        }

        syncRecordRepository.save(item);
    }
}
