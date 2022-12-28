package platform.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findAll();

}
