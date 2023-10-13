package security.simple.example.controller.dto.token;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
