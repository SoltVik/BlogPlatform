package platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import platform.controller.vo.MessageVO;
import platform.domain.Message;
import platform.domain.User;
import platform.repository.MessageRepository;
import platform.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Message> getAll() {
        return messageRepository.findAll();
    }
    public Page<Message> findAll(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }

    public void add(Message message) {
        logger.info("Added message {}", message);
        messageRepository.save(message);
    }

    public void save(Message message) {
        logger.info("Updated message {}", message);
        messageRepository.save(message);
    }

    public Message getById(Integer idx) {
        Optional<Message> result = messageRepository.findByIdAndDateDeleteIsNull(idx);
        return result.orElse(null);
    }

    public Message getByIdIncludingDeleted(Integer id) {
        Optional<Message> result = messageRepository.findById(id);
        return result.orElse(null);
    }

    public List<Message> getAllByParent(int id) {
        List<Message> messages = messageRepository.findAll();

        messages = messages.stream()
                .filter(m -> m.getParentId() != null)
                .filter(m -> m.getDateDelete() == null)
                .collect(Collectors.toList());

        return messages.stream()
                .filter(m -> m.getParentId() == id)
                .sorted((m1, m2) -> m2.compareTo(m1))
                .collect(Collectors.toList());
    }

    public List<Message> getLastPosts(int num) {
        List<Message> messages = messageRepository.findAllAndDateDeleteIsNull();
        return messages.stream()
                .filter(m -> m.getParentId() == null)
                .sorted((m1, m2) -> m2.compareTo(m1))
                .limit(num)
                .collect(Collectors.toList());
    }

    public List<Message> getLastComments(int num) {
        List<Message> messages = messageRepository.findAllAndDateDeleteIsNull();
        return messages.stream()
                .filter(m -> m.getParentId() != null)
                .sorted((m1, m2) -> m2.compareTo(m1))
                .limit(num)
                .collect(Collectors.toList());
    }

    public List<Message> getRandomPostToUsers(List<User> users){
        List<Message> messages = new ArrayList<>();
            for (User user : users) {
                messages.add(messageRepository.findRandomMessageByAuthorId(user.getId()));
            }
        return messages;
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

    public int countAllChildByParent(int id) {
        List<Message> messages = getAllByParent(id);

        int count = 0;
        for (Message message : messages) {
            count ++;
            if (!getAllByParent(message.getId()).isEmpty()) {
                count = count + countAllChildByParent(message.getId());
            }
        }
        return count;
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

    public Integer getPostByComment(int id) {
        Message message = getById(id);

        if (message.getParentId() == null) {
            return (message.getId());
        }

        return getPostByComment(message.getParentId());
    }

    public Message update(int idx, MessageVO messageVO) {
        Message old = messageRepository.findById(idx).orElse(null);
        if (old != null) {
            old.setText(messageVO.getText());
            old.setTitle(messageVO.getTitle());
            old.setDateEdit(new Date());
            old.setEditor(userRepository.findByIdAndIsEnabled(messageVO.getEditorId()));
            old.setParent(getById(messageVO.getParentId()));

            return messageRepository.save(old);
        }
        return null;
    }

    public boolean remove(Integer idx) {
        Message message = getById(idx);
        if (message != null) {
            logger.info("Removed message {}", message);
            message.setDateDelete(new Date());
            messageRepository.save(message);
            return true;
        } else {
            return false;
        }
    }
}
