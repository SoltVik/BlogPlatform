package platform.controller.vo;

import platform.domain.Role;

public class RoleVO {
    private int id;

    private String roleName;

    public RoleVO(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public static RoleVO valueOf(Role role) {
        return new RoleVO(role.getId(), role.getRoleName());
    }
}
