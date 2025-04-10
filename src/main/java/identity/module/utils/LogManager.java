package identity.module.utils;

import identity.module.models.LogMessage;

import java.sql.Timestamp;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogManager {
    static final Logger logger = Logger.getLogger("myLogger");

     static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
    }

    public static String logException(Exception exception, Level level){
        String jsonLogMessage = "";
        StackTraceElement[] elements = exception.getStackTrace();

        logger.log(Level.INFO, "Currently logging exception: ", exception);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        LogMessage logMessage = new LogMessage(exception.getMessage(), timestamp, level);

        for(StackTraceElement element : elements){
            String message = element.toString();
            logMessage.addTraceElement(message);
        }

        try {
            jsonLogMessage = JsonManager.serialize(logMessage);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e){
            logger.log(Level.SEVERE, "Failed to serialize LogMessage: ", e);
        }
        //assuming call to KafkaClient: sendLog(String errorJson)
        return jsonLogMessage;
    }
}
