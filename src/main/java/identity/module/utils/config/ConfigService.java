package identity.module.utils.config;

import identity.module.utils.JsonManager;
import identity.module.utils.LogManager;

@Deprecated
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

        result = ConfigReader.getStringValue(property);
        return result;
    }

}
