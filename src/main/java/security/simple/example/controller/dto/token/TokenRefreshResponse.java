package security.simple.example.controller.dto.token;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TokenRefreshResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType = "Bearer";
}
