package np.gov.digital.household.repository;



import np.gov.digital.household.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HouseholdRepository extends JpaRepository<Household, UUID> {

    List<Household> findByWardId(UUID wardId);

    List<Household> findByHeadCitizenId(UUID headCitizenId);
}
