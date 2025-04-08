package identity.module;

import java.util.List;

public class AuthorisationService {

    //returns JWT string
    protected String login(String jsonRequest){ //required json: { "login": "<login>", "password": "<hashed_password>", "user_ip":  "<not_hashed_ip>"}
        //JsonManager.unwrapPairs(List.of("user", "password"), jsonRequest);
        return "";
    }
}
