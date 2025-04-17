module identity.module.unit {
    requires junit;
    requires identity.module;
    requires org.junit.jupiter.api;
    requires org.apache.tomcat.embed.core;

    exports unit to junit;
}