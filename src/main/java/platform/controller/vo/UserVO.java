package platform.controller.vo;

import platform.domain.User;

public class UserVO {

    private int id;
    private String name;
    private String password;
    private String email;
    private boolean isAdmin;

    public UserVO(int id, String name, String password, String email, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public static UserVO valueOf(User user) {
        return new UserVO(user.getId(), user.getName(), user.getPassword(), user.getName(), user.isAdmin());
    }
}
