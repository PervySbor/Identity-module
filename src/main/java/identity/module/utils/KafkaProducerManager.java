package identity.module.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.logging.Logger;

//encapsulates interactions with KafkaProducer
public class KafkaProducerManager {
    static final Logger logger = Logger.getLogger("myLogger");

    @SuppressWarnings("unchecked")
    public static void send(String topic, Integer partition, String key, String value){
        KafkaProducer<String, String> producer;
        try {

            producer = (KafkaProducer<String, String>) ServiceLocator.getService("KafkaProducer");
        } catch (ClassCastException e){
            logger.severe("Failed to fetch KafkaProducer");
            return;
        }
        producer.send(new ProducerRecord<>(topic, partition, key, value));
    }
}
