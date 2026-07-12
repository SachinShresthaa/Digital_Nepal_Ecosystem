package np.gov.digitalnepal.citizen.repository;

import np.gov.digitalnepal.citizen.entity.Citizen;
import np.gov.digitalnepal.citizen.enums.SyncStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, UUID> {

    Optional<Citizen> findByNidHashAndIsActiveTrue(String nidHash);

    boolean existsByNidHashAndIsActiveTrue(String nidHash);

    Optional<Citizen> findByCitizenshipNoNormAndIsActiveTrue(String citizenshipNoNorm);

    Page<Citizen> findByWardIdAndIsActiveTrue(UUID wardId, Pageable pageable);

    @Query("SELECT c FROM Citizen c WHERE c.ward.id = :wardId AND c.syncStatus = :status AND c.isActive = true")
    Page<Citizen> findByWardIdAndSyncStatus(
            @Param("wardId") UUID wardId,
            @Param("status") SyncStatus status,
            Pageable pageable
    );

    Optional<Citizen> findByLocalRecordId(UUID localRecordId);

    @Query("SELECT c FROM Citizen c WHERE c.isAsyncVerified = false AND c.isActive = true AND c.ward.id = :wardId")
    Page<Citizen> findPendingNidVerification(@Param("wardId") UUID wardId, Pageable pageable);

    @Query("SELECT c.versionNumber FROM Citizen c WHERE c.id = :id")
    Optional<Integer> findVersionById(@Param("id") UUID id);
}