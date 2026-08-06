package np.gov.digital.auth.service;

import lombok.RequiredArgsConstructor;
import np.gov.digital.auth.dto.*;
import np.gov.digital.auth.entity.RefreshToken;
import np.gov.digital.auth.entity.User;
import np.gov.digital.auth.repository.UserRepository;
import np.gov.digital.auth.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken storedToken =
                refreshTokenService.validateRefreshToken(request.getRefreshToken());

        User user = storedToken.getUser();

        refreshTokenService.revokeToken(storedToken);

        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        String accessToken = jwtService.generateAccessToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    public void logout(LogoutRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        request.getRefreshToken());

        refreshTokenService.revokeToken(refreshToken);
    }

    public UserProfileResponse me(Authentication authentication) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .wardId(user.getWardId())
                .municipalityId(user.getMunicipalityId())
                .provinceId(user.getProvinceId())
                .build();
    }
}