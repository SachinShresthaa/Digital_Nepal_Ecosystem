package np.gov.digital.platformgrievance.repository;

import np.gov.digital.platformgrievance.entity.GrievanceEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GrievanceEventRepository extends JpaRepository<GrievanceEvent, UUID> {

    List<GrievanceEvent> findByGrievanceIdOrderByCreatedAtAsc(UUID grievanceId);
}