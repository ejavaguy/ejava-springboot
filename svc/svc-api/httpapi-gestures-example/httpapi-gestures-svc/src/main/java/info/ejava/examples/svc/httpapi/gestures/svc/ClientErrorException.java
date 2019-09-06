package info.ejava.examples.svc.httpapi.gestures.svc;

public abstract class ClientErrorException extends RuntimeException {
    protected ClientErrorException(Throwable cause) {
        super(cause);
    }
    protected ClientErrorException(String message, Object...args) {
        super(String.format(message, args));
    }
    protected ClientErrorException(Throwable cause, String message, Object...args) {
        super(String.format(message, args), cause);
    }

    public static class NotFoundException extends ClientErrorException {
        public NotFoundException(String message, Object...args) {  super(message, args); }
        public NotFoundException(Throwable cause, String message, Object...args) { super(cause, message, args); }
    }

    public static class InvalidInputException extends ClientErrorException {
        public InvalidInputException(String message, Object...args) {  super(message, args); }
        public InvalidInputException(Throwable cause, String message, Object...args) { super(cause, message, args); }
    }
}
