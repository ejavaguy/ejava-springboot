package info.ejava.examples.app.testing.testbasics.grading;

public class ClientError extends Exception {
    public ClientError(String message) { super(message); }
    public ClientError(String message, Exception ex) { super(message, ex); }

    public static class BadRequest extends ClientError {
        public BadRequest(String message) { super(message); }
        public BadRequest(String message, Exception ex) { super(message); }
    }

    public static class NotFound extends ClientError {
        public NotFound(String message) { super(message); }
        public NotFound(String message, Exception ex) { super(message); }
    }
}
