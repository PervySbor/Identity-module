package identity.module.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import identity.module.enums.Roles;

import java.sql.Timestamp;
import java.util.UUID;

@JsonPropertyOrder(value={"sid","iat","eat","rol"})
public class JwtPayload {

    private UUID sessionId;

    private Timestamp createdAt;

    private Timestamp expireAt;

    private String role;

    public JwtPayload(Roles role, UUID session_id){
        this.role = role.name();
        this.sessionId = session_id;
    }

    @JsonGetter("sid")
    public UUID getSessionId() {
        return sessionId;
    }

    @JsonGetter("iat")
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    @JsonGetter("eat")
    public Timestamp getExpireAt() {
        return expireAt;
    }

    @JsonGetter("rol")
    public String getRole() {
        return role;
    }
}
