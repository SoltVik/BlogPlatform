package platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.controller.vo.UserVO;
import platform.domain.Role;
import platform.domain.User;
import platform.service.RoleService;
import platform.service.UserService;

import java.util.Collection;
import java.util.Date;
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
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (!userService.hasRole("ROLE_ADMIN", authorities)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getJSONMsg(403,"error", "Forbidden", "User do not have permissions"));
        }
        String username = body.getUsername();
        String password = body.getPassword();
        String email = body.getEmail();
        Role role = roleService.findById(body.getRoleId());
        int enabled = body.getEnabled();
        if (userService.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "User already exist"));
        }
        if (password.isEmpty()) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Password is empty"));
        }

        if (!userService.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Email is invalid"));
        }
        if (userService.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Email already exist"));
        }
        if (role == null) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Role can not be found"));
        }
        if (enabled != 0 && enabled != 1) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Use: 0 - disabled, 1 - enabled"));
        }

        if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password) && !StringUtil.isEmpty(email) && (enabled >= 0)) {
            User newUser = new User(username, passwordEncoder.encode(body.getPassword()), email, role, enabled);
            userService.add(newUser);
            return ResponseEntity.ok(getJSONMsg(200,"success", "OK", "User '" + username + "' successfully created"));
        }
        return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Check all fields and try again"));
    }

    @Override
    @Operation(summary = "Get user by id")
    public ResponseEntity<UserVO> get(Integer id) {
        User user = userService.findEnabledById(id);
        if (user != null) {
            return ResponseEntity.ok(UserVO.valueOf(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserVO>> getAll() {
        List<User> users = userService.findAll();
        List<UserVO> userVO = users.stream()
                .map(u -> UserVO.valueOf(u))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userVO);
    }

    @Override
    @Operation(summary = "Get all enabled users")
    public ResponseEntity<List<UserVO>> getAllEnabled() {
        List<User> users = userService.findAllEnabled();
        List<UserVO> userVO = users.stream()
                .map(u -> UserVO.valueOf(u))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userVO);
    }

    @Override
    @Operation(summary = "Get user by username")
    public ResponseEntity<UserVO> getByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(UserVO.valueOf(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @Operation(summary = "Get users with the most posts")
    public ResponseEntity<List<UserVO>> getTopAuthors(Integer amount) {
        List<User> users = userService.getTopAuthors(amount);
        List<UserVO> userVO = users.stream()
                .map(u -> UserVO.valueOf(u))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userVO);
    }

    @Override
    @Operation(summary = "Restore user by id")
    public ResponseEntity<String> restore(Integer id) {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (!userService.hasRole("ROLE_ADMIN", authorities)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getJSONMsg(403,"error", "Forbidden", "User do not have permissions"));
        }
        User user = userService.findById(id);
        if (user != null) {
            int enabled = user.getEnabled();
            if (enabled != 0) {
               return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "User with id: '" + id + "' not disabled"));
            }
            user.setEnabled(1);
            userService.save(user);
            return ResponseEntity.ok(getJSONMsg(200,"success", "OK", "User with id '" + id + "' successfully restored"));
        }
        return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "User with id: '" + id + "' not exist"));
    }

    @Override
    @Operation(summary = "Update user by id")
    public ResponseEntity<String> update(Integer id, UserVO body) {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (!userService.hasRole("ROLE_ADMIN", authorities)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getJSONMsg(403,"error", "Forbidden", "User do not have permissions"));
        }
        String username = body.getUsername();
        String password = body.getPassword();
        String email = body.getEmail();
        boolean changePassword = true;
        Role role = roleService.findById(body.getRoleId());
        int enabled = body.getEnabled();

        User user = userService.findByUsername(username);
        if (user != null) {
            if (user.getId() != id) {
                return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Username: '" + username + "' already exist"));
            }
        }
        if (password.isEmpty()) {
            changePassword = false;
        }
        if (!userService.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Email is invalid"));
        }
        user = userService.findByEmail(email);
        if (user != null) {
            if (user.getId() != id) {
                return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Email: '" + email + "' already exist"));
            }
        }
        if (role == null) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Role can not be found"));
        }
        if (enabled != 0 && enabled != 1) {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Use: 0 - disabled, 1 - enabled"));
        }
        if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(email)) {
            User oldUser = userService.findEnabledById(id);
            oldUser.setUsername(username);
            oldUser.setEmail(email);
            if (changePassword) {
                oldUser.setPassword(passwordEncoder.encode(password));
            }
            oldUser.setRole(role);
            oldUser.setEnabled(enabled);
            userService.save(oldUser);
            return ResponseEntity.ok(getJSONMsg(200,"success", "OK", "User with id '" + id + "' successfully updated"));
        }
        return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request", "Check all fields and try again"));
    }

    @Override
    @Operation(summary = "Delete user by id")
    public ResponseEntity<String> delete(Integer id) {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (!userService.hasRole("ROLE_ADMIN", authorities)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getJSONMsg(403,"error", "Forbidden", "User do not have permissions"));
        }
        if (userService.remove(id)) {
            return ResponseEntity.ok(getJSONMsg(200,"success", "OK","User with id '" + id + "' successfully deleted"));
        } else {
            return ResponseEntity.badRequest().body(getJSONMsg(400,"error", "Bad Request","User with id '" + id + "' not found"));
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