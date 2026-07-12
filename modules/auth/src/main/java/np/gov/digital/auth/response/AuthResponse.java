package np.gov.digital.auth.response;

import lombok.Data;
import np.gov.digital.auth.Dto.UserDto;

@Data
public class AuthResponse {

    private String token;
    private String message;
    private UserDto user;


}
