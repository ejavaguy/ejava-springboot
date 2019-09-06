package info.ejava.examples.svc.springmvc.todos;

public class InvalidRequestException extends ClientErrorException {
    public InvalidRequestException(String format, Object...args) {
        super(String.format(format, args));
    }
    public InvalidRequestException(String msg) {
        super(msg);
    }
}
