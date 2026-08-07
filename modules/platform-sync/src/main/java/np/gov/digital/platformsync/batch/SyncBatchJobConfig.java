package np.gov.digital.platformsync.batch;



import lombok.RequiredArgsConstructor;
import np.gov.digital.platformsync.entity.SyncRecord;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SyncBatchJobConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final JpaPagingItemReader<SyncRecord> syncRecordReader;

    private final SyncBatchItemProcessor processor;

    private final SyncBatchItemWriter writer;

    private final SyncBatchRetryListener syncBatchRetryListener;
    @Bean
    public Step syncStep() {

        return new StepBuilder("syncStep", jobRepository)

                .<SyncRecord, SyncRecord>chunk(50, transactionManager)

                .reader(syncRecordReader)

                .processor(processor)

                .writer(writer)

                .listener(syncBatchRetryListener)

                .faultTolerant()

                .retry(Exception.class)

                .retryLimit(3)

                .build();


    }

    @Bean
    public Job syncJob() {

        return new JobBuilder("syncJob", jobRepository)

                .start(syncStep())

                .build();
    }
}