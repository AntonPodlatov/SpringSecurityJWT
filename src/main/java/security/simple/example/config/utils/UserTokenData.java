package security.simple.example.config.utils;

import security.simple.example.controller.dto.CreatedUserDto;
import security.simple.example.model.Privilege;
import security.simple.example.model.Role;
import security.simple.example.model.User;
import lombok.Data;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
public class UserTokenData {
    private Collection<String> privileges;
    private Collection<String> roles;
    private String username;
    private String email;
    private Long id;

    public UserTokenData(CreatedUserDto dto) {
        privileges = dto.getPrivileges();
        username = dto.getUsername();
        email = dto.getEmail();
        roles = dto.getRoles();
        id = dto.getId();
    }

    public UserTokenData(User user) {
        username = user.getUsername();
        email = user.getEmail();

        roles = user
                .getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        privileges= user
                .getRoles()
                .stream()
                .map(Role::getPrivileges)
                .flatMap(Collection::stream)
                .map(Privilege::getName)
                .collect(Collectors.toList());

        id = user.getId();
    }
}
