package security.simple.example.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class CreatedUserDto {
    private Long id;
    private String email;
    private String username;
    private Collection<String> roles;
    private Collection<String> privileges;
}
