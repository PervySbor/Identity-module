package identity.module;

import jakarta.servlet.ServletContext;

public class ConsumerWorker implements Runnable{

    private AuthorisationService authService;
    private String json;

    public ConsumerWorker(ServletContext context, String json){
        authService = (AuthorisationService) context.getAttribute("AuthorizationService");
        this.json = json;
    }

    public void run(){
        this.authService.createSubscription(json);
    }

}
