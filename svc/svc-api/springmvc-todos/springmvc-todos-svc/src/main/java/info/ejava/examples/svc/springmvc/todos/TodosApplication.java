package info.ejava.examples.svc.springmvc.todos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@ApplicationPath("controllers")
@SpringBootApplication
public class TodosApplication {
    public static void main(String...args) {
        SpringApplication.run(TodosApplication.class, args);
    }
}
