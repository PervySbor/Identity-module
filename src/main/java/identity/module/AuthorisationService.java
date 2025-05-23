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
import java.util.Map;
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
    protected Properties login(String jsonRequest, String userIp) { //required json: { "login": "<login>", "password": "<hashed_password>"}
        Properties result = new Properties();
        List<String> values;
        String login, hashedPassword;
        User user;
        try {
            values = JsonManager.unwrapPairs(List.of("login", "password"), jsonRequest);
            login = values.get(0);
            hashedPassword = values.get(1);

            user = this.repository.getUserByLogin(login);
            if(user == null){
                //String error = JsonManager.getResponseMessage(404, "Not found", "User with this login doesn't exist");
                String message = "User with this login doesn't exist";
                result.setProperty("message", message);
                result.setProperty("statusCode", "404");
            } else {
                //hashedPassword = identity.module.utils.SecurityManager.hashString(password);
                if(!hashedPassword.equals(user.getPasswordHash())) {
//                    String error = JsonManager.getResponseMessage(403, "Unauthorized", "Incorrect password");
                    String message = "Incorrect password";
                    result.setProperty("message", message);
                    result.setProperty("statusCode", "403");
                } else {
                    String refreshToken = this.sessionManager.generateNewRefreshToken();
                    String refreshTokenHash = SecurityManager.hashString(refreshToken);
                    String jwt = this.sessionManager.createNewSession(user, userIp, refreshTokenHash, user.getRole(), SESSION_LENGTH, MAX_SESSIONS_AMOUNT);
                    result.setProperty("refresh", refreshToken);
                    result.setProperty("jwt", jwt);
                    result.setProperty("statusCode", "200");
                }
            }
        } catch (FailedToHashException | ParsingUserRequestException | NonUniqueUserException |
                 JsonProcessingException | NoSuchAlgorithmException |
                 InvalidKeyException | UserNotFoundException e){
            LogManager.logException(e, Level.SEVERE);
//                String error = JsonManager.getResponseMessage(409, "Conflict", "Login is already taken");
            String message = "Login is already taken";
            result.setProperty("message", message);
            result.setProperty("statusCode", "409");
        }
        return result;
    }//success: refresh, jwt, statusCode OR message

    //UPDATE NEEDED: will fetch user_ip from HttpRequest in Servlet and pass it here as an argument
    //can be used both for user and admin registration
    protected Properties registerUser(String jsonRequest, String userIp) { //required json: { "login": "<login>", "password": "<hashed_password>"}
        Properties result = new Properties();
        List<String> values;
        String login, hashedPassword;
        boolean loginTaken;
        try {
            Roles role = Roles.createRoles("NEW_USER");
            values = JsonManager.unwrapPairs(List.of("login", "password"), jsonRequest);
            login = values.get(0);
            hashedPassword = values.get(1);

            loginTaken = (this.repository).isLoginTaken(login);
            if(loginTaken){
//                String error = JsonManager.getResponseMessage(409, "Conflict", "Login is already taken");
                String message = "Login is already taken";
                result.setProperty("message", message);
                result.setProperty("statusCode", "409");
            } else {
                //hashedPassword = identity.module.utils.SecurityManager.hashString(password);
                User newUser = new User(login, hashedPassword, role);
                this.repository.saveUser(newUser);
                String refreshToken = this.sessionManager.generateNewRefreshToken();
                String refreshTokenHash = SecurityManager.hashString(refreshToken);
                String jwt = this.sessionManager.createNewSession(newUser, userIp, refreshTokenHash, role, SESSION_LENGTH, MAX_SESSIONS_AMOUNT);
                result.setProperty("refresh", refreshToken);
                result.setProperty("jwt", jwt);
                result.setProperty("statusCode", "200");
            }
        } catch (ParsingUserRequestException | NonUniqueUserException | JsonProcessingException |
                 FailedToHashException | NoSuchAlgorithmException | InvalidKeyException | UserNotFoundException |
                 IncorrectRolesType e){
            LogManager.logException(e, Level.SEVERE);
                //String error = JsonManager.getResponseMessage(500, "Internal Server Error", "Encountered exception on server: " +  e.getMessage());
            String message = "Encountered exception on server: " +  e.getMessage();
            result.setProperty("message", message);
            result.setProperty("statusCode", "500");
        }
        return result;
    }//success: refresh, jwt, statusCode OR message


    protected Properties registerAdmin(String jsonRequest) { //required json: { "login": "<login>", "password": "<hashed_password>"}
        Properties result = new Properties();
        List<String> values;
        String login, hashedPassword;
        boolean loginTaken;
        try {
            Roles role = Roles.createRoles("ADMIN");
            values = JsonManager.unwrapPairs(List.of("login", "password"), jsonRequest);
            login = values.get(0);
            hashedPassword = values.get(1);

            loginTaken = (this.repository).isLoginTaken(login);
            if(loginTaken){
                //String error = JsonManager.getResponseMessage(409, "Conflict", "Login is already taken");
                String message = "Login is already taken";
                result.setProperty("message", message);
                result.setProperty("statusCode", "409");
            } else {
                //hashedPassword = identity.module.utils.SecurityManager.hashString(password);
                User newUser = new User(login, hashedPassword, role);
                this.repository.saveUser(newUser);
                String response = JsonManager.getResponseMessage(200, "Ok", "Successfully created admin account");
                result.setProperty("response", response);
                result.setProperty("statusCode", "200");
            }
        } catch (ParsingUserRequestException | NonUniqueUserException | JsonProcessingException |
                 IncorrectRolesType e){
            LogManager.logException(e, Level.SEVERE);
                //String error = JsonManager.getResponseMessage(500, "Internal Server Error", "Encountered exception on server: " +  e.getMessage());
            String message = "Encountered exception on server: " +  e.getMessage();
            result.setProperty("message", message);
            result.setProperty("statusCode", "500");
        }
        return result;
    }//success: response, statusCode OR message


    protected Properties refresh(String json){ //required json: { "refresh": "9bc17b5d-fcae-4c4f-9fab-09d82d64db4e"} //refresh token is not hashed
        Properties result = new Properties();
        try{
            String refreshToken = JsonManager.unwrapPairs(List.of("refresh"), json).getFirst();
            String hashedRefreshToken = SecurityManager.hashString(refreshToken);
            Session session = repository.getRelevantSession(hashedRefreshToken);
            if (session == null){
                //String error = JsonManager.getResponseMessage(404, "Not found", "Session with this refresh token doesn't exist");
                String message = "Session with this refresh token doesn't exist";
                result.setProperty("message", message);
                result.setProperty("statusCode", "404");
            } else {
                String jwt = sessionManager.createJWT(session.getUser().getRole(), session.getSessionId());
                result.setProperty("jwt", jwt);
            }
        } catch (ParsingUserRequestException | FailedToHashException | JsonProcessingException |
                 NoSuchAlgorithmException | InvalidKeyException | RuntimeException e) {
            LogManager.logException(e, Level.SEVERE);
                //String error = JsonManager.getResponseMessage(500, "Internal Server Error", "Encountered exception on server: " +  e.getMessage());
            String message = "Encountered exception on server: " +  e.getMessage();
            result.setProperty("message", message);
            result.setProperty("statusCode", "500");
        }
        return result;
    }//success: jwt, statusCode OR message

    protected String createSubscription(String json){  //required json: { "session_id": "a91afb61-41c8-4972-bde5-538f9174037a", "tx_hash": "001", "subscription_type": "TRIAL"}
        String result;
        UUID sessionId;
        SubscriptionType subscriptionType;
        String txHash;
        try {
            List<String> values = JsonManager.unwrapPairs(List.of("session_id", "subscription_type", "tx_hash"), json);
            sessionId = UUID.fromString(values.get(0));
            subscriptionType = SubscriptionType.createSubscriptionType(values.get(1));
            txHash = values.get(2);
        } catch (IncorrectSubscriptionType | ParsingUserRequestException e) {
            try {
                result = JsonManager.serialize(Map.of("code", "422", "status", "Unprocessable Content",
                        "message", "Failed to deserialize json request (body)"));
            } catch (JsonProcessingException ex){
                System.out.println("magic");
                result = "";
            }
            return result;
        }
        try {
            Session session = this.repository.findSession(sessionId);
            if (session == null){
//                result = JsonManager.getResponseMessage(403, "Unauthorized",
//                        "Session with this id doesn't exist. Most likely something wrong with identity module logic");
                result = JsonManager.serialize(Map.of("code", "403", "status", "Unauthorized",
                        "message", "Session with this id doesn't exist. Most likely something wrong with identity module logic", "tx_hash", txHash));
                LogManager.logException(new SessionNotFoundException("failed to find the session of user, who purchased the subscription"), Level.SEVERE);
            } else {
                boolean alreadyHasSubscription = this.repository.hasSubscription(session.getUser());
                if(alreadyHasSubscription){
//                    result = JsonManager.getResponseMessage(409, "Conflict",
//                            "User is already subscribed");
                    result = JsonManager.serialize(Map.of("code", "409", "status", "Conflict",
                            "message", "User is already subscribed", "tx_hash", txHash));
                } else {
                    Subscription newSubscription = new Subscription(session.getUser(), subscriptionType);
                    this.repository.saveSubscription(newSubscription);
                    //result = JsonManager.getResponseMessage(200, "Ok", "Successfully created subscription");
                    result = JsonManager.serialize(Map.of("code", "200", "status", "Ok",
                            "message", "Successfully created subscription", "tx_hash", txHash));
                }
            }

        } catch (JsonProcessingException e) {
            LogManager.logException(e, Level.SEVERE);
            try {
                //result = JsonManager.getResponseMessage(500, "Internal Server Error", "Encountered exception on server: " +  e.getMessage());
                result = JsonManager.serialize(Map.of("code", "500", "status", "Internal Server Error",
                        "message", "Encountered exception on server: " +  e.getMessage(), "tx_hash", txHash));
            } catch (JsonProcessingException ex){
                LogManager.logException(ex, Level.SEVERE);
                result = "";
            }
        }
        return result;
    }//success: code, status, message, tx_hash

    protected Properties returnError(int statusCode, String shortErrorMsg, String message){
        Properties result = new Properties();
        try {
            String error = JsonManager.getResponseMessage(statusCode, shortErrorMsg, message);
            result.setProperty("error", error);
            result.setProperty("statusCode", String.valueOf(statusCode));
        } catch (JsonProcessingException e) {
            LogManager.logException(e, Level.SEVERE);
            try {
                String error = JsonManager.getResponseMessage(500, "Internal Server Error", "Encountered exception on server: " +  e.getMessage());
                result.setProperty("error", error);
                result.setProperty("statusCode", "500");
            } catch (JsonProcessingException ex){
                LogManager.logException(ex, Level.SEVERE);
            }
        }
        return result;
    }
}
