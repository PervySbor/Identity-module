package identity.module.repository.DAOs;

import identity.module.interfaces.DAO;
import identity.module.repository.entities.User;
import identity.module.repository.utils.JpaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserDao implements DAO<User> {


    @Override
    public UUID save(User obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.persist(obj);
        UUID userId = obj.getUserId();
        em.getTransaction().commit();
        em.close();
        return userId;
    }

    @Override
    public void delete(User obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.remove(obj);
        em.close();
    }

    @Override
    public void update(User obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.merge(obj);
        em.close();
    }

    @Override
    public User find(Object privateKey) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        User result = em.find(User.class, privateKey);
        em.close();
        return result;
    }


//    public User findByLogin(String login) {
//        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
//        TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.login = :login", Long.class);
//        query.setParameter("login", login);
//        int result = query.getSingleResult().intValue();
//        em.close();
//        return result;
//    }


}
