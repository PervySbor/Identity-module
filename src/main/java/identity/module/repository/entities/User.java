package identity.module.repository.entities;

import identity.module.enums.Roles;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name="users")
//@Access(AccessType.PROPERTY)
public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name="user_id")
    private UUID userId;

//    @Column(name="login")
    private String login;

//    @Column(name="password_hash")
    private String passwordHash;

//    @Enumerated(STRING)
//    @Column(name="role")
    private Roles role;


    public User() {}

    //assumed usage: creating new user (registration)
    public User(String login, String passwordHash, Roles role){
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    //WARNING!!! For test assertions only
    @Override
    public boolean equals(Object o) {
        System.out.println("passed " + o);
        System.out.println(this);
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login) && Objects.equals(passwordHash, user.passwordHash) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, passwordHash, role);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User{");
        if (userId != null){
            sb.append("userId='").append(userId).append("'");
        }
        sb.append("login='").append(login).append('\'').append(", passwordHash='").append(passwordHash).append('\'').append(", role=").append(role).append('}');
        return sb.toString();
    }

    @Enumerated(STRING)
    @Column(name="role")
    public Roles getRole() {
        return role;
    }

    @Column(name="login")
    public String getLogin() {
        return login;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="user_id")
    public UUID getUserId() {
        return userId;
    }

    @Column(name="password_hash")
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
