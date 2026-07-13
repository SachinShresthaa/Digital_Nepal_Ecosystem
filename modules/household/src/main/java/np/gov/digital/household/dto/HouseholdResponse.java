package np.gov.digital.household.dto;



import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class HouseholdResponse {

    private UUID id;
    private UUID wardId;
    private UUID headCitizenId;

    private String houseType;
    private String constructionType;
    private Short roomCount;

    private Boolean landOwned;
    private BigDecimal landAreaRopani;
    private String landLocation;

    private String electricity;
    private String waterSource;
    private String sanitation;
    private String internetAccess;

    private Boolean hasBankAccount;
    private String bankName;

    private String monthlyIncomeBand;
    private String annualIncomeBand;

    private Short dependentCount;
    private String povertyClass;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}