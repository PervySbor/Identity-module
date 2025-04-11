package identity.module.repository.DAOs;

import identity.module.interfaces.DAO;
import identity.module.repository.entities.Session;
import identity.module.repository.entities.User;
import identity.module.repository.utils.JpaUtils;
import jakarta.persistence.EntityManager;

import java.util.UUID;

public class SessionDao implements DAO<Session> {
    @Override
    public UUID save(Session obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.persist(obj);
        UUID sessionId = obj.getSessionId();
        em.close();
        return sessionId;
    }

    @Override
    public void delete(Session obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.remove(obj);
        em.close();
    }

    @Override
    public void update(Session obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.merge(obj);
        em.close();
    }

    @Override
    public Session find(Object privateKey) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        Session result = em.find(Session.class, privateKey);
        em.close();
        return result;
    }
}
