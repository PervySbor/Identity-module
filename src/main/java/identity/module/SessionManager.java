package identity.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import identity.module.enums.Roles;
import identity.module.exceptions.NonUniqueSubscriptionException;
import identity.module.exceptions.NonUniqueUserException;
import identity.module.exceptions.UserNotFoundException;
import identity.module.repository.Repository;
import identity.module.repository.entities.Session;
import identity.module.repository.entities.Subscription;
import identity.module.repository.entities.User;
import identity.module.utils.JsonManager;
import identity.module.utils.SecurityManager;
import identity.module.utils.config.ConfigReader;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

class SessionManager {

    private final Repository repository;

    SessionManager(Repository repo){
        this.repository = repo;
    }

    public String generateNewRefreshToken(){
        return UUID.randomUUID().toString();
    }

    String createJWT(Roles role, UUID session_id)
            throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Timestamp sessionExpireAt = this.repository.findSession(session_id).getExpiresAt();
        int jwtLifespan = Integer.parseInt(ConfigReader.getStringValue("JWT_LIFE_MINUTES"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTimestamp);
        cal.add(Calendar.MINUTE, jwtLifespan);
        Timestamp standardExpireAt = new Timestamp(cal.getTimeInMillis());

        if(standardExpireAt.compareTo(sessionExpireAt) > 0){
            standardExpireAt = sessionExpireAt;
        }

        String payload = JsonManager.getJWTPayload(role, session_id, currentTimestamp, standardExpireAt);

        return SecurityManager.hashJWT(header, payload);
    }

    public String createNewSession(User user, String userIp, String refreshTokenHash, Roles role, int sessionLength, int max_sessions_amount)
            throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, UserNotFoundException, NonUniqueUserException {
        Subscription subscription = this.repository.getRelevantSubscription(user);
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTimestamp);
        cal.add(Calendar.DAY_OF_MONTH, sessionLength);
        Timestamp expectedSessionEnd = new Timestamp(cal.getTimeInMillis());
        if (subscription != null){
            if (subscription.getExpireAt().compareTo(expectedSessionEnd) < 0){
                expectedSessionEnd = subscription.getExpireAt();
            }
        }
        Session session = new Session(user, userIp, refreshTokenHash, currentTimestamp, expectedSessionEnd);
        UUID sessionId = this.repository.saveSession(session, max_sessions_amount);
        return createJWT(role, sessionId);
    }

    //registration
//generate unique refresh -> insert in JWT body
//hash it and store in the DB

    //every JWT creation

//get users refresh token from JWT (/refresh path)
//fetch hashed refresh token from the DB

//create JWT header + body
//hash it -> signature



}
