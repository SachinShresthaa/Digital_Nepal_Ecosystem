package np.gov.digital.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserProfileResponse {

    private UUID id;

    private String fullName;

    private String email;

    private String role;

    private UUID wardId;

    private UUID municipalityId;

    private UUID provinceId;

}