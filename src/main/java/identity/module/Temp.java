package identity.module;

public class Temp {
    //temporary endpoint to test AuthorisationService
    public static void main(String[] args) throws Exception{
        AuthorisationService authService = new AuthorisationService();
        String json = "{ \"login\": \"valid_login\", \"password\": \"V7XPqTkux3VORMqcuGOoLQ==\"}";
        System.out.println(authService.registerUser(json, "NEW_USER"));
        System.out.println(authService.login(json, "127.0.0.1"));

    }
}
