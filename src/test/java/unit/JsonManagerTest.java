package unit;

import org.junit.Assert;
import org.junit.Test;
//import identity.module.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JsonManagerTest {

    @Test
    public void testUnwrapPairs()
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
        List<String> headers = List.of("user", "password");
        String jsonString = "{\"user\": \"usr1\", \"password\": \"passwd\"}";
        String expectedResult = "[usr1, passwd]";

        Class<?> jsonManagerClass = Class.forName("identity.module.utils.JsonManager");
        //Constructor<?> jsonManagerConstructor = jsonManagerClass.getConstructor();
        //Object jsonManagerInstance = jsonManagerConstructor.newInstance();
        Method unwrapPair = jsonManagerClass.getDeclaredMethod("unwrapPairs", List.class, String.class);
        unwrapPair.setAccessible(true);

        String result = unwrapPair.invoke(null, headers, jsonString).toString();

        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetStringValue() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String jsonString = "{\"user\": \"usr1\", \"password\": \"passwd\"}";
        String searchedProperty = "password";
        String expectedResult = "passwd";

        Class<?> jsonManagerClass = Class.forName("identity.module.utils.JsonManager");
        Method getStringValue = jsonManagerClass.getDeclaredMethod("getStringValue", String.class, String.class);
        getStringValue.setAccessible(true);

        String result = (String) getStringValue.invoke(null, jsonString, searchedProperty);

        assertEquals(expectedResult, result);
    }
    @Test
    public void testGetErrorMessage() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int statusCode = 409;
        String error = "Conflict";
        String message = "Login is already taken";
        String expectedResult = "{\"status\":409,\"statusText\":\"Conflict\",\"message\":\"Login is already taken\"}";

        Class<?> jsonManagerClass = Class.forName("identity.module.utils.JsonManager");
        Method getErrorMessage = jsonManagerClass.getDeclaredMethod("getResponseMessage", int.class, String.class, String.class);
        getErrorMessage.setAccessible(true);

        String result = (String) getErrorMessage.invoke(null, statusCode, error, message);

        assertEquals(expectedResult, result);
    }
}