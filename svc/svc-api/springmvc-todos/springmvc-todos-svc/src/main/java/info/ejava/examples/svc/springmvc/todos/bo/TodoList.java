package info.ejava.examples.svc.springmvc.todos.bo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TodoList {
    private int id;
    private String name;
    private List<TodoItem> todoItems;
}
