package test;

import org.junit.Assert;
import org.junit.Test;
//import identity.module.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class JsonManagerTest {

    @Test
    public void testUnwrapPairs()
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
        List<String> headers = List.of("user", "password");
        String jsonString = "{\"user\": \"usr1\", \"password\": \"passwd\"}";
        String expectedResult = "[usr1, passwd]";

        Class<?> jsonManagerClass = Class.forName("identity.module.JsonManager");
        Constructor<?> jsonManagerConstructor = jsonManagerClass.getConstructor();
        Object jsonManagerInstance = jsonManagerConstructor.newInstance();
        Method unwrapPair = jsonManagerClass.getDeclaredMethod("unwrapPairs", List.class, String.class);
        unwrapPair.setAccessible(true);

        String result = unwrapPair.invoke(jsonManagerInstance, headers, jsonString).toString();

        Assert.assertEquals(expectedResult, result);
    }
}
