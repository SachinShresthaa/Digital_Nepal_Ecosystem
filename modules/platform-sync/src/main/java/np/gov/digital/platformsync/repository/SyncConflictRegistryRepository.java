package np.gov.digital.platformsync.repository;

import np.gov.digital.platformsync.entity.SyncConflictRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SyncConflictRegistryRepository extends JpaRepository<SyncConflictRegistry, UUID> {

    List<SyncConflictRegistry> findByCitizenId(UUID citizenId);

}