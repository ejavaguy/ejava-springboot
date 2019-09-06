package info.ejava.examples.svc.authn.noauthn.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name="hello-controller", description = "demonstrates sample calls")
@RestController
public class HelloController {

    @Operation(description = "sample anonymous GET")
    @RequestMapping(path="/api/anonymous/hello",
        method= RequestMethod.GET)
    public String getHello(@RequestParam String name) {
        return "hello, " + name;
    }

    @Operation(description = "sample anonymous POST")
    @RequestMapping(path="/api/anonymous/hello",
            method = RequestMethod.POST,
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String postHello(@RequestBody String name) {
        return "hello, " + name;
    }

    @Operation(description = "sample authenticated GET")
    @RequestMapping(path="/api/authn/hello",
            method= RequestMethod.GET)
    public String getHelloAuthn(@RequestParam String name) {
        return "hello, " + name;
    }

    @Operation(description = "sample authenticated POST")
    @RequestMapping(path="/api/authn/hello",
            method= RequestMethod.POST,
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String postHelloAuthn(@RequestBody String name) {
        return "hello, " + name;
    }

    @Operation(description = "sample authenticated alt POST")
    @RequestMapping(path="/api/alt/hello",
            method= RequestMethod.POST,
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String postHelloAlt(@RequestBody String name) {
        return "hello, " + name;
    }
}
