package identity.module;

import java.util.List;

public class Temp {
    //temporary endpoint to test AuthorisationService
    public static void main(String[] args) throws Exception{
        List<String> result = JsonManager.unwrapPairs(List.of("user", "password"), "{\"user\": \"usr1\", \"password\": \"passwd\"}");
        System.out.println(result);
    }
}
