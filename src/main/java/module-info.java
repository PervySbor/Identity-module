module identity.module {
    requires com.fasterxml.jackson.databind;
    requires jakarta.persistence;
    requires java.sql;

    opens identity.module to identity.module.test;
    exports identity.module.models to com.fasterxml.jackson.databind;
}