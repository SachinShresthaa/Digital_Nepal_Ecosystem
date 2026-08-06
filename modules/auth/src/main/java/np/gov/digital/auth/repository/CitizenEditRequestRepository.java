package np.gov.digital.auth.repository;

import np.gov.digital.auth.entity.CitizenEditRequest;
import np.gov.digital.auth.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CitizenEditRequestRepository
        extends JpaRepository<CitizenEditRequest, UUID> {

    List<CitizenEditRequest> findByStatus(ApprovalStatus status);

}