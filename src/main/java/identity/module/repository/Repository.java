package identity.module.repository;


import identity.module.config.ConfigService;
import identity.module.repository.entities.User;
import identity.module.repository.persistence.CustomPersistenceUnitInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;

import identity.module.repository.entities.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Repository {

    private EntityManager em;

    public Repository(ConfigService configService) {
        Map<String,String> props = new HashMap<>();
        props.put("hibernate.show_sql", "true");
        EntityManagerFactory emf =  new HibernatePersistenceProvider().createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(configService), props);
        em = emf.createEntityManager();
        //emf.close();
    }

    public boolean isLoginTaken(String login){
        boolean isLoginTaken = false;
        em.getTransaction().begin();


        //User result = em.find(User.class, );
        //null -> no such login
        //isLoginTaken = (result != null);

        em.getTransaction().commit();
        return isLoginTaken;
    }


}
