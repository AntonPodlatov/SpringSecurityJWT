package security.simple.example.repos;

import security.simple.example.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
