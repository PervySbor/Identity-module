package identity.module.repository.DAOs;

import identity.module.interfaces.DAO;
import identity.module.repository.entities.Session;
import identity.module.repository.entities.Subscription;
import identity.module.repository.entities.User;
import identity.module.repository.utils.JpaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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
        em.getTransaction().begin();
        Subscription managedObj = em.merge(obj);
        em.remove(managedObj);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void update(Subscription obj) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        em.merge(obj);
        em.close();
    }

    //UNKNOWN ERROR
    /*
    * Supplied id had wrong type: entity 'identity.module.repository.entities.Subscription'
    * has id type 'class identity.module.repository.entities.Subscription'
    * but supplied id was of type 'class identity.module.repository.entities.User'
     * */
    @Override
    public Subscription find(Object primaryKey) {
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();

        em.getTransaction().begin();
        em.merge(primaryKey);
        //Subscription result = em.find(Subscription.class, primaryKey);
        TypedQuery<Subscription> query = em.createQuery("SELECT s FROM Subscription s WHERE s.user=:user", Subscription.class);
        query.setParameter("user",(User) primaryKey);
        Subscription result = query.getSingleResult();

        em.getTransaction().commit();
        em.close();
        return result;
    }
}
