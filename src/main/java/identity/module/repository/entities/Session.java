package identity.module.repository.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name="sessions")
public class Session {

    @Id
    @Column(name="session_id")
    private UUID sessionId;

    @Column(name="user_id")
    private UUID userId;

    @Column(name="refresh_token_hash")
    private String refreshTokenHash;

    @Column(name="user_ip")
    private String userIp;

    @Column(name="created_at")
    private Timestamp createdAt;

    @Column(name="expires_at")
    private Timestamp expiresAt;

    public Session() {}

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public UUID getUserId() {
        return userId;
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

    public void setUserId(UUID userId) {
        this.userId = userId;
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
