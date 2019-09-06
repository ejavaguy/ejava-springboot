package info.ejava.examples.svc.springmvc.todos.api;

import info.ejava.examples.svc.springmvc.todos.InternalErrorException;
import info.ejava.examples.svc.springmvc.todos.InvalidRequestException;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    public String greet(String name) throws InvalidRequestException {
        try {
            if (name==null || name.isEmpty()) {
                throw new InvalidRequestException("Unable to greet, name not supplied");
            }
            
            return String.format("hello %s", name);  //core business code
            
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Internal error greeting name[%s]: %s", name, ex);
        }
    }
}
