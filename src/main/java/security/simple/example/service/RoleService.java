package security.simple.example.service;

import security.simple.example.model.Role;
import security.simple.example.repos.RoleRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepo roleRepo;

    public RoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public Optional<Role> findByName(String roleName) {
        return roleRepo.findByName(roleName);
    }

    public Optional<Role> findByName(Integer id) {
        return roleRepo.findById(id);
    }

    public Iterable<Role> findByIds(Iterable<Integer> ids) {
        return roleRepo.findAllById(ids);
    }

}