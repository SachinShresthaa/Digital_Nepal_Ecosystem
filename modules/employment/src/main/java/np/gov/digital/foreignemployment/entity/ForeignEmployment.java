package np.gov.digital.foreignemployment.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "foreign_employment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForeignEmployment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "citizen_id", nullable = false)
    private UUID citizenId;

    @Column(name = "country_code", nullable = false, length = 5)
    private String countryCode;

    @Column(name = "country_name", nullable = false, length = 100)
    private String countryName;

    @Column(name = "visa_type", nullable = false, length = 30)
    private String visaType;

    @Column(name = "employer_name", length = 300)
    private String employerName;

    @Column(name = "job_category", length = 50)
    private String jobCategory;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @Column(name = "expected_return")
    private LocalDate expectedReturn;

    @Column(name = "remittance_band", length = 20)
    private String remittanceBand;

    @Column(name = "remittance_channel", length = 30)
    private String remittanceChannel;

    @Column(name = "foreign_phone_enc")
    private String foreignPhoneEnc;

    @Column(name = "manager_citizen_id")
    private UUID managerCitizenId;

    @Column(name = "doe_registered")
    private Boolean doeRegistered = false;

    @Column(name = "insured")
    private Boolean insured = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "return_date_actual")
    private LocalDate returnDateActual;

    @Column(name = "return_reason")
    private String returnReason;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
