package np.gov.digital.household.entity;



import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "household")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Household {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "ward_id", nullable = false)
    private UUID wardId;

    @Column(name = "head_citizen_id")
    private UUID headCitizenId;

    @Column(name = "house_type", length = 30)
    private String houseType;

    @Column(name = "construction_type", length = 30)
    private String constructionType;

    @Column(name = "room_count")
    private Short roomCount;

    @Column(name = "land_owned")
    private Boolean landOwned = false;

    @Column(name = "land_area_ropani", precision = 8, scale = 2)
    private BigDecimal landAreaRopani;

    @Column(name = "land_location", length = 300)
    private String landLocation;

    @Column(name = "electricity", length = 30)
    private String electricity;

    @Column(name = "water_source", length = 30)
    private String waterSource;

    @Column(name = "sanitation", length = 30)
    private String sanitation;

    @Column(name = "internet_access", length = 30)
    private String internetAccess;

    @Column(name = "has_bank_account")
    private Boolean hasBankAccount = false;

    @Column(name = "bank_name", length = 200)
    private String bankName;

    @Column(name = "monthly_income_band", length = 30)
    private String monthlyIncomeBand;

    @Column(name = "annual_income_band", length = 30)
    private String annualIncomeBand;

    @Column(name = "dependent_count")
    private Short dependentCount;

    @Column(name = "poverty_class", length = 30)
    private String povertyClass;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
