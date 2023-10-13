package security.simple.example.repos;

import security.simple.example.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends PagingAndSortingRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("select user from User user where user.id in :ids")
    List<User> findByUserIds(@Param("ids") Iterable<Long> ids);
}
