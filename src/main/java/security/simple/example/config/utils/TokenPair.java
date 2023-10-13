package security.simple.example.config.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenPair {
    private final String authToken;
    private final String refreshToken;
}
