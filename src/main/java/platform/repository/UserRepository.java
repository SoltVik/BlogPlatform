package platform.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.id = ?1 AND u.enabled = 1")
    User findByIdAndIsEnabled(int id);

    User findByUsername(String username);

    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.enabled = 1 ORDER BY u.username ASC")
    List<User> findAllEnabled();

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE ?1% ORDER BY u.username ASC")
    List<User> findAllByLetter(String letter);

    @Query("SELECT u FROM User u WHERE u.enabled = 1 AND LOWER(u.username) LIKE ?1% ORDER BY u.username ASC")
    List<User> findAllEnabledByLetter(String letter);

    @Query(value="SELECT * FROM users WHERE LOWER(username) ~ '^[0-9]' ORDER BY username ASC", nativeQuery = true)
    List<User> findAllBySpecSymbol();

    @Query(value="SELECT * FROM users WHERE enabled = 1 AND LOWER(username) ~ '^[0-9]' ORDER BY username ASC", nativeQuery = true)
    List<User> findAllEnabledBySpecSymbol();

    @Query(value="SELECT COUNT(m.*), u.* FROM messages m JOIN users u ON m.author_id = u.user_id WHERE date_delete is null AND parent_id is null GROUP BY u.user_id ORDER BY count DESC LIMIT ?1", nativeQuery = true)
    List<User> getTopAuthors(int limit);
}
