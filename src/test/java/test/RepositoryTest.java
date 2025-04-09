package test;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepositoryTest {

    @Test
    public void testIsLoginTaken_newLogin()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> jsonManagerClass = Class.forName("identity.module.JsonManager");
        Constructor<?> jsonManagerConstructor = jsonManagerClass.getConstructor();
        Object jsonManagerInstance = jsonManagerConstructor.newInstance();

        Class<?> logManagerClass = Class.forName("identity.module.LogManager");
        Constructor<?> logManagerConstructor = logManagerClass.getConstructor();
        Object logManagerInstance = logManagerConstructor.newInstance();

        Class<?> ConfigServiceClass = Class.forName("identity.module.config.ConfigService");
        Constructor<?> ConfceCigServionstructor = ConfigServiceClass.getConstructor(logManagerClass, jsonManagerClass);
        Object ConfigServiceInstance = ConfceCigServionstructor.newInstance(logManagerInstance, jsonManagerInstance);

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Constructor<?> repositoryConstructor = repositoryClass.getConstructor(ConfigServiceClass);
        Method isLoginTaken = repositoryClass.getMethod("isLoginTaken", String.class);
        Object repositoryInstance = repositoryConstructor.newInstance(ConfigServiceInstance);

        boolean result = (boolean) isLoginTaken.invoke(repositoryInstance, "new_login");

        assertFalse(result);
    }

}
