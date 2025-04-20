package test.unit;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class ConfigReaderTest {

    @Test
    public void testCorrectFetchingStringValues()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> jsonManagerClass = Class.forName("identity.module.utils.JsonManager");
        Constructor<?> jsonManagerConstructor = jsonManagerClass.getConstructor();
        //Object jsonManagerInstance = jsonManagerConstructor.newInstance();

        Class<?> configReaderClass = Class.forName("identity.module.utils.config.ConfigReader");
        //Constructor<?> configReaderConstructor = configReaderClass.getDeclaredConstructor();
        //configReaderConstructor.setAccessible(true);
        //Object configReaderInstance = configReaderConstructor.newInstance();
        Method getStringValue = configReaderClass.getDeclaredMethod("getStringValue", String.class);
        getStringValue.setAccessible(true);

        String result = (String) getStringValue.invoke(null,  "DB_URL");
        assertEquals("jdbc:postgresql://localhost:5432/identity", result);
        result = (String) getStringValue.invoke(null, "DB_LOGIN" );
        assertEquals("postgres", result);
    }

    @Test
    public void testCorrectFetchingListValues()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> jsonManagerClass = Class.forName("identity.module.utils.JsonManager");
        Constructor<?> jsonManagerConstructor = jsonManagerClass.getConstructor();
        //Object jsonManagerInstance = jsonManagerConstructor.newInstance();

        Class<?> configReaderClass = Class.forName("identity.module.utils.config.ConfigReader");
        //Constructor<?> configReaderConstructor = configReaderClass.getDeclaredConstructor();
        //configReaderConstructor.setAccessible(true);
        //Object configReaderInstance = configReaderConstructor.newInstance();
        Method getListValue = configReaderClass.getDeclaredMethod("getListValue", String.class);
        getListValue.setAccessible(true);

        List<String> result =(List<String>) getListValue.invoke(null,  "KAFKA_BROKERS");
        assertFalse(result.isEmpty());
        System.out.println(result);
    }
}
