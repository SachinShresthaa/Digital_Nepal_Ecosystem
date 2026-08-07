package np.gov.digital.platformsync.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import np.gov.digital.platformsync.entity.SyncRecord;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SyncBatchItemReader {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @StepScope
    public JpaPagingItemReader<SyncRecord> syncRecordReader(
            @Value("#{jobParameters['batchId']}") String batchId) {

        JpaPagingItemReader<SyncRecord> reader = new JpaPagingItemReader<>();

        reader.setName("syncRecordReader");
        reader.setEntityManagerFactory(entityManagerFactory);

        reader.setQueryString(
                "SELECT s FROM SyncRecord s " +
                        "WHERE s.batchId = :batchId " +
                        "AND s.status = 'PENDING' " +
                        "ORDER BY s.createdAt ASC"
        );

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("batchId", UUID.fromString(batchId));

        reader.setParameterValues(parameters);

        reader.setPageSize(50);

        return reader;
    }
}