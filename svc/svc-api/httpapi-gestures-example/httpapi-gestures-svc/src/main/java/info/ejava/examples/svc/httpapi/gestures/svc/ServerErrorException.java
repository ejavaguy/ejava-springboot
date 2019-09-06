package info.ejava.examples.svc.httpapi.gestures.svc;

public abstract class ServerErrorException extends RuntimeException {
    protected ServerErrorException(Throwable cause) {
        super(cause);
    }
    protected ServerErrorException(String message, Object...args) {
        super(String.format(message, args));
    }
    protected ServerErrorException(Throwable cause, String message, Object...args) {
        super(String.format(message, args), cause);
    }

    public static class InternalErrorException extends ServerErrorException {
        public InternalErrorException(String message, Object...args) {  super(message, args); }
        public InternalErrorException(Throwable cause, String message, Object...args) { super(cause, message, args); }
    }
}
