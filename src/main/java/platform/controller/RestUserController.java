package platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.controller.vo.UserVO;
import platform.domain.Role;
import platform.domain.User;
import platform.service.RoleService;
import platform.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Users")
@RequestMapping("/api/user")
public class RestUserController implements UserRestApi {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Operation(summary = "Add new user")
    public ResponseEntity<String> add(UserVO body) {
        String username = body.getUsername();
        String password = body.getPassword();
        String email = body.getEmail();
        Role role = roleService.findById(body.getRoleId());
        int enabled = body.getEnabled();
        if (userService.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "User already exist"));
        }
        if (password.isEmpty()) {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "Password is empty"));
        }

        if (!userService.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "Email is invalid"));
        }
        if (userService.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "Email already exist"));
        }
        if (role == null) {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "Role can not be found"));
        }
        if (enabled != 0 && enabled != 1) {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "Use: 0 - disabled, 1 - enabled"));
        }

        if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password) && !StringUtil.isEmpty(email) && (enabled >= 0)) {
            User newUser = new User(username, passwordEncoder.encode(body.getPassword()), email, role, enabled);
            userService.add(newUser);
            return ResponseEntity.ok(getJSONMsg("success", "User '" + username + "' successfully created"));
        }
        return ResponseEntity.badRequest().body(getJSONMsg("error", "Check all fields and try again"));
    }

    @Override
    @Operation(summary = "Find user by id")
    public ResponseEntity<UserVO> get(Integer id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(UserVO.valueOf(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @Operation(summary = "Find all users")
    public ResponseEntity<List<UserVO>> getAll() {
        List<User> users = userService.findAll();
        List<UserVO> userVO = users.stream()
                .map(e -> UserVO.valueOf(e))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userVO);
    }

    @Override
    @Operation(summary = "Find all enabled users")
    public ResponseEntity<List<UserVO>> getAllEnabled() {
        List<User> users = userService.findAllEnabled();
        List<UserVO> userVO = users.stream()
                .map(e -> UserVO.valueOf(e))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userVO);
    }

    @Override
    @Operation(summary = "Update user by id")
    public ResponseEntity<String> update(Integer id, UserVO body) {
        String username = body.getUsername();
        String password = body.getPassword();
        String email = body.getEmail();
        boolean changePassword = true;
        Role role = roleService.findById(body.getRoleId());
        int enabled = body.getEnabled();

        User user = userService.findByUsername(username);
        if (user != null) {
            if (user.getId() != id) {
                return ResponseEntity.badRequest().body(getJSONMsg("error", "Username: '" + username + "' already exist"));
            }
        }
        if (password.isEmpty()) {
            changePassword = false;
        }
        if (!userService.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "Email is invalid"));
        }
        user = userService.findByEmail(email);
        if (user != null) {
            if (user.getId() != id) {
                return ResponseEntity.badRequest().body(getJSONMsg("error", "Email: '" + email + "' already exist"));
            }
        }
        if (role == null) {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "Role can not be found"));
        }
        if (enabled != 0 && enabled != 1) {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "Use: 0 - disabled, 1 - enabled"));
        }
        if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(email)) {
            User oldUser = userService.findById(id);
            oldUser.setUsername(username);
            oldUser.setEmail(email);
            if (changePassword) {
                oldUser.setPassword(passwordEncoder.encode(password));
            }
            oldUser.setRole(role);
            oldUser.setEnabled(enabled);
            userService.save(oldUser);
            return ResponseEntity.ok(getJSONMsg("success", "User with id '" + id + "' successfully updated"));
        }
        return ResponseEntity.badRequest().body(getJSONMsg("error", "Check all fields and try again"));
    }

    @Override
    @Operation(summary = "Delete user")
    public ResponseEntity<String> delete(Integer id) {
        if (userService.remove(id)) {
            return ResponseEntity.ok(getJSONMsg("success", "User with id '" + id + "' successfully deleted"));
        } else {
            return ResponseEntity.badRequest().body(getJSONMsg("error", "User with id '" + id + "' not found"));
        }
    }

    private String getJSONMsg(String status, String msg) {
        return "{\n" +
                "\" " + status + " \" : " +
                "\" " + msg + " \"" +
                "\n}";
    }
}
