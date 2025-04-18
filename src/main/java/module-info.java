module identity.module {
    requires com.fasterxml.jackson.databind;
    requires com.zaxxer.hikari;
    requires org.hibernate.orm.core;
    //requires jakarta.servlet;
    requires jakarta.persistence;
    requires java.net.http;
    requires org.apache.tomcat.embed.core;

    opens identity.module to identity.module.test;
    opens identity.module.repository to identity.module.test;
    opens identity.module.repository.entities to identity.module.test, org.hibernate.orm.core;
    opens identity.module.enums to identity.module.test;
    exports identity.module.models to com.fasterxml.jackson.databind;
    opens identity.module.utils.config to identity.module.test;
    opens identity.module.utils to identity.module.test;
    opens identity.module.annotations to identity.module.test;
    opens identity.module.interfaces to identity.module.test;
    exports identity.module.repository.DAOs to identity.module.test;
    exports identity.module to identity.module.test;
}