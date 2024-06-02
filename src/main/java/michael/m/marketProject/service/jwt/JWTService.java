package michael.m.marketProject.service.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;



import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JWTService {
    private final JwtEncoder jwtEncoder;

    public String jwtToken(Authentication authentication) {
        var now = Instant.now();

        var scope = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet
                .builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofDays(1)))
                .subject(authentication.getName())  // Ensure the username/email is set here
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
