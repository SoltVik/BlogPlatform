package platform.controller;

import liquibase.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.controller.vo.MessageVO;
import platform.domain.Message;
import platform.service.MessageService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/msg")
public class RestMessageController implements MessageRestApi{

    @Autowired
    MessageService messageService;

    @Override
    public ResponseEntity<MessageVO> add(MessageVO body) {
        String text = body.getText();
        String title = body.getTitle();
        Date dateCreate = body.getDateCreate();
        int authorId = body.getAuthorId();
        int parentId = body.getParentId();

        Message newMessage = null;
        if (!StringUtil.isEmpty(text) && !StringUtil.isEmpty(title) && authorId > 0) {
            newMessage = new Message(text, title, dateCreate, authorId, parentId);
            messageService.add(newMessage);
        }
        return ResponseEntity.ok(MessageVO.valueOf(newMessage));
    }

    @Override
    public ResponseEntity<MessageVO> get(Integer id) {
        Message message = messageService.getById(id);
        if (message != null) {
            return ResponseEntity.ok(MessageVO.valueOf(message));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<List<MessageVO>> getAll() {
        List<Message> messages = messageService.getAll();
        List<MessageVO> messageVO = messages.stream()
                .map(e -> MessageVO.valueOf(e))
                .collect(Collectors.toList());

        return ResponseEntity.ok(messageVO);
    }

    @Override
    public ResponseEntity<List<MessageVO>> getByParent(Integer id) {
        List<Message> messages = messageService.getAll();
        List<MessageVO> messageVO = messages.stream()
                .filter(e -> e.getParentId() == id)
                .map(e -> MessageVO.valueOf(e))
                .collect(Collectors.toList());

        return ResponseEntity.ok(messageVO);
    }

    @Override
    public ResponseEntity<MessageVO> update(Integer id, MessageVO body) {
        Message message = messageService.update(id, body);
        return ResponseEntity.ok(MessageVO.valueOf(message));
    }

    @Override
    public ResponseEntity<Void> delete(Integer id) {
        if (messageService.remove(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
