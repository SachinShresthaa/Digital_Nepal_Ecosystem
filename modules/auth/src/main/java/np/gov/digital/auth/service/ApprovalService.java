package np.gov.digital.auth.service;

import lombok.RequiredArgsConstructor;
import np.gov.digital.auth.dto.CitizenEditRequestDto;
import np.gov.digital.auth.entity.CitizenEditRequest;
import np.gov.digital.auth.enums.ApprovalStatus;
import np.gov.digital.auth.repository.CitizenEditRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final CitizenEditRequestRepository repository;

    public CitizenEditRequest submit(
            UUID submittedBy,
            CitizenEditRequestDto request) {

        CitizenEditRequest editRequest = CitizenEditRequest.builder()
                .citizenId(request.getCitizenId())
                .submittedBy(submittedBy)
                .changePayload(request.getChangePayload())
                .status(ApprovalStatus.PENDING_APPROVAL)
                .build();

        return repository.save(editRequest);
    }

    public CitizenEditRequest approve(UUID requestId, UUID approverId) {

        CitizenEditRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setApprovedBy(approverId);
        request.setStatus(ApprovalStatus.APPROVED);

        return repository.save(request);
    }

    public CitizenEditRequest reject(
            UUID requestId,
            UUID approverId,
            String reason) {

        CitizenEditRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setApprovedBy(approverId);
        request.setStatus(ApprovalStatus.REJECTED);
        request.setRejectionReason(reason);

        return repository.save(request);
    }

    public List<CitizenEditRequest> pendingRequests() {
        return repository.findByStatus(
                ApprovalStatus.PENDING_APPROVAL);
    }

}