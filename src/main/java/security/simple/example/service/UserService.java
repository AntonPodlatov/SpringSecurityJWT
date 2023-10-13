package security.simple.example.service;

import security.simple.example.controller.dto.CreatedUserDto;
import security.simple.example.controller.dto.UserRegistrationDto;
import security.simple.example.model.Privilege;
import security.simple.example.model.Role;
import security.simple.example.model.User;
import security.simple.example.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    final PasswordEncoder passwordEncoder;
    final RoleService roleService;
    final UserRepo userRepo;



    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUserName(username).orElseThrow(() ->
                new UsernameNotFoundException("user with name=" + username + "not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(Role::getPrivileges)
                        .flatMap(Collection::stream)
                        .map(Privilege::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }

    public User createNewUser(User user) {
        user.setRoles(Set.of(roleService.findByName("ROLE_USER").get()));
        return userRepo.save(user);
    }

    public Optional<User> findByUserName(String username) {
        return userRepo.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    public CreatedUserDto createNewUser(UserRegistrationDto regDto) {
        User user = new User();
        user.setUsername(regDto.getUsername());
        user.setPassword(passwordEncoder.encode(regDto.getPassword()));
        user.setEmail(regDto.getEmail());
        user.setRoles(Set.of(roleService.findByName("ROLE_USER").get()));
        User newUser = createNewUser(user);

        return new CreatedUserDto(
                newUser.getId(),
                newUser.getEmail(),
                newUser.getUsername(),
                newUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()),
                newUser.getRoles().stream().map(Role::getPrivileges).flatMap(Collection::stream)
                        .map(Privilege::getName).collect(Collectors.toList()));
    }

    public Page<User> getPaged(int pagesCount, int pageSize) {
        return userRepo.findAll(PageRequest.of(pagesCount, pageSize));
    }
}