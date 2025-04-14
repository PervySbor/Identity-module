module identity.module.unit {
    requires junit;
    requires identity.module;
    requires org.junit.jupiter.api;
    requires java.sql;

    exports unit to junit;
}