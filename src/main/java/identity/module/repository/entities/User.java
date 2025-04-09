package identity.module.repository.entities;

import jakarta.persistence.*;

import java.util.UUID;

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
    @JoinColumn(name="role_id") // ???
    private Role role;

    public User() {}

    public User(String login, String passwordHash, Role role){
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Role getRole() {
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

    public void setRole(Role role) {
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
