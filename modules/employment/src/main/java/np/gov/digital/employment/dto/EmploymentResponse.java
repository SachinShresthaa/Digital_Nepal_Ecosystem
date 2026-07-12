package np.gov.digital.employment.dto;

import np.gov.digital.employment.Enum.EmploymentCategory;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class EmploymentResponse {

    public UUID id;
    public UUID citizenId;

    public EmploymentCategory category;

    public Map<String, Object> subFields;
    public String incomeBand;
    public Instant updatedAt;
    public UUID updatedBy;
}