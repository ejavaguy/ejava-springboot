package info.ejava.assignments.testing.race.racers.svc;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message, Object...args) {
        super(String.format(message, args));
    }
}
