package identity.module.annotations;

public @interface Temporary {
    String purpose();
    String description() default "";
}
