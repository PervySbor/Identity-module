package identity.module;

import identity.module.repository.utils.MyHikariDataSource;
import identity.module.utils.ServiceLocator;
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
        ServletContext context = ev.getServletContext();

        Properties props = new Properties();
        List<String> bootServers = ConfigReader.getListValue("KAFKA_BROKERS");
        String bootServersString = String.join(",", bootServers);
        props.setProperty("bootstrap.servers", bootServersString);
        String clientId = ConfigReader.getStringValue("CONTAINER_NAME");
        props.setProperty("client.id", clientId);
        props.setProperty("enable.idempotence", "true");
        props.setProperty("key.serializer", StringSerializer.class.getCanonicalName());
        props.setProperty("value.serializer", StringSerializer.class.getCanonicalName());
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        context.setAttribute("KafkaProducer", producer);
        ServiceLocator.init(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev){
        ServletContext context = ev.getServletContext();
        KafkaProducer<?,?> producer = (KafkaProducer<?,?>) context.getAttribute("KafkaProducer");
        if(producer != null){
            producer.close(Duration.ofSeconds(5));
        }
        MyHikariDataSource.destroy(); //destroying HikariCp to prevent memory leakage
    }
}
