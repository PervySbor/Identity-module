module identity.module.test {
    requires junit;
    requires identity.module;
    requires org.junit.jupiter.api;
    requires org.apache.tomcat.embed.core;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    exports test.unit to junit;
    exports test.integration to junit;
}