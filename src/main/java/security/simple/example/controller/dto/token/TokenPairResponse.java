package security.simple.example.controller.dto.token;

import security.simple.example.config.utils.TokenPair;
import lombok.Data;

@Data
public class TokenPairResponse {
    private final TokenPair tokenPair;
}
