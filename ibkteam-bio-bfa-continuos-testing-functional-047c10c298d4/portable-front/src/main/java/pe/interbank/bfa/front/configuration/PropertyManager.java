package pe.interbank.bfa.front.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class PropertyManager {
    private static Properties instance = null;
    private static final String APPLICATION_PREFIX = "application";
    private static final String APPLICATION_SUFFIX = "properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyManager.class);

    public PropertyManager() {
    }

    public static synchronized Properties getInstance() {
        if (instance == null) {
            instance = loadPropertiesFile();
        }
        return instance;
    }

    private static Properties loadPropertiesFile() {
        String environment = Optional.ofNullable(System.getProperty("environment")).orElse("dev");
        String fileName = String.format("%s-%s.%s", APPLICATION_PREFIX, environment, APPLICATION_SUFFIX);
        LOGGER.info("##FILENAME -> {}" , fileName);
        Properties prop = new Properties();
        FileInputStream stream = null;
        try {
            var file = System.getProperty("user.dir") + File.separator + fileName;
            stream = new FileInputStream(file);
            prop.load(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
