module identity.module {
    requires com.fasterxml.jackson.databind;
    requires jakarta.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;

    opens identity.module to identity.module.test;
    opens identity.module.repository to identity.module.test;
    opens identity.module.repository.entities to identity.module.test;
    opens identity.module.enums to identity.module.test;
    exports identity.module.models to com.fasterxml.jackson.databind;
}