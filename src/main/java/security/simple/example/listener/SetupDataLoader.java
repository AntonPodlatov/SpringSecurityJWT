package security.simple.example.listener;

import security.simple.example.model.Privilege;
import security.simple.example.model.Role;
import security.simple.example.repos.PrivilegeRepo;
import security.simple.example.repos.RoleRepo;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadySetup = false;

    private final PrivilegeRepo privilegeRepo;
    private final RoleRepo roleRepo;

    public SetupDataLoader(PrivilegeRepo privilegeRepo, RoleRepo roleRepo) {
        this.privilegeRepo = privilegeRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        List<Privilege> privileges = new ArrayList<>();
        privileges.add(createPrivilegeIfNotFound("READ_PRIVILEGE"));
        privileges.add(createPrivilegeIfNotFound("WRITE_PRIVILEGE"));

        addPrivilegeIfNotFound(privileges, "ROLE_ADMIN");
        addPrivilegeIfNotFound(privileges, "ROLE_STAFF");
        addPrivilegeIfNotFound(privileges, "ROLE_USER");

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
        Optional<Privilege> privilegeOpt = privilegeRepo.findByName(name);

        if (privilegeOpt.isPresent()) {
            return privilegeOpt.get();
        }

        Privilege privilege = new Privilege(name);
        return privilegeRepo.save(privilege);
    }

    @Transactional
    void addPrivilegeIfNotFound(Collection<Privilege> privileges, String roleName) {
        Optional<Role> roleOpt = roleRepo.findByName(roleName);

        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();

            if (role.getPrivileges().size() > 0) {
                return;
            }

            role.setPrivileges(privileges);
            roleRepo.save(role);
        }
    }
}

