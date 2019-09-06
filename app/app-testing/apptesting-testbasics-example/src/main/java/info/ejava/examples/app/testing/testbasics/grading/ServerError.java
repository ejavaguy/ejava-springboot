package info.ejava.examples.app.testing.testbasics.grading;

public class ServerError extends RuntimeException {
    public ServerError(String message) { super(message); }
    public ServerError(String message, Exception ex) { super(message, ex); }

    public static class InternalFailure extends RuntimeException {
        public InternalFailure(String message) { super(message); }
        public InternalFailure(String message, Exception ex) { super(message, ex); }
    }
}
