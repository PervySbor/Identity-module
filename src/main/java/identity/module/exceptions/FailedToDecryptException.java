package identity.module.exceptions;

public class FailedToDecryptException extends Exception {
    public FailedToDecryptException(String message) {
        super(message);
    }
}
