package info.ejava_student.assignment1.testing.races.racer.svc;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message, Object...args) {
        super(String.format(message, args));
    }
}
