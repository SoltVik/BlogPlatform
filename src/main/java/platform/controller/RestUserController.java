package platform.controller;

import liquibase.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.controller.vo.UserVO;
import platform.domain.User;
import platform.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class RestUserController implements UserRestApi{

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<UserVO> add(UserVO body) {
        String name = body.getName();
        String password = body.getPassword();
        String email = body.getEmail();
        boolean isAdmin = body.isAdmin();

        User newUser = null;
        if (!StringUtil.isEmpty(name) && !StringUtil.isEmpty(password) && !StringUtil.isEmpty(email)) {
            newUser = new User(name, password, email, isAdmin);
            userService.add(newUser);
        }
        return ResponseEntity.ok(UserVO.valueOf(newUser));
    }

    @Override
    public ResponseEntity<UserVO> get(Integer id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(UserVO.valueOf(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<List<UserVO>> getAll() {
        List<User> users = userService.findAll();
        List<UserVO> userVO = users.stream()
                .map(e -> UserVO.valueOf(e))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userVO);
    }

    @Override
    public ResponseEntity<UserVO> update(Integer id, UserVO body) {
        User user = userService.update(id, body);
        return ResponseEntity.ok(UserVO.valueOf(user));
    }

    @Override
    public ResponseEntity<Void> delete(Integer id) {
        if (userService.remove(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
