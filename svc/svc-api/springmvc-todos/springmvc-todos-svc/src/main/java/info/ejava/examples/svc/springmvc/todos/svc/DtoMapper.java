package info.ejava.examples.svc.springmvc.todos.svc;

import info.ejava.examples.svc.springmvc.todos.bo.TodoItem;
import info.ejava.examples.svc.springmvc.todos.bo.TodoList;
import info.ejava.examples.svc.springmvc.todos.dto.TodoItemDTO;
import info.ejava.examples.svc.springmvc.todos.dto.TodoListDTO;
import info.ejava.examples.svc.springmvc.todos.dto.TodoListListDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {
    public TodoList map(TodoListDTO dto) {
        TodoList bo = TodoList.builder().name(dto.getName()).build();

        if (dto.getTodoItems()!=null) {
            for (TodoItemDTO itemDTO: dto.getTodoItems()) {
                TodoItem itemBO = map(itemDTO);
                itemBO.setTodoList(bo);
                bo.getTodoItems().add(itemBO);
            }
        }
        return bo;
    }
    
    public TodoListDTO map(TodoList bo) {
        if (bo==null) { return null; }
        TodoListDTO dto = new TodoListDTO();
        dto.setName(bo.getName());
        List<TodoItemDTO> items = bo.getTodoItems().stream()
                                                   .map(item->map(item))
                                                   .collect(Collectors.toList());
        dto.setTodoItems(items);
        return dto;
    }
    
    public TodoItemDTO map(TodoItem bo) {
        TodoItemDTO dto = TodoItemDTO.builder()
                .name(bo.getName())
                .priority(bo.getPriority())
                .build();
        return dto;
    }

    public TodoListListDTO map(List<TodoList> bo) {
        if (bo==null) { return null; }
        List<TodoListDTO> todoLists = bo.stream()
                                        .map(list->map(list))
                                        .collect(Collectors.toList());
        TodoListListDTO dto = new TodoListListDTO(todoLists);
        return dto;
    }

    public TodoItem map(TodoItemDTO dto) {
        if (dto==null) { return null; }
        TodoItem bo = TodoItem.builder()
                .name(dto.getName())
                .priority(dto.getPriority()==null ? 0 : dto.getPriority())
                .build();
        return bo;
    }
}
