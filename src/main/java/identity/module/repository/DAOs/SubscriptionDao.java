package identity.module.repository.DAOs;

import identity.module.interfaces.DAO;
import identity.module.repository.entities.Session;
import identity.module.repository.entities.Subscription;
import identity.module.repository.utils.JpaUtils;
import jakarta.persistence.EntityManager;

import java.util.UUID;

public class SubscriptionDao implements DAO<Subscription> {
    @Override
    public UUID save(Subscription obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.persist(obj);
        UUID userId = obj.getUser().getUserId();
        em.getTransaction().commit();
        em.close();
        return userId;
    }

    @Override
    public void delete(Subscription obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.remove(obj);
        em.close();
    }

    @Override
    public void update(Subscription obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.merge(obj);
        em.close();
    }

    @Override
    public Subscription find(Object privateKey) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        Subscription result = em.find(Subscription.class, privateKey);
        em.close();
        return result;
    }
}
