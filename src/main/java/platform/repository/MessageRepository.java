package platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.domain.Message;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {

    Optional<Message> findByIdAndDateDeleteIsNull(Integer id);
    List<Message> findAll();

    Iterable <Message> findAll(Sort sort);

    @Query("SELECT m FROM Message m WHERE m.parentId = null AND m.dateDelete = null" )
    Page<Message> findAll(Pageable pageable);
}
