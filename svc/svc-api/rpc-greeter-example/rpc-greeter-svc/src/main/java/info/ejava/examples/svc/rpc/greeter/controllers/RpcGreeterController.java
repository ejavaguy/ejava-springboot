package info.ejava.examples.svc.rpc.greeter.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * The following is an example of using the HTTP constructs that are
 * a part of a RESTful interface, but using them in an RPC way.
 * The following is not even close to being RESTful and not what
 * I will be referring to as REST-like.
 */
@RestController
// ==> wraps @Controller
//      ==> wraps @Component
@RequestMapping("rpc/greeter")
@Slf4j
public class RpcGreeterController {
    /**
     * This is an example of a method as simple as it gets
     * @return hi
     */
    @RequestMapping(path="sayHi",
            method= RequestMethod.GET)
    public String sayHi()  {
        return "hi";
    }

    @RequestMapping(path="say/{greeting}",
            method=RequestMethod.GET)
    public String sayGreeting(
            @PathVariable("greeting")String greeting,
            @RequestParam(value = "name", defaultValue = "you") String name) {
        return greeting + ", " + name;
    }

    /**
     * This method is an example of the container returning an error when the
     * client does not supply a required query parameter.
     * @param value
     */
    @RequestMapping(path="boom",
            method=RequestMethod.GET)
    public String boom(@RequestParam(value = "value", required = true) String value) {
        return "worked?";
    }

    /**
     * This method is an example of how the controller method can have full
     * control over the response issued back to the caller.
     * @param name
     */
    @RequestMapping(path="boys",
            method=RequestMethod.GET)
    public ResponseEntity<String> createBoy(@RequestParam("name") String name) {
        try {
            someMethodThatMayThrowException(name);

            String url = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_LOCATION, url)
                    .body(String.format("hello %s, how do you do?", name));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.unprocessableEntity()
                    .body(ex.getMessage());
        }
    }
    private void someMethodThatMayThrowException(String name) {
        if ("blue".equalsIgnoreCase(name)) {
            throw new IllegalArgumentException("boy named blue?");
        }
    }

    /**
     * This method is an example of offloading detailed ResponseEntity
     * handling to an @ExceptionHandler to keep the controller method
     * clean.
     * @param name
     */
    @RequestMapping(path="boys/throws",
            method=RequestMethod.GET)
    public ResponseEntity<String> createBoyThrows(@RequestParam("name") String name) {
        //call some deeply nested code
        someMethodThatMayThrowException(name);

        String url = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/rpc/greeter/boys")
                .build().toUriString();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, url)
                .body(String.format("hello %s, how do you do?", name));
    }


    /**
     * This is an example handler that will convert an exception to a
     * ResponseEntity to return to the caller. It is supplied within the
     * controller here as a 1st step example. Later examples try to
     * generalize the solution and create a service-wide advice.
     * @param ex
     * @return ResponseEntity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handle(IllegalArgumentException ex) {
        return ResponseEntity.unprocessableEntity()
                .body(ex.getMessage());
    }
}
