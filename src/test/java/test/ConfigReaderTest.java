package test;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ConfigReaderTest {

    @Test
    public void testCorrectFetchingStringValues()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> jsonManagerClass = Class.forName("identity.module.JsonManager");
        Constructor<?> jsonManagerConstructor = jsonManagerClass.getConstructor();
        Object jsonManagerInstance = jsonManagerConstructor.newInstance();

        Class<?> configReaderClass = Class.forName("identity.module.config.ConfigReader");
        Constructor<?> configReaderConstructor = configReaderClass.getDeclaredConstructor();
        configReaderConstructor.setAccessible(true);
        Object configReaderInstance = configReaderConstructor.newInstance();
        Method getStringValue = configReaderClass.getDeclaredMethod("getStringValue",jsonManagerClass, String.class);
        getStringValue.setAccessible(true);

        String result = (String) getStringValue.invoke(configReaderInstance, jsonManagerInstance,  "DB_URL");
        assertEquals("jdbc:postgresql://localhost:5432/identity", result);
        result = (String) getStringValue.invoke(configReaderInstance, jsonManagerInstance, "DB_LOGIN" );
        assertEquals("postgres", result);
    }
}
