package np.gov.digital.platformsync.batch;



import lombok.RequiredArgsConstructor;
import np.gov.digital.platformsync.entity.SyncRecord;
import np.gov.digital.platformsync.enums.SyncRecordStatus;
import np.gov.digital.platformsync.repository.SyncRecordRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SyncBatchItemWriter implements ItemWriter<SyncRecord> {

    private final SyncRecordRepository syncRecordRepository;

    @Override
    public void write(Chunk<? extends SyncRecord> chunk) {

        for (SyncRecord record : chunk.getItems()) {

            // Dead-letter after 3 retries
            if (record.getRetryCount() >= 3 &&
                    record.getStatus() != SyncRecordStatus.SYNCED &&
                    record.getStatus() != SyncRecordStatus.CONFLICT) {

                record.setStatus(SyncRecordStatus.FAILED);
            }

            syncRecordRepository.save(record);
        }
    }
}
