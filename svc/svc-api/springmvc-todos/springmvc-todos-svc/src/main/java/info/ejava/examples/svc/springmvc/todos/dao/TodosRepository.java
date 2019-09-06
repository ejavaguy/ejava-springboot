package info.ejava.examples.svc.springmvc.todos.dao;

import info.ejava.examples.svc.springmvc.todos.bo.TodoItem;
import info.ejava.examples.svc.springmvc.todos.bo.TodoList;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface TodosRepository {
    void deleteAll();

    void save(TodoList list);
    void delete(TodoList todoList);

    List<TodoList> findByName(String listName);
    List<TodoList> findAll(PageRequest pageRequest);
    void TodoItem(int id);

    TodoItem findTodoItemByTodoListNameAndTodoItemName(String listName, String itemName);
    TodoItem findTodoItemById(int id);

}
