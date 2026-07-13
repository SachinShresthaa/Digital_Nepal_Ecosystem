package np.gov.digital.auth.Services;

import jdk.jshell.spi.ExecutionControl;
import np.gov.digital.auth.Dto.UserDto;
import np.gov.digital.auth.exception.UserException;
import np.gov.digital.auth.response.AuthResponse;

public interface AuthService {


    AuthResponse signup(UserDto userDto) throws UserException;
    AuthResponse login(UserDto userDto) throws UserException;

}
