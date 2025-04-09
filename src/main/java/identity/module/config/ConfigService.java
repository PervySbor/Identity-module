package identity.module.config;

import identity.module.JsonManager;
import identity.module.LogManager;
import identity.module.exceptions.ConfigFileNotFoundException;
import identity.module.exceptions.FailedToReadJsonValueException;
import identity.module.exceptions.FatalException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

public class ConfigService {

    private final LogManager logManager;
    private final JsonManager jsonManager;

    public ConfigService(LogManager logManager, JsonManager jsonManager){
        this.logManager = logManager;
        this.jsonManager = jsonManager;
    }

    public String getStringValue(String property) {

        ConfigReader configReader;
        String result = "";

        try {
            configReader = new ConfigReader();
            result = configReader.getStringValue(jsonManager, property);
        } catch (ConfigFileNotFoundException | URISyntaxException | FailedToReadJsonValueException | IOException e) {
            logManager.logException(jsonManager, e, Level.CONFIG);
        }
        return result;
    }

}
