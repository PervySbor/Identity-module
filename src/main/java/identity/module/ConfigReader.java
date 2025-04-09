package identity.module;

import identity.module.exceptions.ConfigFileNotFoundException;
import identity.module.exceptions.FailedToReadJsonValueException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Files;

public class ConfigReader {

    private final Path config;

    public ConfigReader()
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

    public String getStringValue(JsonManager jsonManager, String property)
            throws IOException, FailedToReadJsonValueException {
        String json = Files.readString(config);
        return jsonManager.getStringValue(json, property);
    }
}
