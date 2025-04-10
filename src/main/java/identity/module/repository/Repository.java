package identity.module.repository;


import identity.module.exceptions.NonUniqueUserException;
import identity.module.repository.entities.User;
import identity.module.repository.utils.JpaUtils;
import identity.module.utils.LogManager;
import identity.module.utils.config.ConfigService;
import identity.module.repository.utils.CustomPersistenceUnitInfo;
import jakarta.persistence.*;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

//each thread has it's own repository
public class Repository {

    public boolean isLoginTaken(String login)
        throws NonUniqueUserException{
        boolean isLoginTaken = false;
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();

        em.getTransaction().begin();

        TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.login = :login", Long.class);
        query.setParameter("login", login);
        //User result = query.getSingleResult();
//        Optional<User> result;
//        try{
//            result = Optional.of(query.getSingleResult());
//        } catch (NoResultException e){
//            result = Optional.empty();
//        } catch (NonUniqueResultException e){
//            throw (NonUniqueUserException) new NonUniqueUserException("Found more than one user with login <" + login + ">").initCause(e);
//        }

        int result = query.getSingleResult().intValue();
        em.close();
        return switch(result) {
            case 0 -> false;
            case 1 -> true;
            default -> throw new NonUniqueUserException("Found more than one user with login <" + login + ">");
        };
    }

    public void registerNewUser(User user, UUID userId, String userIp){

    }
}
