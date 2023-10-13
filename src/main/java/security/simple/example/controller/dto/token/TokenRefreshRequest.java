package security.simple.example.controller.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class TokenRefreshRequest {
    @NotBlank
    private String refreshToken;
}
