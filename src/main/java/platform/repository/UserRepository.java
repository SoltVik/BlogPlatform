package platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findAll();

    User findByUsername(String username);

    User findByEmail(String email);

    @Query(value="SELECT COUNT(m.*), u.* FROM messages m JOIN users u ON m.author_id = u.user_id WHERE date_delete is null AND parent_id is null GROUP BY u.user_id ORDER BY count DESC LIMIT ?1", nativeQuery = true)
    List<User> getTopAuthors(int limit);
}
