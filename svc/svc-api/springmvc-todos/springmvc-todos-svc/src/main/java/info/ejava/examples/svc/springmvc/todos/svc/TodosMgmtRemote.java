package info.ejava.examples.svc.springmvc.todos.svc;

import info.ejava.examples.svc.springmvc.todos.InvalidRequestException;
import info.ejava.examples.svc.springmvc.todos.ResourceNotFoundException;
import info.ejava.examples.svc.springmvc.todos.dto.TodoItemDTO;
import info.ejava.examples.svc.springmvc.todos.dto.TodoListDTO;
import info.ejava.examples.svc.springmvc.todos.dto.TodoListListDTO;

public interface TodosMgmtRemote {
    TodoListListDTO getTodoLists(int offset, int limit);
    TodoListDTO createTodoList(TodoListDTO todoList) throws InvalidRequestException;
    TodoListDTO getTodoList(String listName) throws ResourceNotFoundException;
    TodoListDTO renameTodoList(String oldName, String newName) throws ResourceNotFoundException;
    void deleteTodoList(String listName) throws ResourceNotFoundException;
    
    
    void addTodoListItem(String listName, TodoItemDTO item)
            throws ResourceNotFoundException, InvalidRequestException;
    TodoItemDTO updateTodoListItem(String listName, String itemName, TodoItemDTO item)
            throws ResourceNotFoundException, InvalidRequestException;
    void deleteTodoListItem(String listName, String itemName)
            throws ResourceNotFoundException, InvalidRequestException;
    
    void deleteAll();
}
