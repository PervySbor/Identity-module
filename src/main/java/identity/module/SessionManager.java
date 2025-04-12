package identity.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import identity.module.enums.Roles;
import identity.module.exceptions.NonUniqueSubscriptionException;
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

public class SessionManager {


    public static String generateNewRefreshToken(){
        return UUID.randomUUID().toString();
    }

    public static String createJWT(Roles role, UUID session_id)
            throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

        String payload = JsonManager.getJWTPayload(role, session_id);

        return SecurityManager.hashJWT(header, payload);
    }

    public static  String createNewSession(User user, String userIp, String refreshTokenHash, Roles role, int sessionLength, int max_sessions_amount)
            throws NonUniqueSubscriptionException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        Subscription subscription = Repository.getSubscriptionByUserId(user.getUserId());
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
        UUID sessionId = Repository.saveSession(session, max_sessions_amount);
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
