package platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.domain.Message;
import platform.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {

    Optional<Message> findByIdAndDateDeleteIsNull(Integer id);

    List<Message> findAll();

    @Query("SELECT m FROM Message m WHERE m.dateDelete = null" )
    List<Message> findAllAndDateDeleteIsNull();

    Iterable <Message> findAll(Sort sort);

    @Query("SELECT m FROM Message m WHERE m.parentId = null AND m.dateDelete = null" )
    Page<Message> findAll(Pageable pageable);

    @Query(value="SELECT * FROM messages WHERE parent_id IS null AND date_delete IS null AND author_id = ?1 ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Message findRandomMessageByAuthorId(int id);

}
