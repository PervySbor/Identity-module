package identity.module;

import jakarta.servlet.ServletContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;



import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Controller
//runs in a special thread, manages worker threads for processing received messages
public class KafkaConsumerManager implements Runnable{

    private final ExecutorService ex;
    private final KafkaConsumer<String, String> consumer;
    private final AuthorisationService authService;


    public KafkaConsumerManager(int maxAmtOfThreads, String bootServers, String clientId, String consumerGroupName, List<String> topics, AuthorisationService authService){
        ex = Executors.newFixedThreadPool(maxAmtOfThreads);
        this.authService = authService;

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", bootServers);
        props.setProperty("group.id", consumerGroupName);
        props.setProperty("client.id", clientId);
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", StringDeserializer.class.getCanonicalName());
        props.setProperty("value.deserializer", StringDeserializer.class.getCanonicalName());
        consumer = new KafkaConsumer<>(props);

        consumer.subscribe(topics);
    }

    public void run(){
        do {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            if(!records.isEmpty()) {
                for (ConsumerRecord<String, String> record : records) {
                    //{ "session_id": "a91afb61-41c8-4972-bde5-538f9174037a", "subscription_type": "TRIAL"}
                    ConsumerWorker workerRunnable = new ConsumerWorker(this.authService, record.value());
                    ex.execute(workerRunnable);
                }
                consumer.commitAsync();
            }
        } while (!Thread.currentThread().isInterrupted());
        ex.close();
        consumer.close();
    }


}
