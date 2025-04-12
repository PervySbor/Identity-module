package identity.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import identity.module.enums.Roles;
import identity.module.exceptions.FailedToHashException;
import identity.module.exceptions.NonUniqueSubscriptionException;
import identity.module.exceptions.NonUniqueUserException;
import identity.module.exceptions.ParsingUserRequestException;
import identity.module.repository.Repository;
import identity.module.repository.entities.User;
import identity.module.utils.JsonManager;
import identity.module.utils.LogManager;
import identity.module.utils.SecurityManager;
import identity.module.utils.config.ConfigReader;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

public class AuthorisationService {

    private final int SESSION_LENGTH = Integer.parseInt(ConfigReader.getStringValue("SESSION_LENGTH"));
    private final int MAX_SESSIONS_AMOUNT  =  Integer.parseInt(ConfigReader.getStringValue("MAX_SESSIONS_AMOUNT"));

    //TO BE REFACTORED
    //can be used both for user and admin login
    protected Properties login(String jsonRequest) { //required json: { "login": "<login>", "password": "<hashed_password>", "user_ip":  "<not_hashed_ip>"}
        Properties result = new Properties();
        List<String> values;
        String login, password, userIp, hashedPassword;
        User user;
        boolean loginTaken;
        try {
            values = JsonManager.unwrapPairs(List.of("login", "password", "user_ip"), jsonRequest);
            login = values.get(0);
            password = values.get(1);
            userIp = values.get(2);

            user = Repository.getUserByLogin(login);
        } catch (ParsingUserRequestException | NonUniqueUserException e){
            LogManager.logException(e, Level.FINE);
            return result;  //in case message is corrupted
        }
        if(user == null){
            try {
                String error = JsonManager.getResponseMessage(404, "Not found", "User with this login doesn't exist");
                result.setProperty("error", error);
            } catch (JsonProcessingException e){
                LogManager.logException(e, Level.SEVERE); //something wrong with the logic (or I messed up with the arguments)
                return result;
            }
        }
        else {
            try {
                hashedPassword = identity.module.utils.SecurityManager.hashString(password);
                if(!hashedPassword.equals(user.getPasswordHash())) {
                    String error = JsonManager.getResponseMessage(404, "Not found", "User with this login doesn't exist");
                    result.setProperty("error", error);
                    return result;
                }
            } catch (FailedToHashException | JsonProcessingException e) {
                LogManager.logException(e, Level.SEVERE); //something wrong with the logic (or I messed up with the arguments)
                return result;
            }
            String refreshToken = SessionManager.generateNewRefreshToken();
            try{
                String refreshTokenHash = SecurityManager.hashString(refreshToken);
                String jwt = SessionManager.createNewSession(user, userIp, refreshTokenHash, user.getRole(), SESSION_LENGTH, MAX_SESSIONS_AMOUNT);
                result.setProperty("refresh_token", refreshToken);
                result.setProperty("jwt", jwt);
            } catch (FailedToHashException | NonUniqueSubscriptionException | NoSuchAlgorithmException |
                     InvalidKeyException | JsonProcessingException e) {
                LogManager.logException(e, Level.SEVERE); //something wrong with the logic (or I messed up with the arguments)
                return result;
            }
        }
        return result;
    }

    //can be used both for user and admin registration
    protected Properties register(String jsonRequest, Roles role) { //required json: { "login": "<login>", "password": "<hashed_password>", "user_ip":  "<not_hashed_ip>"}
        Properties result = new Properties();
        List<String> values;
        String login, password, userIp, hashedPassword;
        boolean loginTaken;
        try {
            values = JsonManager.unwrapPairs(List.of("login", "password", "user_ip"), jsonRequest);
            login = values.get(0);
            password = values.get(1);
            userIp = values.get(2);

            loginTaken = Repository.isLoginTaken(login);
        } catch (ParsingUserRequestException | NonUniqueUserException e){
            LogManager.logException(e, Level.FINE);
            return result;  //in case message is corrupted
        }
        if(loginTaken){
            try {
                String error = JsonManager.getResponseMessage(409, "Conflict", "Login is already taken");
                result.setProperty("error", error);
            } catch (JsonProcessingException e){
                LogManager.logException(e, Level.SEVERE); //something wrong with the logic (or I messed up with the arguments)
                return result;
            }
        }
        else {
            try {
                hashedPassword = identity.module.utils.SecurityManager.hashString(password);
            } catch (FailedToHashException e) {
                LogManager.logException(e, Level.SEVERE); //something wrong with the logic (or I messed up with the arguments)
                return result;
            }
            User newUser = new User(login, hashedPassword, role);
            String refreshToken = SessionManager.generateNewRefreshToken();
            try{
                String refreshTokenHash = SecurityManager.hashString(refreshToken);
                String jwt = SessionManager.createNewSession(newUser, userIp, refreshTokenHash, role, SESSION_LENGTH, MAX_SESSIONS_AMOUNT);
                result.setProperty("refresh_token", refreshToken);
                result.setProperty("jwt", jwt);
            } catch (FailedToHashException | NonUniqueSubscriptionException | NoSuchAlgorithmException |
                     InvalidKeyException | JsonProcessingException e) {
                LogManager.logException(e, Level.SEVERE); //something wrong with the logic (or I messed up with the arguments)
                return result;
            }
        }
        return result;
    }
}
