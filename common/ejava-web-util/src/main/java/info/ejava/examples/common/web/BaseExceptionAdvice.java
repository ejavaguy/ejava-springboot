package info.ejava.examples.common.web;

import info.ejava.examples.common.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.NoSuchElementException;

import static info.ejava.examples.common.exceptions.ClientErrorException.*;
import static info.ejava.examples.common.exceptions.ServerErrorException.InternalErrorException;

/**
 * This class provide custom error handling for exceptions thrown by the
 * controller. It is one of several techniques offered by Spring and
 * selected primarily because we retain full control over the response
 * and response headers returned to the caller.
 */
@Slf4j
public class BaseExceptionAdvice {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageDTO> handle(NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getError(), ex.getMessage(), ex.getDate());
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<MessageDTO> handle(NoSuchElementException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, null, ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<MessageDTO> handle(InvalidInputException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getError(), ex.getMessage(), ex.getDate());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<MessageDTO> handle(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getError(), ex.getMessage(), ex.getDate());
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<MessageDTO> handle(NotAuthorizedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getError(), ex.getMessage(), ex.getDate());
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<MessageDTO> handle(InternalErrorException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getError(), ex.getMessage(), ex.getDate());
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<MessageDTO> handle(AccessDeniedException ex) {
        String text = String.format("caller[%s] is forbidden from making this request", getPrincipal());
        return this.buildResponse(HttpStatus.FORBIDDEN, null, text, (Instant)null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDTO> handleRuntimeException(RuntimeException ex) {
        log.warn("{}", ex.getMessage(), ex);
        String text = String.format("unexpected error executing request: %s", ex.toString());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error", text, null);
    }

    protected ResponseEntity<MessageDTO> buildResponse(HttpStatus status, String error, String text, Instant date) {
        String url = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
        MessageDTO message = MessageDTO.builder()
                .url(url)
                .statusCode(status.value())
                .statusName(status.name())
                .message(error==null ? status.getReasonPhrase() : error)
                .description(text)
                .date(date!=null ? date : Instant.now())
                .build();
        ResponseEntity<MessageDTO> response = ResponseEntity
                .status(status)
                .body(message);
        return response;
    }

    protected String getPrincipal() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (NullPointerException ex) {
            return "null";
        }
    }
}
