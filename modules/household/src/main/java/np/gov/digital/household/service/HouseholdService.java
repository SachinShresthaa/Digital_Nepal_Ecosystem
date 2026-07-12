package np.gov.digital.household.service;



import np.gov.digital.household.dto.HouseholdRequest;
import np.gov.digital.household.dto.HouseholdResponse;

import java.util.List;
import java.util.UUID;

public interface HouseholdService {

    HouseholdResponse create(HouseholdRequest request);

    HouseholdResponse getById(UUID id);

    List<HouseholdResponse> getByWardId(UUID wardId);

    List<HouseholdResponse> getByHeadCitizenId(UUID headCitizenId);

    HouseholdResponse update(UUID id, HouseholdRequest request);

    void delete(UUID id);
}
