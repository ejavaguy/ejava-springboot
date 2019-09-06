package info.ejava.examples.svc.springmvc.todos.bo;

import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@With
public class TodoItem {
    private int id;
    private String name;
    private int priority;
    private TodoList todoList;
}
