package identity.module;

import identity.module.repository.utils.MyHikariDataSource;
import identity.module.utils.KafkaProducerManager;
import identity.module.utils.config.ConfigReader;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent ev){

        List<String> bootServers = ConfigReader.getListValue("KAFKA_BROKERS");
        String bootServersString = String.join(",", bootServers);
        String clientId = ConfigReader.getStringValue("CONTAINER_NAME");

        KafkaProducerManager.init(bootServersString, clientId);
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev){
        KafkaProducerManager.destroy();
        MyHikariDataSource.destroy(); //destroying HikariCp to prevent memory leakage
    }
}
