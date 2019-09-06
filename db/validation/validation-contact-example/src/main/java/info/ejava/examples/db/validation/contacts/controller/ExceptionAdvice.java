package info.ejava.examples.db.validation.contacts.controller;

import info.ejava.examples.common.dto.MessageDTO;
import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.common.web.BaseExceptionAdvice;
import info.ejava.examples.db.validation.contacts.svc.InternalError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.Payload;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class ExceptionAdvice extends BaseExceptionAdvice {
    /**
     * Spring will throw a ConstraintViolationException when failing validation
     * for non-@RequestBody paramters or vanilla non-HTTP AOP calls.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageDTO> handle(ConstraintViolationException ex) {
        final String description = ex.getConstraintViolations().stream()
                .map(v->v.getPropertyPath().toString() + ": " + v.getMessage())
                .collect(Collectors.joining("\n"));
        boolean isFromAPI = false;
        isFromAPI = ex.getConstraintViolations().stream()
                .map(v -> v.getRootBean().getClass().getAnnotation(RestController.class))
                .filter(a->a!=null)
                .findFirst()
                .orElse(null)!=null;

        boolean isInternalError = false;
        isInternalError = isFromAPI ? false : ex.getConstraintViolations().stream()
                .map(v -> v.getConstraintDescriptor().getPayload())
                .filter(p-> p.contains(InternalError.class))
                .findFirst()
                .orElse(null)!=null;

//        HttpStatus status = HttpStatus.BAD_REQUEST;
//        final HttpStatus status = isFromAPI ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
        final HttpStatus status = isFromAPI || !isInternalError ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;


        return buildResponse(status, "Validation Error", description, (Instant)null);
    }

    /**
     * This exception is thrown when an HTTP/Controller method fails validation
     * during for @RequestBody parameters.
     * @param ex
     * @return response entity with error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageDTO> handle(MethodArgumentNotValidException ex) {
        List<String> fieldMsgs = ex.getFieldErrors().stream()
                .map(e -> e.getObjectName() + "." + e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());
        List<String> globalMsgs = ex.getGlobalErrors().stream()
                .map(e -> e.getObjectName() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());
        final String description = Stream.concat(fieldMsgs.stream(), globalMsgs.stream())
                .collect(Collectors.joining("\n"));
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Validation Error", description, (Instant)null);
    }
}
