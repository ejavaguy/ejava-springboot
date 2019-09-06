package info.ejava.examples.svc.springmvc.todos;

public class InternalErrorException extends RuntimeException {
    public InternalErrorException(String format, Object...args) {
        super(String.format(format, args));
    }
    public InternalErrorException(String msg) {
        super(msg);
    }
    public InternalErrorException(Throwable ex, String msg) {
        super(msg, ex);
    }
    public InternalErrorException(Throwable ex, String format, Object...args) {
        super(String.format(format, args), ex);
    }
}
