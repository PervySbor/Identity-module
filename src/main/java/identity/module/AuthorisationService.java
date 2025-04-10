package identity.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import identity.module.exceptions.FailedToEncryptException;
import identity.module.exceptions.NonUniqueUserException;
import identity.module.exceptions.ParsingUserRequestException;
import identity.module.repository.Repository;
import identity.module.repository.entities.User;
import identity.module.utils.JsonManager;
import identity.module.utils.LogManager;
import identity.module.utils.SecurityManager;

import java.util.List;
import java.util.logging.Level;

public class AuthorisationService {

    private final Repository repository = new Repository();

    //returns JWT string
    protected String login(String jsonRequest) { //required json: { "login": "<login>", "password": "<hashed_password>", "user_ip":  "<not_hashed_ip>"}
        try {
            JsonManager.unwrapPairs(List.of("login", "password"), jsonRequest);
        } catch (ParsingUserRequestException e){
            LogManager.logException(e, Level.FINE);
            return "";  //in case message is corrupted
        }


        return "";
    }

    protected String register(String jsonRequest) { //required json: { "login": "<login>", "password": "<hashed_password>", "user_ip":  "<not_hashed_ip>"}
        List<String> values;
        String login, password, hashedPassword;
        boolean loginTaken;
        try {
            values = JsonManager.unwrapPairs(List.of("login", "password"), jsonRequest);
            login = values.get(0);
            password = values.get(1);

            loginTaken = repository.isLoginTaken(login);
        } catch (ParsingUserRequestException | NonUniqueUserException e){
            LogManager.logException(e, Level.FINE);
            return "";  //in case message is corrupted
        }
        if(loginTaken){
            try {
                return JsonManager.getErrorMessage(409, "Conflict", "Login is already taken");
            } catch (JsonProcessingException e){
                LogManager.logException(e, Level.SEVERE); //something wrong with the logic (or I messed up with the arguments)
                return "";
            }
        }
        else {
            try {
                hashedPassword = SecurityManager.encryptPassword(password);
            } catch (FailedToEncryptException e){
                LogManager.logException(e, Level.SEVERE); //something wrong with the logic (or I messed up with the arguments)
                return "";
            }
            User newUser = new User(login , hashedPassword);
            //I don't know how to create Session here as I need to get userId,
            //which will only be generated after persisting User to the Context

            /*
             * registration logic
             */
            return "";
        }
    }
}
