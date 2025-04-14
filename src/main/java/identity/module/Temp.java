package identity.module;

import identity.module.enums.Roles;
import identity.module.utils.SecurityManager;

import java.util.List;

public class Temp {
    //temporary endpoint to test AuthorisationService
    public static void main(String[] args) throws Exception{
        AuthorisationService authService = new AuthorisationService();
        String json = "{ \"login\": \"valid_login\", \"password\": \"V7XPqTkux3VORMqcuGOoLQ==\", \"user_ip\":  \"127.0.0.1\"}";
        System.out.println(authService.register(json, Roles.NEW_USER));
        System.out.println(authService.login(json));

    }
}
