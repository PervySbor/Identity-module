package identity.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import identity.module.enums.Roles;
import identity.module.enums.SubscriptionType;
import identity.module.exceptions.*;
import identity.module.repository.Repository;
import identity.module.repository.entities.Session;
import identity.module.repository.entities.Subscription;
import identity.module.repository.entities.User;
import identity.module.utils.JsonManager;
import identity.module.utils.LogManager;
import identity.module.utils.SecurityManager;
import identity.module.utils.config.ConfigReader;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

public class AuthorisationService {

    private final int SESSION_LENGTH = Integer.parseInt(ConfigReader.getStringValue("SESSION_LENGTH"));
    private final int MAX_SESSIONS_AMOUNT  =  Integer.parseInt(ConfigReader.getStringValue("MAX_SESSIONS_AMOUNT"));
    private final Repository repository = new Repository();
    private final SessionManager sessionManager = new SessionManager(repository);

    //TO BE REFACTORED
    //can be used both for user and admin login
    protected Properties login(String jsonRequest) { //required json: { "login": "<login>", "password": "<hashed_password>", "user_ip":  "<not_hashed_ip>"}
        Properties result = new Properties();
        List<String> values;
        String login, userIp, hashedPassword;
        User user;
        try {
            values = JsonManager.unwrapPairs(List.of("login", "password", "user_ip"), jsonRequest);
            login = values.get(0);
            hashedPassword = values.get(1);
            userIp = values.get(2);

            user = this.repository.getUserByLogin(login);
            if(user == null){
                String error = JsonManager.getResponseMessage(404, "Not found", "User with this login doesn't exist");
                result.setProperty("error", error);
            } else {
                //hashedPassword = identity.module.utils.SecurityManager.hashString(password);
                if(!hashedPassword.equals(user.getPasswordHash())) {
                    String error = JsonManager.getResponseMessage(403, "Unauthorized", "Incorrect password");
                    result.setProperty("error", error);
                } else {
                    String refreshToken = this.sessionManager.generateNewRefreshToken();
                    String refreshTokenHash = SecurityManager.hashString(refreshToken);
                    String jwt = this.sessionManager.createNewSession(user, userIp, refreshTokenHash, user.getRole(), SESSION_LENGTH, MAX_SESSIONS_AMOUNT);
                    result.setProperty("refresh_token", refreshToken);
                    result.setProperty("jwt", jwt);
                }
            }
        } catch (FailedToHashException | ParsingUserRequestException | NonUniqueUserException |
                 JsonProcessingException | NoSuchAlgorithmException |
                 InvalidKeyException | UserNotFoundException e){
            LogManager.logException(e, Level.FINE);
            return null;  //in case message is corrupted
        }
        return result;
    }

    //can be used both for user and admin registration
    protected Properties register(String jsonRequest, Roles role) { //required json: { "login": "<login>", "password": "<hashed_password>", "user_ip":  "<not_hashed_ip>"}
        Properties result = new Properties();
        List<String> values;
        String login, hashedPassword, userIp;
        boolean loginTaken;
        try {
            values = JsonManager.unwrapPairs(List.of("login", "password", "user_ip"), jsonRequest);
            login = values.get(0);
            hashedPassword = values.get(1);
            userIp = values.get(2);

            loginTaken = (this.repository).isLoginTaken(login);
            if(loginTaken){
                String error = JsonManager.getResponseMessage(409, "Conflict", "Login is already taken");
                result.setProperty("error", error);
            } else {
                //hashedPassword = identity.module.utils.SecurityManager.hashString(password);
                User newUser = new User(login, hashedPassword, role);
                this.repository.saveUser(newUser);
                String refreshToken = this.sessionManager.generateNewRefreshToken();
                String refreshTokenHash = SecurityManager.hashString(refreshToken);
                String jwt = this.sessionManager.createNewSession(newUser, userIp, refreshTokenHash, role, SESSION_LENGTH, MAX_SESSIONS_AMOUNT);
                result.setProperty("refresh_token", refreshToken);
                result.setProperty("jwt", jwt);
            }
        } catch (ParsingUserRequestException | NonUniqueUserException | JsonProcessingException |
                 FailedToHashException | NoSuchAlgorithmException |
                 InvalidKeyException | UserNotFoundException e){
            LogManager.logException(e, Level.FINE);
            return null;  //in case message is corrupted
        }
        return result;
    }


    protected Properties refresh(String json){ //required json: { "refresh_token": "9bc17b5d-fcae-4c4f-9fab-09d82d64db4e"} //refresh token is not hashed
        Properties result = new Properties();
        try{
            String refreshToken = JsonManager.unwrapPairs(List.of("refresh_token"), json).getFirst();
            String hashedRefreshToken = SecurityManager.hashString(refreshToken);
            Session session = repository.getRelevantSession(hashedRefreshToken);
            if (session == null){
                String error = JsonManager.getResponseMessage(404, "Not found", "Session with this refresh token doesn't exist");
                result.setProperty("error", error);
            } else {
                String jwt = sessionManager.createJWT(session.getUser().getRole(), session.getSessionId());
                result.setProperty("jwt", jwt);
            }
        } catch (ParsingUserRequestException | FailedToHashException | JsonProcessingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            LogManager.logException(e, Level.FINE);
            return null;  //in case message is corrupted
        }
        return result;
    }

    protected Properties createSubscription(String json){  //required json: { "session_id": "a91afb61-41c8-4972-bde5-538f9174037a", "subscription_type": "TRIAL"}
        Properties result = new Properties();
        try {
            List<String> values = JsonManager.unwrapPairs(List.of("session_id", "subscription_type"), json);
            UUID sessionId = UUID.fromString(values.get(0));
            SubscriptionType subscriptionType = SubscriptionType.createSubscriptionType(values.get(1));
            Session session = this.repository.findSession(sessionId);
            if (session == null){
                String error = JsonManager.getResponseMessage(403, "Unauthorized",
                        "Session with this id doesn't exist. Most likely something wrong with identity module logic");
                result.setProperty("error", error);
                LogManager.logException(new SessionNotFoundException("failed to find the session of user, who purchased the subscription"), Level.SEVERE);
            } else {
                boolean alreadyHasSubscription = this.repository.hasSubscription(session.getUser());
                if(alreadyHasSubscription){
                    String error = JsonManager.getResponseMessage(409, "Conflict",
                            "User is already subscribed");
                    result.setProperty("error", error);
                } else {
                    Subscription newSubscription = new Subscription(session.getUser(), subscriptionType);
                    this.repository.saveSubscription(newSubscription);
                    String response = JsonManager.getResponseMessage(200, "Ok", "Successfully created subscription");
                    result.setProperty("response", response);
                }
            }

        } catch (ParsingUserRequestException | IncorrectSubscriptionType | JsonProcessingException e) {
            LogManager.logException(e, Level.FINE);
            return null;  //in case message is corrupted
        }
        return result;
    }
}
