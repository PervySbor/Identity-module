package identity.module.repository;


import identity.module.exceptions.NonUniqueSubscriptionException;
import identity.module.exceptions.NonUniqueUserException;
import identity.module.interfaces.DAO;
import identity.module.repository.DAOs.SessionDao;
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
//upd. no threads => static everything
public class Repository {

    public static boolean isLoginTaken(String login)
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

    public static User getUserByLogin(String login) throws NonUniqueUserException {
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

    public static Subscription getSubscriptionByUserId(UUID userId) throws NonUniqueSubscriptionException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("userId", userId);
        List<Subscription> results = DAO.executeQuery("SELECT s FROM Subscription s WHERE s.userId = :userId", properties, Subscription.class);
        if (results.isEmpty()){
            return null;
        }
        else if (results.size() > 1){
            throw new NonUniqueSubscriptionException("Found more than one subscription for user with id: <" + userId + ">");
        }
        return results.getFirst();
    }

    public static Session getSessionByHashedRefreshToken(String hashedRefreshToken){
        Map<String, Object> properties = new HashMap<>();
        properties.put("hashedRefreshToken", hashedRefreshToken);
        List<Session> results = DAO.executeQuery("SELECT s FROM Session s WHERE s.refreshTokenHash = :hashedRefreshToken", properties, Session.class);
        if (results.isEmpty()){
            return null;
        }
        return results.getFirst();
    }

    public static UUID saveSession(Session session,int max_sessions_amount){
        SessionDao sDao = new SessionDao();
        Map<String, Object> properties = new HashMap<>();
        properties.put("userId", session.getUserId());
        List<Session> results = DAO.executeQuery("SELECT s FROM Session s WHERE s.userId = :userId", properties, Session.class);
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
                sDao.delete(earliestCreatedSession);
            }
        }

        return sDao.save(session);
    }

    public static UUID saveUser(User user){
        return new UserDao().save(user);
    }

    public static int deleteUser(String login){
        Map<String, Object> properties = new HashMap<>();
        properties.put("login", login);
        return DAO.executeUDQuery("DELETE FROM User u WHERE u.login=:login", properties);

    }


}
