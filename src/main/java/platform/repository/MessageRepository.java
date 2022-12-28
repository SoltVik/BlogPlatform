package platform.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.domain.Message;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {

    List<Message> findAll();
}
