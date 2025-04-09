package identity.module.repository;

import identity.module.repository.entities.User;
import identity.module.repository.persistence.CustomPersistenceUnitInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Repository {

    private EntityManager em;

    public Repository {
        Map<String,String> props = new HashMap<>();
        props.put("hibernate.show_sql", "true");
        EntityManagerFactory emf =  new HibernatePersistenceProvider().createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), props);
        em = emf.createEntityManager();
        emf.close();
    }

    public boolean isLoginTaken(String login){
        boolean isLoginTaken;
        em.getTransaction().begin();


        User result = em.find(User.class, login);
        //null -> no such login
        isLoginTaken = (result != null);

        em.getTransaction().commit();
        return isLoginTaken;
    }


}
