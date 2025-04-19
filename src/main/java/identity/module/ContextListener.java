package identity.module;

import identity.module.repository.utils.MyHikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent ev){
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev){
        MyHikariDataSource.destroy(); //destroying HikariCp to prevent memory leakage
    }
}
