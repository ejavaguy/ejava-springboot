package info.ejava.examples.svc.springmvc.todos.dto;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static info.ejava.examples.svc.springmvc.todos.dto.BddAssertions.then;
import static org.assertj.core.api.BDDAssertions.then;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class MarshallingTest {
    protected abstract <T> String marshal(T object) throws Exception;
    protected abstract <T> T demarshal(Class<T> type, String buffer) throws Exception;
    
    private TodoListDTO buildTodoList(String name) {
        TodoListDTO todoList = TodoListDTO.builder()
            .name(name)
            .todoItems(new LinkedList<>())
            .build();
        for (int i=0; i<3; i++) {
            TodoItemDTO item = new TodoItemDTO("item" + i, i);
            todoList.getTodoItems().add(item);
        }
        return todoList;
    }

    private <T> T marshal_and_demarshal(T obj, Class<T> type) throws Exception {
        String buffer = marshal(obj);
        T result = demarshal(type, buffer);
        return result;
    }
    
    @Test
    public void message_dto_marshals() throws Exception {
        //given - a message
        MessageDTO msg = new MessageDTO("sample text");

        //when - marshalled to a string and demarshalled back to an object
        MessageDTO result = marshal_and_demarshal(msg, MessageDTO.class);

        //then
        then(result.getText()).isEqualTo(msg.getText());
    }

    @Test
    public void todoItem_dto_marshals() throws Exception {
        //given - a message with text and priority
        TodoItemDTO item = new TodoItemDTO("item1", 13);

        //when - marshalled to a string and demarshalled back to an object
        TodoItemDTO result = marshal_and_demarshal(item, TodoItemDTO.class);

        //then
        then(result).hasName(item.getName());
        then(result).hasPriority(item.getPriority());
    }
    
    @Test
    public void todoList_dto_marshals() throws Exception {
        //given
        TodoListDTO todoList = buildTodoList("testA");

        //when
        TodoListDTO result = marshal_and_demarshal(todoList, TodoListDTO.class);

        //then
        then(result).hasName(todoList.getName());
        then(result.getTodoItems()).hasSize(todoList.getTodoItems().size());

        //and then -- all the same items will be present in new list
        for (TodoItemDTO item: todoList.getTodoItems()) {
            TodoItemDTO r = result.getTodoItems().stream()
                    .filter(lst->lst.getName().equals(item.getName()))
                    .findFirst().orElse(null);

            then(r).as("name not found: %s", item.getName()).isNotNull();
            then(r).hasPriority(item.getPriority());
        }
    }
    
    @Test
    public void todoListList_dto_marshals() throws Exception {
        //given a list of 2odo lists with several items
        TodoListListDTO todoLists = new TodoListListDTO(new ArrayList<>());
        for (int i=0; i<3; i++) {
            todoLists.getTodoLists().add(buildTodoList("list"+i));
        }

        //when
        TodoListListDTO result = marshal_and_demarshal(todoLists, TodoListListDTO.class);

        //then -- the list of lists should be the same size
        then(result.getCount()).as("unexpected list count").isEqualTo(todoLists.getCount());

        //and then -- each list should be present in the result
        for (TodoListDTO list: todoLists.getTodoLists()) {
            TodoListDTO r = result.getTodoLists().stream()
                    .filter(lst->lst.getName().equals(list.getName()))
                    .findFirst().orElse(null);
            then(r).as("name not found: %s", list.getName());
        }
    }
}
