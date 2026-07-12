package np.gov.digitalnepal.citizen.repository;

import np.gov.digitalnepal.citizen.entity.DisabilityProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisabilityProfileRepository extends JpaRepository<DisabilityProfile, UUID> {

    Optional<DisabilityProfile> findByCitizenId(UUID citizenId);

    boolean existsByCitizenId(UUID citizenId);
}
