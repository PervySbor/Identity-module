package test;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigReaderTest {

    @Test
    public void testCorrectFetchingStringValues() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> jsonManagerClass = Class.forName("identity.module.JsonManager");
        Constructor<?> jsonManagerConstructor = jsonManagerClass.getConstructor();
        Object jsonManagerInstance = jsonManagerConstructor.newInstance();

        Class<?> configReaderClass = Class.forName("identity.module.ConfigReader");
        Constructor<?> configReaderConstructor = configReaderClass.getConstructor();
        Object configReaderInstance = configReaderConstructor.newInstance();
        Method getStringValue = configReaderClass.getMethod("getStringValue",jsonManagerClass, String.class);

        String result = (String) getStringValue.invoke(configReaderInstance, jsonManagerInstance,  "DB_URL");
        assertEquals("jdbc:postgresql://localhost:5432/identity", result);
        result = (String) getStringValue.invoke(configReaderInstance, jsonManagerInstance, "DB_LOGIN" );
        assertEquals("postgres", result);
    }
}
