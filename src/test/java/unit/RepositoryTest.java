package unit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.Assert.*;


public class RepositoryTest {

    //stores userId's
    private List<UUID> existingUsers = new ArrayList<>();

    @Before
    public void loadEntitiesForTest()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(System.currentTimeMillis()));
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Timestamp expiredStart = new Timestamp(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Timestamp expiredEnd = new Timestamp(cal.getTimeInMillis());

        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object subscribedUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "SUBSCRIBED");

        Class<?> subscriptionTypesEnum = Class.forName("identity.module.enums.SubscriptionType");
        Object trialSubscriptionType = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, subscriptionTypesEnum, "TRIAL");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
        Object userInstance1 = userConstructor.newInstance("login_1", "V7XPqTkux3VORMqcuGOoLQ==", subscribedUserRole);
        Object userInstance2 = userConstructor.newInstance("login_2", "hashed_password", subscribedUserRole);

        Class<?> subscriptionClass = Class.forName("identity.module.repository.entities.Subscription");
        Constructor<?> subscriptionConstructor = subscriptionClass.getConstructor(userClass, subscriptionTypesEnum);
        Constructor<?> fullSubscriptionConstructor = subscriptionClass.getConstructor(userClass, subscriptionTypesEnum, Timestamp.class, Timestamp.class);
        Object subscriptionInstance = subscriptionConstructor.newInstance(userInstance1,trialSubscriptionType);
        Object expiredSubscriptionInstance = fullSubscriptionConstructor.newInstance(userInstance2, trialSubscriptionType, expiredStart, expiredEnd);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method saveUser = repositoryClass.getMethod("saveUser", userClass);
        Method saveSubscription = repositoryClass.getMethod("saveSubscription", subscriptionClass);

        this.existingUsers.add((UUID) saveUser.invoke(repositoryInstance, userInstance1));
        saveSubscription.invoke(repositoryInstance, subscriptionInstance);
        this.existingUsers.add((UUID) saveUser.invoke(repositoryInstance, userInstance2));
        saveSubscription.invoke(repositoryInstance, expiredSubscriptionInstance);

        System.out.println("Finished preparations");

    }

    //WARNING!!! uses getUserByLogin() itself
    @After
    public void deleteEntitiesAfterTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        System.out.println("Started cleaning");
        Class<?> userClass = Class.forName("identity.module.repository.entities.User");

        Class<?> userDaoClass = Class.forName("identity.module.repository.DAOs.UserDao");
        Constructor<?> userDaoConstructor = userDaoClass.getConstructor();
        Object userDaoInstance = userDaoConstructor.newInstance();
        Method deleteUser = userDaoClass.getMethod("delete", Object.class);
        Method findUser = userDaoClass.getMethod("find", Object.class);

        Class<?> subscriptionDaoClass = Class.forName("identity.module.repository.DAOs.SubscriptionDao");
        Constructor<?> subscriptionDaoConstructor = subscriptionDaoClass.getConstructor();
        Object subscriptionDaoInstance = subscriptionDaoConstructor.newInstance();
        Method deleteSubscription = subscriptionDaoClass.getMethod("delete", Object.class);
        Method findSubscription = subscriptionDaoClass.getMethod("find", Object.class);

        List<Object> userInstances = new ArrayList<>();

        for(UUID userId : this.existingUsers){
            userInstances.add(findUser.invoke(userDaoInstance, userId)); //found all previously created users
        }

        for(Object userInst : userInstances){
            Object subscriptionInstance = findSubscription.invoke(subscriptionDaoInstance, userInst); //found all previously created Subscriptions (with Users as keys)
            if (subscriptionInstance != null) {
                deleteSubscription.invoke(subscriptionDaoInstance, subscriptionInstance);
            }
            deleteUser.invoke(userDaoInstance, userInst);
        }

        System.out.println("Finished cleaning");
    }

    @Test
    public void testSaveSubscription() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "NEW_USER");

        Class<?> subscriptionTypesEnum = Class.forName("identity.module.enums.SubscriptionType");
        Object newSubscriptionType = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, subscriptionTypesEnum, "TRIAL");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
        Object userInstance = userConstructor.newInstance("some_login", "V7XPqTkux3VORMqcuGOoLQ==", newUserRole);

        Class<?> subscriptionClass = Class.forName("identity.module.repository.entities.Subscription");
        Constructor<?> subscriptionConstructor = subscriptionClass.getConstructor(userClass, subscriptionTypesEnum);
        Object subscriptionInstance = subscriptionConstructor.newInstance(userInstance,newSubscriptionType);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method saveUser = repositoryClass.getMethod("saveUser", userClass);
        Method saveSubscription = repositoryClass.getMethod("saveSubscription", subscriptionClass);

        this.existingUsers.add((UUID) saveUser.invoke(repositoryInstance, userInstance));
        saveSubscription.invoke(repositoryInstance,subscriptionInstance);

    }

    @Test
    public void testIsLoginTaken_newLogin()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method isLoginTaken = repositoryClass.getMethod("isLoginTaken", String.class);
        //Object repositoryInstance = repositoryConstructor.newInstance();

        boolean result = (boolean) isLoginTaken.invoke(repositoryInstance, "new_login");

        assertFalse(result);
    }

    @Test
    public void testIsLoginTaken_usedLogin()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        //Constructor<?> repositoryConstructor = repositoryClass.getConstructor();
        Method isLoginTaken = repositoryClass.getMethod("isLoginTaken", String.class);
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();

        boolean result = (boolean) isLoginTaken.invoke(repositoryInstance, "login_1");

        assertTrue(result);
    }

    @Test
    public void testGetUserByLogin()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object subscribedUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "SUBSCRIBED");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
        Object userInstance = userConstructor.newInstance("login_1", "V7XPqTkux3VORMqcuGOoLQ==", subscribedUserRole);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method getUserByLogin = repositoryClass.getMethod("getUserByLogin", String.class);
        Object receivedUserInstance = getUserByLogin.invoke(repositoryInstance, "login_1");

        assertTrue(userInstance.equals(userClass.cast(receivedUserInstance)));
    }

    @Test
    public void testGetRelevantSubscription_ifRelevant() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");

        Class<?> subscriptionTypesEnum = Class.forName("identity.module.enums.SubscriptionType");
        Object newSubscriptionType = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, subscriptionTypesEnum, "TRIAL");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");

        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method getRelevantSubscriptions = repositoryClass.getMethod("getRelevantSubscription", userClass);

        Method getUserByLogin = repositoryClass.getMethod("getUserByLogin", String.class);
        Object receivedUserInstance = getUserByLogin.invoke(repositoryInstance, "login_1");


        Class<?> subscriptionClass = Class.forName("identity.module.repository.entities.Subscription");
        Constructor<?> subscriptionConstructor = subscriptionClass.getConstructor(userClass, subscriptionTypesEnum);
        Object subscriptionInstance = subscriptionConstructor.newInstance(receivedUserInstance,newSubscriptionType);

        Object receivedSubscription = getRelevantSubscriptions.invoke(repositoryInstance, receivedUserInstance);

        System.out.println("invoked");

        System.out.println(subscriptionInstance);
        System.out.println(receivedSubscription);

        assertEquals(subscriptionInstance,receivedSubscription);

        System.out.println("asserted");

    }

    @Test
    public void testGetRelevantSubscription_ifExpired() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");

        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object outTrialUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "OUT_TRIAL_USER");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
        Object expectedUserInstance = userConstructor.newInstance("login_2", "hashed_password", outTrialUserRole);

        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method getRelevantSubscriptions = repositoryClass.getMethod("getRelevantSubscription", userClass);

        Method getUserByLogin = repositoryClass.getMethod("getUserByLogin", String.class);
        Object receivedUserInstance = getUserByLogin.invoke(repositoryInstance, "login_2");


        Object receivedSubscription = getRelevantSubscriptions.invoke(repositoryInstance, receivedUserInstance);
        Object updatedUserInstance = getUserByLogin.invoke(repositoryInstance, "login_2");

        assertNull(receivedSubscription); //as subscription is expired, should receive null
        assertEquals(expectedUserInstance, updatedUserInstance);
    }



}
