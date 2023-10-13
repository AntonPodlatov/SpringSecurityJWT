package security.simple.example.service;

import security.simple.example.model.Privilege;
import security.simple.example.model.Role;
import security.simple.example.repos.RoleRepo;
import security.simple.example.repos.UserRepo;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service("userDetailsService")
public class InstUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final MessageSource messages;
    private final UserService userService;

    public InstUserDetailsService(
            UserRepo userRepo,
            RoleRepo roleRepo,
            MessageSource messages,
            UserService userService) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.messages = messages;
        this.roleRepo = roleRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<security.simple.example.model.User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            return new User(
                    " ", " ", true, true, true, true,
                    getAuthorities(List.of(roleRepo.findByName("ROLE_USER").get())));
        }

        security.simple.example.model.User user = userOpt.get();

        return new User(
                user.getEmail(), user.getPassword(), user.isRemoved(), true, true,
                true, getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles) {
        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();

        for (Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }

        for (Privilege item : collection) {
            privileges.add(item.getName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        return privileges
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}