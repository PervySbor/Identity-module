package identity.module.exceptions;

public class NonUniqueUserException extends Exception {
    public NonUniqueUserException(String message) {
        super(message);
    }
}
