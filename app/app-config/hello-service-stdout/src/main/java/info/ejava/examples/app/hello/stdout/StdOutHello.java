package info.ejava.examples.app.hello.stdout;

import info.ejava.examples.app.hello.Hello;

public class StdOutHello implements Hello {
    private final String greeting;
    
    public StdOutHello(String greeting) {
        this.greeting = greeting;
    }
    
    @Override
    public void sayHello(String name) {
        System.out.println(greeting + " " + name);
    }
}
