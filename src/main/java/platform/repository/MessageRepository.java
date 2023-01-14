package platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.domain.Message;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {

    List<Message> findAll();

    Iterable <Message> findAll(Sort sort);

    @Query("SELECT m FROM Message m WHERE m.parentId = null")
    Page<Message> findAll(Pageable pageable);
}
