package identity.module.repository.entities;

import identity.module.enums.Roles;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;

import static jakarta.persistence.EnumType.STRING;

@Deprecated
//@Entity
//@Table(name="roles")
public class Role {

    @Column(name="role_name")
    @Enumerated(STRING)
    private Roles role;

    @Id
    @Column(name="role_id")
    private Integer roleId;

    public Role() {}

    //no user constructor, as roles won't be changed

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

}
