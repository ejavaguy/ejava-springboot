package info.ejava.examples.svc.docker.hello.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @GetMapping(produces = {MediaType.TEXT_PLAIN_VALUE})
    public String hello(@RequestParam("name")String name) {
        return "hello, " + name;
    }

}
