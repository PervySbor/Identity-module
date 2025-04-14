package identity.module.repository;


import identity.module.annotations.Temporary;
import identity.module.annotations.Tested;
import identity.module.enums.Roles;
import identity.module.exceptions.NonUniqueSubscriptionException;
import identity.module.exceptions.NonUniqueUserException;
import identity.module.exceptions.UserNotFoundException;
import identity.module.interfaces.DAO;
import identity.module.repository.DAOs.SessionDao;
import identity.module.repository.DAOs.SubscriptionDao;
import identity.module.repository.DAOs.UserDao;
import identity.module.repository.entities.Session;
import identity.module.repository.entities.Subscription;
import identity.module.repository.entities.User;
import identity.module.repository.utils.JpaUtils;
import identity.module.utils.LogManager;
import identity.module.utils.config.ConfigService;
import identity.module.repository.utils.CustomPersistenceUnitInfo;
import jakarta.persistence.*;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.sql.Timestamp;
import java.util.*;

//each thread has it's own repository
public class Repository {

    private final SubscriptionDao subscriptionDao = new SubscriptionDao();
    private final SessionDao sessionDao = new SessionDao();
    private final UserDao userDao = new UserDao();

    @Tested
    public boolean isLoginTaken(String login)
        throws NonUniqueUserException{
        boolean isLoginTaken = false;
        Map<String, Object> properties = new HashMap<>();
        properties.put("login", login);
        List<Long> results = DAO.executeQuery("SELECT COUNT(u) FROM User u WHERE u.login = :login", properties, Long.class);
        int result = results.getFirst().intValue();
        return switch(result) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new NonUniqueUserException("Found more than one user with login <" + login + ">");
        };
    }

    @Tested
    public User getUserByLogin(String login) throws NonUniqueUserException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("login", login);
        List<User> results = DAO.executeQuery("SELECT u FROM User u WHERE u.login = :login", properties, User.class);
        if (results.isEmpty()){
            return null;
        }
        else if (results.size() > 1){
            throw new NonUniqueUserException("Found more than one user with login <" + login + ">");
        }
        return results.getFirst();
    }

    @Tested
    public Subscription getRelevantSubscription(User user) throws UserNotFoundException, NonUniqueUserException {
        Subscription subscription;
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("user", user);
        subscription = this.subscriptionDao.find(user);
//        List<Subscription> results = DAO.executeQuery("SELECT s FROM Subscription s WHERE s.user = :user", properties, Subscription.class);
//        if (results.isEmpty()){
//            return null;
//        }
//        else if (results.size() > 1){
//            throw new NonUniqueSubscriptionException("Found more than one subscription for user with id: <" + user.getUserId() + ">");
//        }
//        subscription = results.getFirst();
        if (subscription != null) {
            Timestamp expiresAt = subscription.getExpireAt();
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            if (currentTimestamp.compareTo(expiresAt) > 0) {//subscription is expired
                subscriptionDao.delete(subscription);
                Map<String, Object> properties = new HashMap<>();
                properties.put("userId", user.getUserId());
                properties.put("role", Roles.OUT_TRIAL_USER);
                int linesAffected = DAO.executeUDQuery("UPDATE User SET role=:role WHERE userId=:userId", properties);
                if (linesAffected == 0) {
                    throw new UserNotFoundException("failed to find user with login = <" + user.getLogin() + ">");
                } else if (linesAffected > 1) {
                    throw new NonUniqueUserException("found more than one user with id <" + user.getUserId() + ">");
                }
                return null;
            }
        }
        return subscription;
    }

    @Tested
    public Session getRelevantSession(String hashedRefreshToken){
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> properties = new HashMap<>();
        properties.put("hashedRefreshToken", hashedRefreshToken);
        List<Session> results = DAO.executeQuery("SELECT s FROM Session s WHERE s.refreshTokenHash = :hashedRefreshToken", properties, Session.class);
        if (results.isEmpty()){
            return null;
        }
        if (currentTimestamp.compareTo(results.getFirst().getExpiresAt()) > 0){ //session expired
            sessionDao.delete(results.getFirst());
            return null;
        }
        return results.getFirst();
    }


    public UUID saveSession(Session session,int max_sessions_amount){
        Map<String, Object> properties = new HashMap<>();
        properties.put("user", session.getUser());
        List<Session> results = DAO.executeQuery("SELECT s FROM Session s WHERE s.user = :user", properties, Session.class);
        if (results.size() > max_sessions_amount - 1){
            while(results.size() > max_sessions_amount - 1) {
                Session earliestCreatedSession = results.getFirst();
                Timestamp earliestCreationTime = earliestCreatedSession.getCreatedAt();
                for (Session s : results) {
                    Timestamp sCreationTime = s.getCreatedAt();
                    if(earliestCreationTime.compareTo(sCreationTime) > 0){
                        earliestCreatedSession = s;
                    }
                }
                results.remove(earliestCreatedSession);
                sessionDao.delete(earliestCreatedSession);
            }
        }

        return sessionDao.save(session);
    }

    @Tested
    public UUID saveUser(User user){
        return new UserDao().save(user);
    }


    public Session findSession(UUID sessionId){
        return sessionDao.find(sessionId);
    }

    public boolean hasSubscription(User user){
        Subscription subscription = subscriptionDao.find(user);
        return subscription != null;
    }

    @Tested //Only saves new subscriptions
    public void saveSubscription(Subscription subscription){
        subscriptionDao.save(subscription);
    }

}
