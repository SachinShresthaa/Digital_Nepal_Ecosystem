package np.gov.digital.platformsync.repository;

import np.gov.digital.platformsync.entity.SyncBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SyncBatchRepository extends JpaRepository<SyncBatch, UUID> {
}