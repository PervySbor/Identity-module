package identity.module.kafka.clients;

import identity.module.utils.KafkaProducerManager;
import identity.module.utils.config.ConfigReader;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringSerializer;


import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//runs in a special thread, manages worker threads for processing received messages
public class KafkaConsumerManager implements Runnable{

    private  ExecutorService ex;
    private KafkaConsumer<String, String> consumer;

    public KafkaConsumerManager(int maxAmtOfThreads, String bootServers, String clientId, String consumerGroupName, List<String> topics){
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
        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<String, String> record : records){

            }
        }
    }


}
