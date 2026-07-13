package np.gov.digital.foreignemployment.dto;



import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ForeignEmploymentRequest {

    private UUID citizenId;

    private String countryCode;
    private String countryName;

    private String visaType;

    private String employerName;
    private String jobCategory;

    private LocalDate departureDate;
    private LocalDate expectedReturn;

    private String remittanceBand;
    private String remittanceChannel;

    private String foreignPhoneEnc;

    private UUID managerCitizenId;

    private Boolean doeRegistered;
    private Boolean insured;

    private Boolean isActive;

    private LocalDate returnDateActual;
    private String returnReason;
}
