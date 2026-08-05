package np.gov.digital.citizen.repository;

import np.gov.digital.citizen.entity.FamilyLink;
import np.gov.digital.citizen.enums.LinkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FamilyLinkRepository extends JpaRepository<FamilyLink, UUID> {
    // Get all family links for a citizen — used by GET /api/v1/citizens/{id}/family
    List<FamilyLink> findByCitizenId(UUID citizenId);

    // Find all PENDING links that are waiting for a citizen with this normalized citizenship number to register.
    @Query("SELECT fl FROM FamilyLink fl WHERE fl.relatedCitizenshipNo = :citizenshipNoNorm AND fl.linkStatus = :status")
    List<FamilyLink> findPendingByRelatedCitizenshipNo(
            @Param("citizenshipNoNorm") String citizenshipNoNorm,
            @Param("status") LinkStatus status
    );

    // Check if a specific family link already exists — prevents duplicates.
    boolean existsByCitizenIdAndRelatedCitizenshipNo(UUID citizenId, String relatedCitizenshipNo);
}
