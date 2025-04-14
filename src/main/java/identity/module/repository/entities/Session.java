package identity.module.repository.entities;

import identity.module.utils.config.ConfigReader;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="sessions")
public class Session {

    @Id
    @Column(name="session_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sessionId;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="refresh_token_hash")
    private String refreshTokenHash; //created new for each new session

    @Column(name="user_ip")
    private String userIp;

    @Column(name="created_at")
    private Timestamp createdAt;

    @Column(name="expires_at")
    private Timestamp expiresAt;

    public Session() {}

    public Session(User user, String userIp, String refreshTokenHash, Timestamp createdAt, Timestamp expiresAt){
        this.userIp = userIp;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.refreshTokenHash = refreshTokenHash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User{");
        if (sessionId != null){
            sb.append("sessionId='").append(sessionId).append("'");
        }
        if (user != null){
            sb.append("user='").append(user).append("'");
        }
        sb.append('\'').append(", refreshTokenHash='").append(refreshTokenHash)
                .append('\'').append(", createdAt=").append(createdAt)
                .append('\'').append(", expiresAt=").append(expiresAt).append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(refreshTokenHash, session.refreshTokenHash) && Objects.equals(userIp, session.userIp) && Objects.equals(createdAt, session.createdAt) && Objects.equals(expiresAt, session.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, refreshTokenHash, userIp, createdAt, expiresAt);
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getRefreshTokenHash() {
        return refreshTokenHash;
    }

    public String getUserIp() {
        return userIp;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public void setRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }
}
