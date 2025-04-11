package identity.module.interfaces;

import identity.module.repository.utils.JpaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DAO<T> {

    void save(T obj);

    void delete(T obj);

    void update(T obj);

    T find(Object privateKey);

    static <X> List<X> executeQuery(String jsql, Map<String, Object> args, Class<X> Xclass){
        EntityManager em = JpaUtils.getEntityManagerFactory().createEntityManager();
        TypedQuery<X> query= em.createQuery(jsql, Xclass);
        Set<Map.Entry<String, Object>> pairs = args.entrySet();
        for(Map.Entry<String, Object> pair : pairs){
            query.setParameter(pair.getKey(), pair.getValue());
        }
        List<X> result = query.getResultList();
        em.close();
        return result;
    }
}
