package info.ejava.examples.svc.springmvc.todos.api;

import info.ejava.examples.svc.springmvc.todos.InternalErrorException;
import info.ejava.examples.svc.springmvc.todos.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("greetings")
@RequiredArgsConstructor
public class GreetingsController {
    private final GreetingService greetingService;


    @RequestMapping(value = "hi",
            method = RequestMethod.GET,
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> sayHi() {
        String entity = "hello";
        ResponseEntity<String> response = ResponseEntity.ok(entity);
        return response;
    }

    @RequestMapping(value = "greet",
            method = RequestMethod.GET,
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> greet(
            @RequestParam(value = "name", required = false) String name) {
        ResponseEntity<?> response = null;
        try {
            String entity = greetingService.greet(name);
            response = ResponseEntity.ok(entity);
        } catch (InvalidRequestException ex) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        } catch (InternalErrorException ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("unexpected error greeting name[%s]", name));
        }
        return response;
    }
}
