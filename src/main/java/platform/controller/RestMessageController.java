package platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.controller.vo.MessageVO;
import platform.domain.Message;
import platform.domain.User;
import platform.service.MessageService;
import platform.service.UserService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Messages")
@RequestMapping("/api/msg")
public class RestMessageController implements MessageRestApi{

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    @Operation(summary = "Add new message")
    public ResponseEntity<String> add(MessageVO body) {
        String text = body.getText();
        String title = body.getTitle();
        Date dateCreate = body.getDateCreate();
        if (dateCreate == null) {
            dateCreate = new Date();
        }

        Integer authorId = body.getAuthorId();
        Integer parentId = body.getParentId();

        if (authorId == null) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Author ID is null"));
        }

        User author = userService.findEnabledById(authorId);
        if (author == null) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Author with id: '" + authorId + "' not exist"));
        }

        if (!StringUtil.isEmpty(text) && !StringUtil.isEmpty(title)) {
            Message newMessage = new Message(text, title, dateCreate, author, (parentId != null) ? messageService.getById(parentId) : null);
            messageService.add(newMessage);
            return ResponseEntity.ok(getJSONMsg(200,"success", "OK", "Message successfully created"));
        }
        return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Check all fields and try again"));
    }

    @Override
    @Operation(summary = "Get message by id")
    public ResponseEntity<MessageVO> get(Integer id) {
        Message message = messageService.getById(id);
        if (message != null) {
            return ResponseEntity.ok(MessageVO.valueOf(message));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @Operation(summary = "Get all messages")
    public ResponseEntity<List<MessageVO>> getAll() {
        List<Message> messages = messageService.getAll();
        List<MessageVO> messagesVO = messages.stream()
                .map(m -> MessageVO.valueOf(m))
                .collect(Collectors.toList());
        return ResponseEntity.ok(messagesVO);
    }

    @Override
    @Operation(summary = "Get messages (comments) by parent id")
    public ResponseEntity<List<MessageVO>> getByParent(Integer id) {
        List<Message> messages = messageService.getAll();
        List<MessageVO> messagesVO = messages.stream()
                .filter(m -> m.getParentId() == id)
                .map(m -> MessageVO.valueOf(m))
                .collect(Collectors.toList());
        return ResponseEntity.ok(messagesVO);
    }

    @Override
    @Operation(summary = "Get last comments")
    public ResponseEntity<List<MessageVO>> getLastComments(Integer amount) {
        List<Message> messages = messageService.getLastComments(amount);
        List<MessageVO> messagesVO = messages.stream()
                .map(m -> MessageVO.valueOf(m))
                .collect(Collectors.toList());
        return ResponseEntity.ok(messagesVO);
    }

    @Override
    @Operation(summary = "Get last posts")
    public ResponseEntity<List<MessageVO>> getLastPosts(Integer amount) {
        List<Message> messages = messageService.getLastPosts(amount);
        List<MessageVO> messagesVO = messages.stream()
                .map(m -> MessageVO.valueOf(m))
                .collect(Collectors.toList());
        return ResponseEntity.ok(messagesVO);
    }

    @Override
    @Operation(summary = "Restore messages by id")
    public ResponseEntity<String> restore(Integer id) {
        Message message = messageService.getByIdIncludingDeleted(id);
        if (message != null) {
            Date dateDelete = message.getDateDelete();
            if (dateDelete == null) {
                return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Message with id: '" + id + "' not deleted"));
            }
            message.setDateDelete(null);
            messageService.save(message);
            return ResponseEntity.ok(getJSONMsg(200,"success", "OK", "Message with id '" + id + "' successfully restored"));
        }
        return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Message with id: '" + id + "' not exist"));
    }

    @Override
    @Operation(summary = "Update messages by id")
    public ResponseEntity<String> update(Integer id, MessageVO body) {
        Message old = messageService.getById(id);
        if (old != null) {
            String text = body.getText();
            String title = body.getTitle();

            Integer authorId = body.getAuthorId();
            Integer editorId = body.getEditorId();

            if (authorId == null) {
                return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Author ID is null"));
            }
            if (editorId == null) {
                return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Editor ID is null"));
            }

            User author = userService.findEnabledById(authorId);
            if (author == null) {
                return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Author with id: '" + authorId + "' not exist"));
            }
            User editor = userService.findEnabledById(editorId);
            if (editor == null) {
                return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Editor with id: '" + editorId + "' not exist"));
            }

            if (!StringUtil.isEmpty(text) && !StringUtil.isEmpty(title)) {
                messageService.update(id, body);
                return ResponseEntity.ok(getJSONMsg(200, "success", "OK", "Message with id '" + id + "' successfully updated"));
            }
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Check all fields and try again"));
        }
        return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Message with id '" + id + "' not found"));
    }

    @Override
    @Operation(summary = "Delete messages by id ")
    public ResponseEntity<String> delete(Integer id) {
        if (messageService.remove(id)) {
            return ResponseEntity.ok(getJSONMsg(200,"success", "OK", "Message with id '" + id + "' successfully deleted"));
        } else {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Message with id '" + id + "' not found"));
        }
    }

    private String getJSONMsg(int status, String state, String stateMsg, String msg) {
        return "{\n" +
                "\"timestamp\": \"" + new Date() + "\",\n" +
                "\"status\": " + status + ",\n" +
                "\"" + state + "\": \"" + stateMsg + "\",\n" +
                "\"message\": \"" + msg + "\"" +
                "\n}";
    }
}
