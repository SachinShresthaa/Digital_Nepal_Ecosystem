package np.gov.digital.auth.controller;

import np.gov.digital.auth.Dto.UserDto;
import np.gov.digital.auth.Services.AuthService;
import np.gov.digital.auth.exception.UserException;
import np.gov.digital.auth.response.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signupHandeler(
            @RequestBody UserDto userDto
            ) throws UserException {

        return ResponseEntity.ok(authService.signup(userDto));

    }



    @PostMapping("/login")
    public ResponseEntity<AuthResponse> LoginHandeler(
            @RequestBody UserDto userDto
    ) throws UserException {

        return ResponseEntity.ok(authService.login(userDto));

    }
}
