package np.gov.digital.employment.repository;



import np.gov.digital.employment.entity.EmploymentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmploymentRepository extends JpaRepository<EmploymentProfile, UUID> {

    Optional<EmploymentProfile> findByCitizenId(UUID citizenId);
}