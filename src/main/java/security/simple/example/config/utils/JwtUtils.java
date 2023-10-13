package security.simple.example.config.utils;

import security.simple.example.controller.dto.CreatedUserDto;
import security.simple.example.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.lifetime}")
    private Duration AUTH;

    @Value("${jwt.lifetime.refresh}")
    private Duration REFRESH;

    public String generateToken(UserTokenData data, Duration lifetime) {
        ExpirationDate expDate = new ExpirationDate(lifetime);

        return Jwts.builder()
                .setClaims(
                        new HashMap<>() {{
                            put("id", data.getId());
                            put("email", data.getEmail());
                            put("roles", data.getRoles());
                            put("privileges", data.getPrivileges());
                        }}
                ).setSubject(data.getUsername())
                .setIssuedAt(expDate.issued)
                .setExpiration(expDate.expired)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String generateAuthToken(CreatedUserDto dto) {
        return generateToken(new UserTokenData(dto), AUTH);
    }

    public String generateAuthToken(User user) {
        return generateToken(new UserTokenData(user), AUTH);
    }

    public String generateRefreshToken(CreatedUserDto dto) {
        return generateToken(new UserTokenData(dto), REFRESH);
    }

    public String generateRefreshToken(User user) {
        return generateToken(new UserTokenData(user), REFRESH);
    }

    public TokenPair generateTokenPair(User user) {
        return new TokenPair(generateAuthToken(user), generateRefreshToken(user));
    }

    public TokenPair generateTokenPair(CreatedUserDto dto) {
        return new TokenPair(generateAuthToken(dto), generateRefreshToken(dto));
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserName(String token) {
        return getClaims(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return getClaims(token).get("roles", List.class);
    }

    public List<String> getPrivileges(String token) {
        return getClaims(token).get("privileges", List.class);
    }

    private static final class ExpirationDate {
        private final Date issued;
        private final Date expired;

        public ExpirationDate(Duration lifetime) {
            issued = new Date();
            expired = new Date(issued.getTime() + lifetime.toMillis());
        }
    }
}
