module identity.module {
    requires com.fasterxml.jackson.databind;
    requires jakarta.persistence;
    requires com.zaxxer.hikari;
    requires org.hibernate.orm.core;

    opens identity.module to identity.module.unit;
    opens identity.module.repository to identity.module.unit;
    opens identity.module.repository.entities to identity.module.unit, org.hibernate.orm.core;
    opens identity.module.enums to identity.module.unit;
    exports identity.module.models to com.fasterxml.jackson.databind;
    opens identity.module.utils.config to identity.module.unit;
    opens identity.module.utils to identity.module.unit;
    opens identity.module.annotations to identity.module.unit;
    exports identity.module.repository.DAOs to identity.module.unit;
}