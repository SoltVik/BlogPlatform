package platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="roles")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class Role {

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_STAFF = 2;
    public static final int ROLE_USER = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int id;

    @NotNull(message = "Name cannot be null")
    @Column(name = "role_name")
    private String roleName;

    public Role() {

    }

    public Role(String roleName) {
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
}
