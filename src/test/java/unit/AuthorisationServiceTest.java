package unit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class AuthorisationServiceTest {

    private RepositoryTest repositoryTest = new RepositoryTest();
    private List<UUID> existingSessions = new ArrayList<>();
    private String refreshToken;

    @Before
    public void loadEntitiesForTest()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
//        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
//                .invoke(null, rolesEnum, "NEW_USER");
//
//        Class<?> subscriptionTypesEnum = Class.forName("identity.module.enums.SubscriptionType");
//        Object newSubscriptionType = Enum.class.getMethod("valueOf", Class.class, String.class)
//                .invoke(null, subscriptionTypesEnum, "TRIAL");
//
//        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
//        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
//        Object userInstance = userConstructor.newInstance("login_1", "V7XPqTkux3VORMqcuGOoLQ==", newUserRole);
//
//        Class<?> subscriptionClass = Class.forName("identity.module.repository.entities.Subscription");
//        Constructor<?> subscriptionConstructor = subscriptionClass.getConstructor(userClass, subscriptionTypesEnum);
//        Object subscriptionInstance = subscriptionConstructor.newInstance(userInstance,newSubscriptionType);
//
//        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
//        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
//        Method saveUser = repositoryClass.getMethod("saveUser", userClass);
//        Method saveSubscription = repositoryClass.getMethod("saveSubscription", subscriptionClass);
//
//        this.existingUsers.add((UUID) saveUser.invoke(repositoryInstance, userInstance));
//        saveSubscription.invoke(repositoryInstance, subscriptionInstance);
        this.repositoryTest.loadEntitiesForTest();
        existingSessions = repositoryTest.getExistingSessions();

        System.out.println("Finished preparations");

    }

    //WARNING!!! uses getUserByLogin() itself
    @After
    public void deleteEntitiesAfterTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        System.out.println("Started cleaning");
//        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
//
//        Class<?> userDaoClass = Class.forName("identity.module.repository.DAOs.UserDao");
//        Constructor<?> userDaoConstructor = userDaoClass.getConstructor();
//        Object userDaoInstance = userDaoConstructor.newInstance();
//        Method deleteUser = userDaoClass.getMethod("delete", Object.class);
//        Method findUser = userDaoClass.getMethod("find", Object.class);
//
//        Class<?> subscriptionDaoClass = Class.forName("identity.module.repository.DAOs.SubscriptionDao");
//        Constructor<?> subscriptionDaoConstructor = subscriptionDaoClass.getConstructor();
//        Object subscriptionDaoInstance = subscriptionDaoConstructor.newInstance();
//        Method deleteSubscription = subscriptionDaoClass.getMethod("delete", Object.class);
//        Method findSubscription = subscriptionDaoClass.getMethod("find", Object.class);
//
//        List<Object> userInstances = new ArrayList<>();
//
//        for(UUID userId : this.existingUsers){
//            userInstances.add(findUser.invoke(userDaoInstance, userId)); //found all previously created users
//        }
//
//        for(Object userInst : userInstances){
//            Object subscriptionInstance = findSubscription.invoke(subscriptionDaoInstance, userInst); //found all previously created Subscriptions (with Users as keys)
//            if (subscriptionInstance != null) {
//                deleteSubscription.invoke(subscriptionDaoInstance, subscriptionInstance);
//                deleteUser.invoke(userDaoInstance, userInst);
//            }
//        }
        repositoryTest.deleteEntitiesAfterTest();

        System.out.println("Finished cleaning");
    }

    @Test
    public void testLogin() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String receivedJson = "{ \"login\": \"login_1\", \"password\": \"V7XPqTkux3VORMqcuGOoLQ==\", \"user_ip\":  \"127.0.0.1\"}";
        Class<?> authServiceClass = Class.forName("identity.module.AuthorisationService");
        Object authServiceInstance = authServiceClass.getConstructor().newInstance();
        Method login = authServiceClass.getDeclaredMethod("login", String.class);
        login.setAccessible(true);

        System.out.println(login.invoke(authServiceInstance, receivedJson));

    }

    @Test
    public void testRegister_ifLoginAlreadyTaken() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String receivedJson = "{ \"login\": \"login_1\", \"password\": \"V7XPqTkux3VORMqcuGOoLQ==\", \"user_ip\":  \"127.0.0.1\"}";

        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "NEW_USER");

        Class<?> authServiceClass = Class.forName("identity.module.AuthorisationService");
        Object authServiceInstance = authServiceClass.getConstructor().newInstance();
        Method register = authServiceClass.getDeclaredMethod("register", String.class, rolesEnum);
        register.setAccessible(true);

        System.out.println(register.invoke(authServiceInstance, receivedJson, newUserRole));

    }

    @Test
    public void testRegister_ifValidLogin() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String receivedJson = "{ \"login\": \"valid_login\", \"password\": \"V7XPqTkux3VORMqcuGOoLQ==\", \"user_ip\":  \"127.0.0.1\"}";

        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "NEW_USER");

        Class<?> authServiceClass = Class.forName("identity.module.AuthorisationService");
        Object authServiceInstance = authServiceClass.getConstructor().newInstance();
        Method register = authServiceClass.getDeclaredMethod("register", String.class, rolesEnum);
        register.setAccessible(true);

        Properties result = (Properties) register.invoke(authServiceInstance, receivedJson, newUserRole);
        System.out.println(result);
        this.refreshToken = result.getProperty("refresh_token");
    }

    @Test
    public void testRefresh() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        testRegister_ifValidLogin();
        String receivedJson = "{\"refresh_token\":\""+ this.refreshToken +"\"}";

        Class<?> authServiceClass = Class.forName("identity.module.AuthorisationService");
        Object authServiceInstance = authServiceClass.getConstructor().newInstance();
        Method refreshMethod = authServiceClass.getDeclaredMethod("refresh", String.class);
        refreshMethod.setAccessible(true);


        Properties result = (Properties) refreshMethod.invoke(authServiceInstance, receivedJson);
        System.out.println(result);

    }

    @Test
    public void testCreateSubscription() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String receivedJson = "{\"session_id\":\""+ this.existingSessions.get(2)+"\",\"subscription_type\":\"TRIAL\"}";
        System.out.println(receivedJson);

        Class<?> authServiceClass = Class.forName("identity.module.AuthorisationService");
        Object authServiceInstance = authServiceClass.getConstructor().newInstance();
        Method createSubscription = authServiceClass.getDeclaredMethod("createSubscription", String.class);
        createSubscription.setAccessible(true);

        Properties result = (Properties) createSubscription.invoke(authServiceInstance, receivedJson);
        System.out.println(result);

    }
}
