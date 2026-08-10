package np.gov.digital.platformsync.repository;

import np.gov.digital.platformsync.entity.SyncBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SyncBatchRepository extends JpaRepository<SyncBatch, UUID> {

    List<SyncBatch> findByWardId(UUID wardId);

    long countByWardId(UUID wardId);

    long countByWardIdAndStatus(UUID wardId, String status);

}

