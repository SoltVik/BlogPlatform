package platform.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.domain.Role;

import java.util.List;

@Repository
public interface RoleRepository  extends CrudRepository<Role, Integer> {

    Role findById(int id);

    List<Role> findAll();
}
