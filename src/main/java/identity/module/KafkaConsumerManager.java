package identity.module;

import jakarta.servlet.ServletContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringSerializer;


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
    private ServletContext context;


    public KafkaConsumerManager(int maxAmtOfThreads, String bootServers, String clientId, String consumerGroupName, List<String> topics, ServletContext context){
        ex = Executors.newFixedThreadPool(maxAmtOfThreads);

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", bootServers);
        props.setProperty("group.id", consumerGroupName);
        props.setProperty("client.id", clientId);
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", StringSerializer.class.getCanonicalName());
        props.setProperty("value.deserializer", StringSerializer.class.getCanonicalName());
        consumer = new KafkaConsumer<>(props);

        consumer.subscribe(topics);
    }

    public void run(){
        do {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                //{ "session_id": "a91afb61-41c8-4972-bde5-538f9174037a", "subscription_type": "TRIAL"}
                ConsumerWorker workerRunnable = new ConsumerWorker(context, record.value());
                ex.execute(workerRunnable);
            }
            consumer.commitAsync();
        } while (!Thread.currentThread().isInterrupted());
        ex.close();
        consumer.close();
    }


}
