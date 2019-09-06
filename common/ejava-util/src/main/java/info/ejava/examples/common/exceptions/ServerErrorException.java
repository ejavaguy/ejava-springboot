package info.ejava.examples.common.exceptions;

import java.time.Instant;

public abstract class ServerErrorException extends RuntimeException {
    protected Instant date = Instant.now();
    protected String error;

    public ServerErrorException withDate(Instant date) {
        this.date = date;
        return this;
    }
    public ServerErrorException withError(String error) {
        this.error = error;
        return this;
    }

    public String getError() {
        return error;
    }
    public Instant getDate() {
        return date;
    }
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
