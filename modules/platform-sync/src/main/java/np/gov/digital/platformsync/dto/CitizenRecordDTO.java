package np.gov.digital.platformsync.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import np.gov.digital.citizen.enums.ConsentChannel;
import np.gov.digital.citizen.enums.DigitalLiteracy;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitizenRecordDTO {

    /**
     * Client-generated primary key.
     * Used by the mobile app for offline synchronization.
     */
    private UUID citizenId;

    // GEOGRAPHIC SCOPE
    private UUID wardId;

    // IDENTITY
    private String nid;
    private String citizenshipNo;
    private String passportNo;

    // NAME
    private String nameNp;
    private String nameEn;

    // DEMOGRAPHICS
    private String dob;
    private String sex;
    private String bloodGroup;
    private String religion;
    private String ethnicity;
    private String motherTongue;
    private String tole;

    // CONTACT
    private String phone;
    private String phoneAlt;
    private String email;

    // DIGITAL PROFILE
    private DigitalLiteracy digitalLiteracy;
    private Boolean hasSmartphone;
    private String photoUrl;

    // CONSENT
    private ConsentChannel consentChannel;

    // REGISTRATION
    private String registrationChannel;

    // OFFLINE SYNC
    private UUID localRecordId;
    private String deviceId;
    // OFFLINE VERSIONING
    private Integer versionNumber;
}