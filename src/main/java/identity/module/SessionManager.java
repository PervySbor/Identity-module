package identity.module;

import java.util.UUID;

public class SessionManager {


    public static String generateNewRefreshToken(){
        return UUID.randomUUID().toString();
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
