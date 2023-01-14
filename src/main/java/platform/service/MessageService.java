package platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import platform.controller.vo.MessageVO;
import platform.domain.Message;
import platform.repository.MessageRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getAll() {
        return messageRepository.findAll();
    }
    public Page<Message> findAll(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }

    public void add(Message message) {
        messageRepository.save(message);
    }

    public Message getById(Integer idx) {
        Optional<Message> result = messageRepository.findById(idx);
        return result.orElse(null);
    }

    public List<Message> getAllByParent(Integer id) {
        List<Message> messages = messageRepository.findAll();

        messages = messages.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.toList());

        return messages.stream()
                .filter(m -> m.getParentId() == (int)id)
                .sorted((m1, m2) -> m2.compareTo(m1))
                .collect(Collectors.toList());
    }

    public long countChildByParent(Integer id) {
       List<Message> messages = messageRepository.findAll();
       messages = messages.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.toList());

        return messages.stream()
                .filter(m -> m.getParentId() == (int)id)
                .count();
    }


    public Message update(int idx, MessageVO messageVO) {
        Message old = messageRepository.findById(idx).get();
        old.setText(messageVO.getText());
        old.setTitle(messageVO.getTitle());
        old.setDateEdit(new Timestamp(System.currentTimeMillis()));
        old.setEditorId(messageVO.getEditorId());
        old.setParent(messageVO.getParent());

        return messageRepository.save(old);
    }

    public boolean remove(Integer idx) {
        Message message = getById(idx);
        if (message != null) {
            messageRepository.delete(message);
            return true;
        } else {
            return false;
        }
    }
}
