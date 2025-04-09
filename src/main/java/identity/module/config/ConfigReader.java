package identity.module.config;

import identity.module.JsonManager;
import identity.module.exceptions.ConfigFileNotFoundException;
import identity.module.exceptions.FailedToReadJsonValueException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Files;

class ConfigReader {

    private final Path config;

    protected ConfigReader()
            throws ConfigFileNotFoundException, URISyntaxException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL filePath = loader.getResource("config.json");
        if(filePath == null){
            throw new ConfigFileNotFoundException("");
        }
        config = Path.of(filePath.toURI());
        if (!Files.exists(config)) {
            throw new ConfigFileNotFoundException("");
        }
    }

    protected String getStringValue(JsonManager jsonManager, String property)
            throws IOException, FailedToReadJsonValueException {
        String json = Files.readString(config);
        return jsonManager.getStringValue(json, property);
    }
}
