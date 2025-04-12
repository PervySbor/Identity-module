module identity.module.unit {
    requires junit;
    requires identity.module;
    requires java.logging;
    requires org.junit.jupiter.api;

    exports unit to junit;
}