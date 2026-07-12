package np.gov.digital.foreignemployment.repository;




import np.gov.digital.foreignemployment.entity.ForeignEmployment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ForeignEmploymentRepository extends JpaRepository<ForeignEmployment, UUID> {

    List<ForeignEmployment> findByCitizenId(UUID citizenId);

    List<ForeignEmployment> findByIsActive(Boolean isActive);
}
