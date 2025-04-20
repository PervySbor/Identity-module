package identity.module;

import identity.module.repository.utils.MyHikariDataSource;
import identity.module.utils.KafkaProducerManager;
import identity.module.utils.config.ConfigReader;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent ev){

        ServletContext ctx = ev.getServletContext();
        ExecutorService kafkaExecutor = Executors.newSingleThreadExecutor();

        ctx.setAttribute("AuthorisationService", new AuthorisationService());
        ctx.setAttribute("kafkaExecutor", kafkaExecutor);

        List<String> bootServers = ConfigReader.getListValue("KAFKA_BROKERS");
        String bootServersString = String.join(",", bootServers);
        String clientId = ConfigReader.getStringValue("CONTAINER_NAME");

        KafkaProducerManager.init(bootServersString, clientId + "-producer");

        int maxAmtOfThreads = Integer.parseInt(ConfigReader.getStringValue("CONSUMER_THREADS_AMT"));
        String consumerGroupName = ConfigReader.getStringValue("CONSUMER_GROUP_NAME");
        List<String> topicsToRead = ConfigReader.getListValue("TOPICS_TO_READ_FROM");

        KafkaConsumerManager consumerManager =
                new KafkaConsumerManager(maxAmtOfThreads, bootServersString, clientId + "-consumer", consumerGroupName, topicsToRead, ctx);

        kafkaExecutor.execute(consumerManager);

    }

    @Override
    public void contextDestroyed(ServletContextEvent ev){
        ExecutorService kafkaExecutor = (ExecutorService) ev.getServletContext().getAttribute("kafkaExecutor");
        kafkaExecutor.shutdownNow();
        try {
            kafkaExecutor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw (RuntimeException) new RuntimeException("failed to shutdown kafkaExecutor").initCause(e);
        }
        KafkaProducerManager.destroy();
        MyHikariDataSource.destroy(); //destroying HikariCp to prevent memory leakage
    }
}
