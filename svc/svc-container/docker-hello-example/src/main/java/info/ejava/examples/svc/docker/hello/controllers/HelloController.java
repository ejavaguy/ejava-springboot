package info.ejava.examples.svc.docker.hello.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping(path="/api/hello",
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public String hello(
            @RequestParam("name")String name,
            @Parameter(hidden = true) //for swagger
            @AuthenticationPrincipal UserDetails user) {
        String username = user==null ? null : user.getUsername();
        String greeting = "hello, " + name;
        return username==null ? greeting : greeting + " (from " + username + ")";
    }
}
