package identity.module;

import identity.module.utils.SecurityManager;

import java.util.List;

public class Temp {
    //temporary endpoint to test AuthorisationService
    public static void main(String[] args) throws Exception{
        System.out.println("<" + SecurityManager.hashString("password") + ">");
    }
}
