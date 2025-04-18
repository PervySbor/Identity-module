package test.unit;

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
    private List<UUID> existingSessions = new ArrayList<>();

    public List<UUID> getExistingUsers(){
        return this.existingUsers;
    }

    public List<UUID> getExistingSessions(){
        return this.existingSessions;
    }


    @Before
    public void loadEntitiesForTest()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Timestamp validStart = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(validStart);
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Timestamp validEnd = new Timestamp(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Timestamp expiredStart = new Timestamp(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Timestamp expiredEnd = new Timestamp(cal.getTimeInMillis());
        cal.setTime(validStart);
        cal.add(Calendar.MINUTE, 10);
        Timestamp expiresSoonEnd = new Timestamp(cal.getTimeInMillis());


        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object subscribedUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "SUBSCRIBED");
        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "NEW_USER");


        Class<?> subscriptionTypesEnum = Class.forName("identity.module.enums.SubscriptionType");
        Object trialSubscriptionType = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, subscriptionTypesEnum, "TRIAL");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
        Object userInstance1 = userConstructor.newInstance("login_1", "V7XPqTkux3VORMqcuGOoLQ==", subscribedUserRole);
        Object userInstance2 = userConstructor.newInstance("login_2", "hashed_password", subscribedUserRole);
        Object userInstance3 = userConstructor.newInstance("login_3", "hashed_password", newUserRole);

        Class<?> subscriptionClass = Class.forName("identity.module.repository.entities.Subscription");
        Constructor<?> subscriptionConstructor = subscriptionClass.getConstructor(userClass, subscriptionTypesEnum);
        Constructor<?> fullSubscriptionConstructor = subscriptionClass.getConstructor(userClass, subscriptionTypesEnum, Timestamp.class, Timestamp.class);
        Object subscriptionInstance = subscriptionConstructor.newInstance(userInstance1,trialSubscriptionType);
        Object expiredSubscriptionInstance = fullSubscriptionConstructor.newInstance(userInstance2, trialSubscriptionType, expiredStart, expiredEnd);

        Class<?> sessionClass = Class.forName("identity.module.repository.entities.Session");
        Constructor<?> sessionConstructor = sessionClass.getConstructor(userClass, String.class, String.class, Timestamp.class, Timestamp.class);
        Object expiredSessionInstance = sessionConstructor.newInstance(userInstance2, "127.0.0.1", "hashed_refresh_token_1", expiredStart, expiredEnd);
        Object expiresSoonSessionInstance = sessionConstructor.newInstance(userInstance1, "127.0.0.1", "hashed_refresh_token_2", validStart, expiresSoonEnd);
        Object repeatedSessionInstance1 = sessionConstructor.newInstance(userInstance3, "127.0.0.1", "hashed_refresh_token_3", validStart, validEnd);
        Object repeatedSessionInstance2 = sessionConstructor.newInstance(userInstance3, "127.0.0.2", "hashed_refresh_token_4", validStart, validEnd);
        Object repeatedSessionInstance3 = sessionConstructor.newInstance(userInstance3, "127.0.0.3", "hashed_refresh_token_5", validStart, validEnd);
        Object repeatedSessionInstance4 = sessionConstructor.newInstance(userInstance3, "127.0.0.4", "hashed_refresh_token_6", validStart, validEnd);
        Object repeatedSessionInstance5 = sessionConstructor.newInstance(userInstance3, "127.0.0.5", "hashed_refresh_token_7", validStart, validEnd);


        Class<?> sessionDaoClass = Class.forName("identity.module.repository.DAOs.SessionDao");
        Constructor<?> sessionDaoConstructor = sessionDaoClass.getConstructor();
        Object sessionDaoInstance = sessionDaoConstructor.newInstance();
        Method saveSession = sessionDaoClass.getMethod("save",Object.class);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method saveUser = repositoryClass.getMethod("saveUser", userClass);
        Method saveSubscription = repositoryClass.getMethod("saveSubscription", subscriptionClass);

        this.existingUsers.add((UUID) saveUser.invoke(repositoryInstance, userInstance1));
        saveSubscription.invoke(repositoryInstance, subscriptionInstance);
        this.existingSessions.add((UUID) saveSession.invoke(sessionDaoInstance, expiresSoonSessionInstance));
        this.existingUsers.add((UUID) saveUser.invoke(repositoryInstance, userInstance2));
        saveSubscription.invoke(repositoryInstance, expiredSubscriptionInstance);
        this.existingSessions.add((UUID) saveSession.invoke(sessionDaoInstance, expiredSessionInstance)); //not tested yet
        this.existingUsers.add((UUID) saveUser.invoke(repositoryInstance, userInstance3));
        this.existingSessions.add((UUID) saveSession.invoke(sessionDaoInstance, repeatedSessionInstance1));
        this.existingSessions.add((UUID) saveSession.invoke(sessionDaoInstance, repeatedSessionInstance2));
        this.existingSessions.add((UUID) saveSession.invoke(sessionDaoInstance, repeatedSessionInstance3));
        this.existingSessions.add((UUID) saveSession.invoke(sessionDaoInstance, repeatedSessionInstance4));
        this.existingSessions.add((UUID) saveSession.invoke(sessionDaoInstance, repeatedSessionInstance5));

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
//        Class<?> sessionDaoClass = Class.forName("identity.module.repository.DAOs.SessionDao");
//        Constructor<?> sessionDaoConstructor = sessionDaoClass.getConstructor();
//        Object sessionDaoInstance = sessionDaoConstructor.newInstance();
//        Method deleteSession = sessionDaoClass.getMethod("delete",Object.class);
//        Method findSession = sessionDaoClass.getMethod("find", Object.class);
//
//        List<Object> userInstances = new ArrayList<>();
//
//        for(UUID sessionId : this.existingSessions){
//            Object sessionInstance = findSession.invoke(sessionDaoInstance, sessionId);
//            deleteSession.invoke(sessionDaoInstance, sessionInstance);
//        }
//
//        for(UUID userId : this.existingUsers){
//            userInstances.add(findUser.invoke(userDaoInstance, userId)); //found all previously created users
//        }
//
//        for(Object userInst : userInstances){
//            Object subscriptionInstance = findSubscription.invoke(subscriptionDaoInstance, userInst); //found all previously created Subscriptions (with Users as keys)
//            if (subscriptionInstance != null) {
//                deleteSubscription.invoke(subscriptionDaoInstance, subscriptionInstance);
//            }
//            deleteUser.invoke(userDaoInstance, userInst);
//        }
//

        Method execUDQuery = Class.forName("identity.module.interfaces.DAO")
                .getMethod("executeUDQuery", String.class, Map.class);
        Map<String, String> props = new HashMap<>();
        execUDQuery.invoke(null, "DELETE FROM Session", props);
        execUDQuery.invoke(null, "DELETE FROM Subscription", props);
        execUDQuery.invoke(null, "DELETE FROM User", props);


        System.out.println("Finished cleaning");
    }

    @Test
    public void testGetRelevantSession_ifExpired() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> sessionDaoClass = Class.forName("identity.module.repository.DAOs.SessionDao");
        Constructor<?> sessionDaoConstructor = sessionDaoClass.getConstructor();
        Object sessionDaoInstance = sessionDaoConstructor.newInstance();
        Method findSession = sessionDaoClass.getMethod("find", Object.class);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method getSession = repositoryClass.getMethod("getRelevantSession", String.class);


        Object foundSession2 = getSession.invoke(repositoryInstance, "hashed_refresh_token_1");
        Object foundSession1 = findSession.invoke(sessionDaoInstance, this.existingSessions.get(1));

        assertNull(foundSession1);
        assertNull(foundSession2);

    }

    @Test
    public void testGetRelevantSession_ifRelevant() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> sessionDaoClass = Class.forName("identity.module.repository.DAOs.SessionDao");
        Constructor<?> sessionDaoConstructor = sessionDaoClass.getConstructor();
        Object sessionDaoInstance = sessionDaoConstructor.newInstance();
        Method findSession = sessionDaoClass.getMethod("find", Object.class);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method getSession = repositoryClass.getMethod("getRelevantSession", String.class);

        Object foundSession1 = findSession.invoke(sessionDaoInstance, this.existingSessions.get(0));
        Object foundSession2 = getSession.invoke(repositoryInstance, "hashed_refresh_token_2");

        assertEquals(foundSession1, foundSession2);

    }

    @Test
    public void testSaveSession_ifAlreadyAchievedMaxAmount()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Timestamp validStart = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(validStart);
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Timestamp validEnd = new Timestamp(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_MONTH, -5);

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");

        Class<?> userDaoClass = Class.forName("identity.module.repository.DAOs.UserDao");
        Constructor<?> userDaoConstructor = userDaoClass.getConstructor();
        Object userDaoInstance = userDaoConstructor.newInstance();
        Method findUser = userDaoClass.getMethod("find", Object.class);

        Class<?> sessionClass = Class.forName("identity.module.repository.entities.Session");
        Constructor<?> sessionConstructor = sessionClass.getConstructor(userClass, String.class, String.class, Timestamp.class, Timestamp.class);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method saveSession = repositoryClass.getMethod("saveSession", sessionClass, int.class);

        Class<?> sessionDaoClass = Class.forName("identity.module.repository.DAOs.SessionDao");
        Constructor<?> sessionDaoConstructor = sessionDaoClass.getConstructor();
        Object sessionDaoInstance = sessionDaoConstructor.newInstance();
        Method findSession = sessionDaoClass.getMethod("find", Object.class);

        Object foundUserInstance = findUser.invoke(userDaoInstance, this.existingUsers.get(2));
        Object addedSessionInstance = sessionConstructor.newInstance(foundUserInstance, "127.0.0.1", "hashed_refresh_token_X", validStart, validEnd);


        UUID savedSessionId = (UUID) saveSession.invoke(repositoryInstance, addedSessionInstance, 5);

        Object savedSessionInstance = findSession.invoke(sessionDaoInstance, savedSessionId);

        assertEquals(savedSessionInstance, addedSessionInstance);
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
    public void testIsLoginTaken_ifNewLogin()
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
    public void testIsLoginTaken_ifUsedLogin()
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
    public void testGetUserByLogin_ifUserExists()
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
    public void testGetUserByLogin_ifNoneExists()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method getUserByLogin = repositoryClass.getMethod("getUserByLogin", String.class);
        Object receivedUserInstance = getUserByLogin.invoke(repositoryInstance, "not_existing_login");

        assertNull(receivedUserInstance);
    }

    @Test(expected=Exception.class)
    public void testGetUserByLogin_ifDuplicateExists()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {


        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
                .invoke(null, rolesEnum, "NEW_USER");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
        Object userInstance = userConstructor.newInstance("login_1", "duplicate_user_password", newUserRole);

        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");
        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method getUserByLogin = repositoryClass.getMethod("getUserByLogin", String.class);
        Method saveUser = repositoryClass.getMethod("saveUser", userClass);

        this.existingUsers.add((UUID) saveUser.invoke(repositoryInstance, userInstance));


        Object receivedUserInstance = getUserByLogin.invoke(repositoryInstance, "login_1");
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


    @Test
    public void testGetRelevantSubscription_ifNoneExists() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> repositoryClass = Class.forName("identity.module.repository.Repository");

//        Class<?> rolesEnum = Class.forName("identity.module.enums.Roles");
//        Object newUserRole = Enum.class.getMethod("valueOf", Class.class, String.class)
//                .invoke(null, rolesEnum, "NEW_USER");

        Class<?> userClass = Class.forName("identity.module.repository.entities.User");
//        Constructor<?> userConstructor = userClass.getConstructor(String.class, String.class, rolesEnum);
//        Object expectedUserInstance = userConstructor.newInstance("login_3", "hashed_password", newUserRole);

        Object repositoryInstance = repositoryClass.getConstructor().newInstance();
        Method getRelevantSubscriptions = repositoryClass.getMethod("getRelevantSubscription", userClass);

        Method getUserByLogin = repositoryClass.getMethod("getUserByLogin", String.class);
        Object receivedUserInstance = getUserByLogin.invoke(repositoryInstance, "login_3");

        Object receivedSubscription = getRelevantSubscriptions.invoke(repositoryInstance, receivedUserInstance);

        assertNull(receivedSubscription); //as no subscription exist, should receive null
    }
}
