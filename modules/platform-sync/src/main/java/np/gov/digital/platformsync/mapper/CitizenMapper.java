package np.gov.digital.platformsync.mapper;



import np.gov.digital.citizen.dto.CitizenRegistrationRequest;
import np.gov.digital.platformsync.dto.CitizenRecordDTO;
import org.springframework.stereotype.Component;

@Component
public class CitizenMapper {

    public CitizenRegistrationRequest toRegistrationRequest(
            CitizenRecordDTO record,
            String deviceId) {

        return CitizenRegistrationRequest.builder()

                // Geographic Scope
                .wardId(record.getWardId())

                // Identity
                .nid(record.getNid())
                .citizenshipNo(record.getCitizenshipNo())
                .passportNo(record.getPassportNo())

                // Name
                .nameNp(record.getNameNp())
                .nameEn(record.getNameEn())

                // Demographics
                .dob(record.getDob())
                .sex(record.getSex())
                .bloodGroup(record.getBloodGroup())
                .religion(record.getReligion())
                .ethnicity(record.getEthnicity())
                .motherTongue(record.getMotherTongue())
                .tole(record.getTole())

                // Contact
                .phone(record.getPhone())
                .phoneAlt(record.getPhoneAlt())
                .email(record.getEmail())

                // Digital Profile
                .digitalLiteracy(record.getDigitalLiteracy())
                .hasSmartphone(record.getHasSmartphone())
                .photoUrl(record.getPhotoUrl())

                // Consent
                .consentChannel(record.getConsentChannel())

                // Registration
                .registrationChannel(record.getRegistrationChannel())

                // Offline Sync
                .localRecordId(record.getLocalRecordId())
                .deviceId(deviceId)

                .build();
    }
}
