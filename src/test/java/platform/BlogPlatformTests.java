package platform;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import platform.controller.vo.MessageVO;
import platform.domain.Message;
import platform.domain.Role;
import platform.domain.User;
import platform.service.MessageService;
import platform.service.RoleService;
import platform.service.UserService;

import java.util.Date;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class BlogPlatformTests {

    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Autowired
    RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    public void init() {
        Role role = new Role("ROLE_ADMIN");
        roleService.add(role);
        role = new Role("ROLE_STAFF");
        roleService.add(role);
        role = new Role("ROLE_USER");
        roleService.add(role);
        User user = new User("Admin", "admin", "admin@blogplatform.com", roleService.findById(Role.ROLE_ADMIN), 1);
        userService.add(user);
        user = new User("User", "user", "user@blogplatform.com", roleService.findById(Role.ROLE_USER), 1);
        userService.add(user);
        user = new User("Test", "test", "test@blogplatform.com", roleService.findById(Role.ROLE_USER), 1);
        userService.add(user);
        Message message = new Message("Some text for testing 1", "Testing title 1", new Date(), user, null);
        messageService.add(message);
        message = new Message("Some text for testing 2", "Testing title 2", new Date(), user, null);
        messageService.add(message);
        message = new Message("Some text for testing 3", "Testing title 3", new Date(), user, null);
        messageService.add(message);
    }

    @Test
    public void testAddUser() {
        List<User> users = userService.findAll();
        int count = users.size();
        User user = new User("Ivan", "password", "ivan@blogplatform.com", roleService.findById(Role.ROLE_USER), 1);
        userService.add(user);
        users = userService.findAll();
        Assertions.assertEquals(count + 1, users.size());
        Assertions.assertEquals("Ivan", users.get(count).getUsername());
    }

    @Test
    public void testUpdateUser() {
        User user = userService.findByUsername("Test");
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        userService.save(user);
        Assertions.assertNotEquals(password, user.getPassword());
    }

    @Test
    public void testDeleteUser() {
        List<User> users = userService.findAll();
        int count = users.size();
        User user = userService.findByUsername("User");
        int userId = user.getId();
        userService.remove(userId);
        users = userService.findAllEnabled();
        Assertions.assertEquals(count - 1, users.size());
        Assertions.assertNull(userService.findEnabledById(userId));
    }

    @Test
    public void testAdminRole() {
        User user = userService.findByUsername("Admin");
        Assertions.assertEquals(user.getRole().getId(), Role.ROLE_ADMIN);
    }

    @Test
    public void testAddMsg() {
        List<Message> messages = messageService.getAll();
        int count = messages.size();
        User user = userService.findById(1);
        Message message = new Message("Text for new message", "New testing post", new Date(), user, null);
        messageService.add(message);
        messages = messageService.getAll();
        Assertions.assertEquals(count + 1, messages.size());
        Assertions.assertEquals("New testing post", messages.get(count).getTitle());
    }

    @Test
    public void testUpdateMsg() {
        Message message = messageService.getById(1);
        User user = userService.findById(1);
        String oldTitle = message.getTitle();
        Date dateEdit = message.getDateEdit();
        User editor = message.getEditor();
        String newTitle = "New title for post 1";
        message.setTitle(newTitle);
        message.setEditor(user);
        MessageVO newMsg = MessageVO.valueOf(message);
        messageService.update(1, newMsg);
        message = messageService.getById(1);
        Assertions.assertNotEquals(oldTitle, message.getTitle());
        Assertions.assertNotEquals(dateEdit, message.getDateEdit());
        Assertions.assertNotEquals(editor, message.getEditor());
        Assertions.assertEquals(user, message.getEditor());
    }

    @Test
    public void testDeleteMsg() {
        List<Message> messages = messageService.getAllEnabled();
        int count = messages.size();
        messageService.remove(1);
        messages = messageService.getAllEnabled();
        Assertions.assertEquals(count - 1, messages.size());
        Assertions.assertNull(messageService.getById(1));
    }

    @Test
    public void testApiRespond() {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic VXNlcjp1c2Vy");
        String body = "";
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("http://localhost:8090/api/user/getAll", HttpMethod.GET, requestEntity, String.class);
        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        Assertions.assertEquals(200, status);
    }
}
