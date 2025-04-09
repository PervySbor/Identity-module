package identity.module.repository.entities;

import identity.module.enums.Roles;
import jakarta.persistence.*;

import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="user_id")
    private UUID userId;

    @Column(name="login")
    private String login;

    @Column(name="password_hash")
    private String passwordHash;

    @OneToOne
    //as Role is deprecated
    //@JoinColumn(name="role_id") // ???
    @Enumerated(STRING)
    private Roles role;


    public User() {}


    //assumed usage: creating new user (registration)
    public User(String login, String passwordHash){
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = Roles.NEW_USER;
    }

    public Roles getRole() {
        return role;
    }

    public String getLogin() {
        return login;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
