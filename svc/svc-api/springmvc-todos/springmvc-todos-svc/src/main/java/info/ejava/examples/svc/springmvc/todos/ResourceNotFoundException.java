package info.ejava.examples.svc.springmvc.todos;

public class ResourceNotFoundException extends ClientErrorException {
    public ResourceNotFoundException(String format, Object...args) {
        super(String.format(format, args));
    }
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
