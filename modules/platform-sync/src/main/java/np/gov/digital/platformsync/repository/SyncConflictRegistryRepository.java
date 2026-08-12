package np.gov.digital.platformsync.repository;

import np.gov.digital.platformsync.entity.SyncConflictRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SyncConflictRegistryRepository
        extends JpaRepository<SyncConflictRegistry, UUID> {

    List<SyncConflictRegistry> findByCitizenId(UUID citizenId);

    List<SyncConflictRegistry> findByResolutionStatus(String resolutionStatus);

    List<SyncConflictRegistry> findByResolutionStatusOrderByResolvedAtAsc(
            String resolutionStatus
    );
    @Query("""
    SELECT c
    FROM SyncConflictRegistry c
    JOIN Citizen citizen ON citizen.id = c.citizenId
    WHERE citizen.ward.id = :wardId
    AND c.resolutionStatus = :status
""")
    List<SyncConflictRegistry> findByWardIdAndResolutionStatus(
            @Param("wardId") UUID wardId,
            @Param("status") String status
    );
}