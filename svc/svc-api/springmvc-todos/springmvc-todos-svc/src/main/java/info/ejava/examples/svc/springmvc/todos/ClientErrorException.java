package info.ejava.examples.svc.springmvc.todos;

public class ClientErrorException extends Exception {
    public ClientErrorException(String msg) {
        super(msg);
    }
}
