package platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    static final Logger logger = LoggerFactory.getLogger(MessageService.class);

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

    public List<Message> getAllByParent(int id) {
        List<Message> messages = messageRepository.findAll();

        messages = messages.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.toList());

        return messages.stream()
                .filter(m -> m.getParentId() == id)
                .sorted((m1, m2) -> m2.compareTo(m1))
                .collect(Collectors.toList());
    }

    public long countChildByParent(int id) {
       List<Message> messages = messageRepository.findAll();
       messages = messages.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.toList());

        return messages.stream()
                .filter(m -> m.getParentId() == id)
                .count();
    }

    public void deleteAllByParent(int id) {
        List<Message> messages = getAllByParent(id);

        for ( Message message : messages) {
            if (getAllByParent(message.getId()).isEmpty()) {
                remove(message.getId());
            } else if (!getAllByParent(message.getId()).isEmpty()) {
                deleteAllByParent(message.getId());
                remove(message.getId());
            }
        }
    }


    public Message update(int idx, MessageVO messageVO) {
        Message old = messageRepository.findById(idx).get();
        old.setText(messageVO.getText());
        old.setTitle(messageVO.getTitle());
        old.setDateEdit(new Timestamp(System.currentTimeMillis()));
        old.setEditor(messageVO.getEditorId());
        old.setParent(messageVO.getParent());

        return messageRepository.save(old);
    }

    public boolean remove(Integer idx) {
        Message message = getById(idx);
        if (message != null) {
            logger.info("Removed message {}", message);
            messageRepository.delete(message);
            return true;
        } else {
            return false;
        }
    }
}
