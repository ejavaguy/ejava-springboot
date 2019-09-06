package info.ejava.examples.svc.springmvc.todos.svc;

import info.ejava.examples.svc.springmvc.todos.InternalErrorException;
import info.ejava.examples.svc.springmvc.todos.InvalidRequestException;
import info.ejava.examples.svc.springmvc.todos.ResourceNotFoundException;
import info.ejava.examples.svc.springmvc.todos.bo.TodoItem;
import info.ejava.examples.svc.springmvc.todos.bo.TodoList;
import info.ejava.examples.svc.springmvc.todos.dao.TodosRepository;
import info.ejava.examples.svc.springmvc.todos.dto.TodoItemDTO;
import info.ejava.examples.svc.springmvc.todos.dto.TodoListDTO;
import info.ejava.examples.svc.springmvc.todos.dto.TodoListListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.util.List;

//@Service
@RequiredArgsConstructor
public class TodosMgmtService implements TodosMgmtRemote {
    private final DtoMapper dtoMapper;
    private final TodosRepository todosRepo;

    @Override
    public TodoListListDTO getTodoLists(int offset, int limit) {
        List<TodoList> result = getTodoListsLocal(offset, limit);
        return dtoMapper.map(result);
    }

    public List<TodoList> getTodoListsLocal(int offset, int limit) {
        return todosRepo.findAll(PageRequest.of(offset / limit , limit));
    }

    @Override
    public TodoListDTO createTodoList(TodoListDTO todoList) {
        try {
            TodoList result = createTodoList(dtoMapper.map(todoList));
            return dtoMapper.map(result);
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error creating todoList: %s", ex.toString());
        }
    }
    
    public TodoList createTodoList(TodoList todoList) {
        todosRepo.save(todoList);
        return todoList;
    }

    @Override
    public TodoListDTO getTodoList(String listName) throws ResourceNotFoundException {
        try {
            TodoList result = getTodoListLocal(listName);
            if (result==null) {
                throw new ResourceNotFoundException("listName[%s] not found", listName);
            }
            return dtoMapper.map(result);
        } catch (RuntimeException ex) {            
            throw new InternalErrorException("Error getting todoList: %s", ex.toString());
        }
    }
    
    public TodoList getTodoListLocal(String listName) {
        List<TodoList> results = todosRepo.findByName(listName);
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public TodoListDTO renameTodoList(String oldName, String newName) throws ResourceNotFoundException {
        try {
            TodoList todoList = getTodoListLocal(oldName);
            if (todoList==null) {
                throw new ResourceNotFoundException("todoList[%s] not found", oldName);
            }
            todoList.setName(newName);
            return dtoMapper.map(todoList);
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error renaming todoList: %s", ex.toString());            
        }
    }

    @Override
    public void deleteTodoList(String listName) throws ResourceNotFoundException {
        try {
            if (deleteTodoListLocal(listName)==0) {
                throw new ResourceNotFoundException("todoList[%s] not found", listName);
            }
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error deleting todoList: %s", ex.toString());            
        }
    }
    
    public int deleteTodoListLocal(String listName) {
        TodoList todoList = getTodoListLocal(listName);
        if (todoList==null) {
            return 0;
        } else {
            todosRepo.delete(todoList);
            return 1;
        }            
    }

    @Override
    public void addTodoListItem(String listName, TodoItemDTO item)
            throws ResourceNotFoundException, InvalidRequestException {
        try {
            TodoList todoList = getTodoListLocal(listName);
            if (todoList==null) {
                throw new ResourceNotFoundException("todoList[%s] not found", listName);
            }
            TodoItem itemBO = dtoMapper.map(item);
            if (itemBO==null) {
                throw new InvalidRequestException("required item not supplied");
            }
            
            addTodoListItem(todoList, itemBO);
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error adding listItem to todoList[%s]: %s", listName, ex.toString());            
        }
    }
    
    public void addTodoListItem(TodoList todoList, TodoItem item) {
        item.setTodoList(todoList);
        todoList.getTodoItems().add(item);
    }

    public TodoItem getTodoListItem(String listName, String itemName) {
        TodoItem item = todosRepo.findTodoItemByTodoListNameAndTodoItemName(listName, itemName);
        return item;
    }
    
    @Override
    public TodoItemDTO updateTodoListItem(String listName, String itemName, TodoItemDTO item)
            throws ResourceNotFoundException {
        try {
            TodoItem dbCopy = getTodoListItem(listName, itemName);
            if (dbCopy==null) {
                throw new ResourceNotFoundException("todoList[%s], todoItem[%s] not found", listName, itemName);
            }
                //assign the PK from the DB copy retrieved by distinct list+item name
            TodoItem toUpdate = dtoMapper.map(item);
            toUpdate.setId(dbCopy.getId());
            
            return dtoMapper.map(updateTodoListItem(toUpdate));
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error updating todoList: %s", ex.toString());            
        }
    }
    
    public TodoItem updateTodoListItem(TodoItem item) {
        TodoItem dbItem = todosRepo.findTodoItemById(item.getId());
        dbItem.setName(item.getName());
        dbItem.setPriority(item.getPriority());
        return dbItem;
    }


    @Override
    public void deleteTodoListItem(String listName, String itemName) throws ResourceNotFoundException {
        try {
            TodoItem item = getTodoListItem(listName, itemName);
            if (item==null) {
                throw new ResourceNotFoundException("todoList[%s], todoItem[%s] not found", listName, itemName);
            }
            deleteTodoListItem(item);
        } catch (RuntimeException ex) {
            throw new InternalErrorException("Error deleting todoList: %s", ex.toString());            
        }
    }
    
    public void deleteTodoListItem(TodoItem item) {
        todosRepo.TodoItem(item.getId());
    }

    
    @Override
    public void deleteAll() {
        todosRepo.deleteAll();
    }
}
