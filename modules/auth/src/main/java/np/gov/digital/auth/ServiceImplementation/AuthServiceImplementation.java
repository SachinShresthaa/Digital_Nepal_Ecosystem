package np.gov.digital.auth.ServiceImplementation;

import np.gov.digital.auth.Dto.UserDto;
import np.gov.digital.auth.Services.AuthService;
import np.gov.digital.auth.configuration.JwtProvider;
import np.gov.digital.auth.exception.UserException;
import np.gov.digital.auth.mapper.UserMapper;
import np.gov.digital.auth.modal.User;
import np.gov.digital.auth.repository.UserRepository;
import np.gov.digital.auth.response.AuthResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImplementation implements AuthService {


    @Override
    public AuthResponse signup(UserDto userDto) throws UserException {
        return null;
    }

    @Override
    public AuthResponse login(UserDto userDto) throws UserException {
        return null;
    }
}
