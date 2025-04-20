package identity.module;

import identity.module.utils.config.ConfigReader;
import jakarta.servlet.ServletContext;

public class ConsumerWorker implements Runnable{

    private AuthorisationService authService;
    private String json;
    private String topicToRespondTo;
    private String responseKey;
    private int partition;

    public ConsumerWorker(ServletContext context, String json){
        authService = (AuthorisationService) context.getAttribute("AuthorizationService");
        this.json = json;
        topicToRespondTo = ConfigReader.getStringValue("SUBSCRIPTION_RESPONSE_TOPIC");
        responseKey = ConfigReader.getStringValue("SUB_RESPONSE_KEY");
        partition = Integer.parseInt(ConfigReader.getStringValue("SUB_RESPONSE_PARTITION"));
    }

    public void run(){
        String response = this.authService.createSubscription(json);
        KafkaProducerManager.send(topicToRespondTo, partition, responseKey, response);
    }

}
