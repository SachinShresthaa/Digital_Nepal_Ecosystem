package np.gov.digital.auth.repository;

import np.gov.digital.auth.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CitizenRepository
        extends JpaRepository<Citizen, UUID> {
}
