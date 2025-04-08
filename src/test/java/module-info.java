module identity.module.test {
    requires junit;
    requires identity.module;
    requires java.logging;
    requires org.junit.jupiter.api;

    exports test to junit;
}