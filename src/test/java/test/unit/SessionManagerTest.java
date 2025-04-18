package test.unit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class SessionManagerTest {

    private RepositoryTest repositoryTest = new RepositoryTest();

    @Before
    public void loadEntitiesForTheTest() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        repositoryTest.loadEntitiesForTest();
    }

    @After
    public void deleteEntitiesAfterTest() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        repositoryTest.deleteEntitiesAfterTest();
    }

    @Ignore
    @Test
    public void testCreateJWT() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException {

        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object subscribedUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "SUBSCRIBED");
        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "NEW_USER");

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();

        Class<?> sessionManagerClass = Class.forName("identity.module.SessionManager");
        Object sessionManagerInstance = sessionManagerClass.getDeclaredConstructor(repositoryClass)
                .newInstance(repositoryInstance);
        Method createJWT = sessionManagerClass.getDeclaredMethod("createJWT", rolesEnum, UUID.class);

        createJWT.invoke(sessionManagerInstance, newUserRole, repositoryTest.getExistingSessions().get(0));


    }
}
