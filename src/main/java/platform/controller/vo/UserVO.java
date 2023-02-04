package platform.controller.vo;

import platform.domain.Role;
import platform.domain.User;

public class UserVO {

    private int id;
    private String username;
    private String password;
    private String email;
    private Role role;
    private int enabled;

    public UserVO(int id, String username, String password, String email, Role role, int enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public int getEnabled() {
        return enabled;
    }

    public static UserVO valueOf(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getPassword(), user.getUsername(), user.getRole(), user.getEnabled());
    }
}
