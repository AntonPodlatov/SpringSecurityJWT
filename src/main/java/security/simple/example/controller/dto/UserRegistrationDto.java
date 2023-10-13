package security.simple.example.controller.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private String passwordConfirmed;
    private String email;
}
