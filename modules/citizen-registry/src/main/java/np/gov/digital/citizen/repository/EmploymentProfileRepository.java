package np.gov.digital.citizen.repository;

import np.gov.digital.citizen.entity.EmploymentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmploymentProfileRepository extends JpaRepository<EmploymentProfile, UUID> {

    Optional<EmploymentProfile> findByCitizenId(UUID citizenId);

    boolean existsByCitizenId(UUID citizenId);
}
