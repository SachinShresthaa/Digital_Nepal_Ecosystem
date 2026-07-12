package np.gov.digital.employment.dto;

import lombok.Data;
import np.gov.digital.employment.Enum.EmploymentCategory;

import java.util.Map;
import java.util.UUID;

@Data
public class EmploymentRequest {

    public UUID citizenId;

    public EmploymentCategory category;

    public Map<String, Object> subFields;

    public String incomeBand;

    public UUID updatedBy;
}