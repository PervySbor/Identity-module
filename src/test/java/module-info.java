module identity.module.unit {
    requires junit;
    requires identity.module;
    requires org.junit.jupiter.api;
    requires java.sql;
    //requires org.apache.tomcat.embed.core;

    exports test.unit to junit;
}