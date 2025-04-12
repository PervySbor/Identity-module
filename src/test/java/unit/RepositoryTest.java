package unit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryTest {

    @Before
    public void loadEntitiesForTest()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "NEW_USER");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
        Object userInstance = userConstructor.newInstance("login_1", "password_hash", newUserRole);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Method saveUser = repositoryClass.getMethod("saveUser", userClass);
        System.out.println(saveUser.invoke(null, userInstance));

        System.out.println("Finished preparations");

    }

    @After
    public void deleteEntitiesAfterTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Method deleteUser = repositoryClass.getMethod("deleteUser", String.class);
        deleteUser.invoke(null, "login_1");

        System.out.println("Finished cleaning");
    }

    @Test
    public void testIsLoginTaken_newLogin()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        //Constructor<?> repositoryConstructor = repositoryClass.getConstructor();
        Method isLoginTaken = repositoryClass.getMethod("isLoginTaken", String.class);
        //Object repositoryInstance = repositoryConstructor.newInstance();

        boolean result = (boolean) isLoginTaken.invoke(null, "new_login");

        assertFalse(result);
    }

    @Test
    public void testIsLoginTaken_usedLogin()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        //Constructor<?> repositoryConstructor = repositoryClass.getConstructor();
        Method isLoginTaken = repositoryClass.getMethod("isLoginTaken", String.class);
        //Object repositoryInstance = repositoryConstructor.newInstance();

        boolean result = (boolean) isLoginTaken.invoke(null, "login_1");

        assertTrue(result);
    }

    @Test
    public void testGetUserByLogin()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "NEW_USER");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
        Object userInstance = userConstructor.newInstance("login_1", "password_hash", newUserRole);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Method getUserByLogin = repositoryClass.getMethod("getUserByLogin", String.class);
        Object receivedUserInstance = getUserByLogin.invoke(null, "login_1");

        assertTrue(userInstance.equals(userClass.cast(receivedUserInstance)));
    }





}
