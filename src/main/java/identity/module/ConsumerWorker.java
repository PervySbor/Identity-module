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
        topicToRespondTo = ConfigReader.getStringValue("TOPIC_TO_RESPOND_TO");
        responseKey = ConfigReader.getStringValue("RESPONSE_KEY");
        partition = Integer.parseInt(ConfigReader.getStringValue("PARTITION_TO_RESPOND_TO"));
    }

    public void run(){
        String response = this.authService.createSubscription(json);
        KafkaProducerManager.send(topicToRespondTo, partition, responseKey, response);
    }

}
