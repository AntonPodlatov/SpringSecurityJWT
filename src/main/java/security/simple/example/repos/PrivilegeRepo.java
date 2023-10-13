package security.simple.example.repos;

import security.simple.example.model.Privilege;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepo extends CrudRepository<Privilege, Integer> {
    Optional<Privilege> findByName(String name);
}
