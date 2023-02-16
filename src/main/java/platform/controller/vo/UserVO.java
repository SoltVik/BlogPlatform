package platform.controller.vo;

import platform.domain.User;

import javax.validation.constraints.*;

public class UserVO {

    private int id;

    @NotEmpty
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotNull(message = "Password cannot be null")
    private String password;

    @NotEmpty
    @Email(message = "Email should be valid")
    private String email;

    @NotNull
    private int roleId;

    @NotNull
    @Min(value = 0, message = "0 - disabled, 1 - enabled")
    @Max(value = 1, message = "0 - disabled, 1 - enabled")
    private int enabled;

    public UserVO(int id, String username, String password, String email, int roleId, int enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roleId = roleId;
        this.enabled = enabled;
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

    public int getRoleId() {
        return roleId;
    }

    public int getEnabled() {
        return enabled;
    }

    public static UserVO valueOf(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(), user.getRole().getId(), user.getEnabled());
    }
}
