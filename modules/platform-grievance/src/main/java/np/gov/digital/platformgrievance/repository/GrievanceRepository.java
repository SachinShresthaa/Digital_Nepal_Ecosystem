package np.gov.digital.platformgrievance.repository;

import np.gov.digital.platformgrievance.entity.Grievance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GrievanceRepository extends JpaRepository<Grievance, UUID> {

    boolean existsByTrackingCode(String trackingCode);

    Optional<Grievance> findByTrackingCode(String trackingCode);
}
