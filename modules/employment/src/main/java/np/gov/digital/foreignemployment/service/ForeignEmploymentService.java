package np.gov.digital.foreignemployment.service;



import np.gov.digital.employment.dto.EmploymentRequest;
import np.gov.digital.foreignemployment.dto.ForeignEmploymentRequest;
import np.gov.digital.foreignemployment.dto.ForeignEmploymentResponse;

import java.util.List;
import java.util.UUID;

public interface ForeignEmploymentService {

    ForeignEmploymentResponse create(ForeignEmploymentRequest request);

    ForeignEmploymentResponse getById(UUID id);

    List<ForeignEmploymentResponse> getByCitizenId(UUID citizenId);

    ForeignEmploymentResponse update(UUID id, ForeignEmploymentRequest request);

    void delete(UUID id);
    void syncFromEmployment(EmploymentRequest request);
}
