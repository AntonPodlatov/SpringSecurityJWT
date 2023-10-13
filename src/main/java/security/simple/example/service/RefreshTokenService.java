package security.simple.example.service;

import security.simple.example.config.utils.JwtUtils;
import security.simple.example.model.RefreshToken;
import security.simple.example.model.User;
import security.simple.example.repos.RefreshTokenRepo;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserService userService;
    private final JwtUtils jwtUtils;



    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) throws TokenRefreshException {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(refreshToken);

            throw new TokenRefreshException(refreshToken.getToken(),
                    "Refresh token was expired. Please make a new sign in request");
        }

        return refreshToken;

    }

    public RefreshToken updateOrSaveRefreshToken(Long userId, String token) throws Exception {
        User user = userService.findById(userId).stream().findFirst()
                .orElseThrow(() -> new Exception("User not found."));

        Claims claims = jwtUtils.getClaims(token);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(claims.getExpiration().toInstant());
        refreshToken.setToken(token);
        refreshToken.setUser(user);

        return refreshTokenRepo.save(refreshToken);
    }

    public static class TokenRefreshException extends RuntimeException {
        String expiredToken;

        public TokenRefreshException(String refreshToken, String s) {
            super(s);
            this.expiredToken = refreshToken;
        }

    }
}
