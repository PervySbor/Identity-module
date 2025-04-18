module identity.module.test {
    requires junit;
    requires identity.module;
    requires org.junit.jupiter.api;
    requires java.sql;
    requires org.apache.tomcat.embed.core;
    requires java.net.http;

    exports test.unit to junit;
    exports test.integration to junit;
}