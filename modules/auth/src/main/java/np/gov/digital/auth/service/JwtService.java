package np.gov.digital.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import np.gov.digital.auth.config.JwtProperties;
import org.springframework.stereotype.Service;
import np.gov.digital.auth.entity.User;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        byte[] key = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(key);
    }

    public String generateAccessToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("user_id", user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("ward_id", user.getWardId())
                .claim("municipality_id", user.getMunicipalityId())
                .claim("province_id", user.getProvinceId())
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis()
                                + jwtProperties.getAccessTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis()
                                + jwtProperties.getRefreshTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, User user) {

        return extractUsername(token).equals(user.getEmail())
                && !isTokenExpired(token);
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
