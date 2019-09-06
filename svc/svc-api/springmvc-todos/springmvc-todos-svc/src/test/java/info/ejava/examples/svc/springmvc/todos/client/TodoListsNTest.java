package info.ejava.examples.svc.springmvc.todos.client;

import org.junit.jupiter.api.Test;

public class TodoListsNTest {
    @Test
    public void test() {}
/*
    private static final Logger logger = LoggerFactory.getLogger(TodoListsNTest.class);
    private static final String baseHttpUrlString = getITProperty("url.base.http", "http://localhost:8080");
    private static final String mediaType = System.getProperty("media_type", "application/json");
    //private static final String mediaType = System.getProperty("media_type", "application/xml");
    
    private TodosJaxRsClient todosClient;

    @Before
    public void setUp() throws NamingException {
        String baseUrlString = baseHttpUrlString;
        URI baseTodosUrl = UriBuilder.fromPath(baseUrlString).path("ejavaTodos/controllers").build();
        
        Client jaxRsClient = ClientBuilder.newClient();
        todosClient = new TodosJaxRsClientImpl(jaxRsClient, baseTodosUrl, mediaType);
        
        assertSuccess("error deleting all", todosClient.deleteAll());
    }
    
    static String getITProperty(String key, String defaultValue) {
        Properties props = new Properties();
        try (InputStream is = TodoListsNTest.class.getResourceAsStream("/it.properties")) {
            props.load(is);            
            return props.containsKey(key) ? (String)props.get(key) : defaultValue;        
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Test
    public void createTodosList() {
        TodoListDTO todoList = new TodoListDTO("My First List");
        Response response = todosClient.createTodoList(todoList);
        
        logger.info("{} {}", response.getStatus(), response.getStatusInfo());
        if (response.getStatusInfo().getFamily() != Status.Family.SUCCESSFUL) {
            fail(response.readEntity(String.class));
        }
        
        assertEquals("unexpected status family", Status.Family.SUCCESSFUL, response.getStatusInfo().getFamily());
        assertEquals("unexpected status", Status.CREATED, response.getStatusInfo());
        
        TodoListDTO createdList = response.readEntity(TodoListDTO.class);
        assertEquals("unexpected name", todoList.getName(), createdList.getName());
    }
    
    @Test
    public void getTodoLists() {
        int count=3;
        Set<String> names = new HashSet<>();
        for (int i=0; i<count; i++) {
            TodoListDTO todoList = new TodoListDTO("list" + i);
            assertSuccess("error creating todoList", todosClient.createTodoList(todoList));
            names.add(todoList.getName());
        }
        
        TodoListListDTO todoLists =getEntity(todosClient.getTodoLists(null, null), TodoListListDTO.class);
        assertEquals("unexpected number of total lists", names.size(), todoLists.getCount());
        
        todoLists =getEntity(todosClient.getTodoLists(0, 2), TodoListListDTO.class);
        assertEquals("unexpected number of total lists", 2, todoLists.getCount());
        for(TodoListDTO tl: todoLists.getTodoLists()) {
            names.remove(tl.getName());
        }
        
        todoLists =getEntity(todosClient.getTodoLists(2, 1), TodoListListDTO.class);
        assertEquals("unexpected number of total lists", 1, todoLists.getCount());
        assertEquals("unexpected name for list", 
                names.iterator().next(), 
                todoLists.getTodoLists().get(0).getName());
    }
    
    @Test
    public void getTodoList() {
        TodoListDTO todoList = new TodoListDTO("test");
        assertSuccess("error creating todoList", todosClient.createTodoList(todoList));
        
            //request a resource that does not exist
        Response response = todosClient.getTodoList("foobar_not_exist");
        assertEquals("unexpected error family", 
                Status.Family.CLIENT_ERROR, 
                response.getStatusInfo().getFamily());
        assertEquals("unexpected status", 
                Status.NOT_FOUND, 
                response.getStatusInfo());
        logger.debug("{}", response.readEntity(MessageDTO.class));
        
            //request a resource that does exist
        TodoListDTO resultList = getEntity(todosClient.getTodoList(todoList.getName()), TodoListDTO.class);
        assertEquals("unexpected todoList name", todoList.getName(), resultList.getName());
        logger.debug("{}", resultList);
    }
    
    @Test
    public void deleteTodoList() {
        TodoListListDTO startingLists =getEntity(todosClient.getTodoLists(null, null), TodoListListDTO.class);
        
        TodoListDTO todoList = new TodoListDTO("test");
        assertSuccess("error creating todoList", todosClient.createTodoList(todoList));
        assertEquals("unexpected number of lists", 
                startingLists.getCount()+1, 
                getEntity(todosClient.getTodoLists(null, null), TodoListListDTO.class).getCount());
        
            //attempt to delete a list that does not exist
        Response response = todosClient.deleteTodoList("foobar_not_exist");
        assertEquals("unexpected status family", 
                Status.Family.CLIENT_ERROR, 
                response.getStatusInfo().getFamily());
        assertEquals("unexpected status", Status.NOT_FOUND, response.getStatusInfo());
        logger.debug("{}", response.readEntity(MessageDTO.class));
        
        //request a resource that does exist
        assertSuccess("error deleting todoList", todosClient.deleteTodoList(todoList.getName()));
        
        TodoListListDTO endingLists =getEntity(todosClient.getTodoLists(null, null), TodoListListDTO.class);
        assertEquals("unexpected number of lists", startingLists.getCount(), endingLists.getCount());
    }
    
    @Test
    public void renameTodoList() {
        TodoListDTO todoList = new TodoListDTO("test");
        assertSuccess("error creating todoList", todosClient.createTodoList(todoList));
        
            //attempt to rename a non-existant list
        Response response = todosClient.renameTodoList("foobar_not_exist","why_bother");
        assertEquals("unexpected status family", 
                Status.Family.CLIENT_ERROR, 
                response.getStatusInfo().getFamily());
        assertEquals("unexpected status", Status.NOT_FOUND, response.getStatusInfo());
        logger.debug("{}", response.readEntity(MessageDTO.class));
        
            //rename an existing list
        TodoListDTO renamed = 
            getEntity(todosClient.renameTodoList(todoList.getName(),"new_name"), TodoListDTO.class);
        assertNotEquals("name has not changed", todoList.getName(), renamed.getName());
        assertEquals("unexpected name", "new_name", renamed.getName());
        
            //get the list using names used
        assertSuccess("error getting renamed list", todosClient.getTodoList(renamed.getName()));
        response=todosClient.getTodoList(todoList.getName());
        logger.debug("{}", response.readEntity(String.class));
        assertEquals("unexpected status", Status.NOT_FOUND, response.getStatusInfo());
    }
    
    @Test
    public void addTodoItem() {
        TodoListDTO todoList = new TodoListDTO("test");        
        assertSuccess("error creating todoList", todosClient.createTodoList(todoList));
        
        TodoItemDTO todoItem = new TodoItemDTO("this");
        
            //attempt to add the item to a non-existent list
        Response response = todosClient.addTodoItem("foobar_not_exist", todoItem);
        assertEquals("unexpected status", Status.NOT_FOUND, response.getStatusInfo());
        logger.debug("{}", response.readEntity(MessageDTO.class));
        
            //attempt to pass no item
//errors are too noisy        
//        response = todosClient.addTodoItem(todoList.getName(), null);
//        logger.debug("{} {} : {}", response.getStatus(), response.getStatusInfo(), response.readEntity(String.class));
//        assertNotEquals("unexpected status", Status.Family.SUCCESSFUL, response.getStatusInfo().getFamily());
        
            //add the valid item to an existing list
        assertSuccess("error adding todoItem",todosClient.addTodoItem(todoList.getName(), todoItem));
        
            //get the TodoList with its new item
        TodoListDTO updatedList = getEntity(todosClient.getTodoList(todoList.getName()), TodoListDTO.class);
        assertEquals("unexpected number of items", 1, updatedList.getTodoItems().size());
        TodoItemDTO addedItem = updatedList.getTodoItems().get(0);
        assertEquals("unexpected todoItem name", todoItem.getName(), addedItem.getName());
    }
    
    @Test
    public void updateTodoItem() {
        TodoListDTO todoList = new TodoListDTO("test");        
        assertSuccess("error creating todoList", todosClient.createTodoList(todoList));
        
        //add the items with various priorities
        Random random = new Random();
        for (int i=0; i<10; i++) {
            TodoItemDTO todoItem = new TodoItemDTO("this"+i);
            todoItem.setPriority(random.nextInt(10));
            todosClient.addTodoItem(todoList.getName(), todoItem);
        }
        
        //get the todoList and verify we received them in priority order
        todoList = getEntity(todosClient.getTodoList(todoList.getName()), TodoListDTO.class);
        int lastValue=0;
        for (TodoItemDTO item: todoList.getTodoItems()) {
            logger.debug("{}", item);
            assertTrue("unexpected priority", item.getPriority()>=lastValue);
            lastValue = item.getPriority();
        }
        
        //change the priority to a fixed value
        for (TodoItemDTO item: todoList.getTodoItems()) {
            item.setPriority(5);
            TodoItemDTO updated = 
                getEntity(todosClient.updateTodoItem(todoList.getName(), item),TodoItemDTO.class);
            assertEquals("unexpected priority", new Integer(5), updated.getPriority());
        }        
    }
    
    @Test
    public void deleteTodoItem() {
        TodoListDTO todoList = new TodoListDTO("test");
        todoList.setTodoItems(new LinkedList<TodoItemDTO>());
        for (int i=0; i<10; i++) {
            TodoItemDTO todoItem = new TodoItemDTO("this"+i);
            todoItem.setPriority(i);
            todoList.getTodoItems().add(todoItem);
        }
        todoList = getEntity(todosClient.createTodoList(todoList), TodoListDTO.class);
        assertEquals("unexpected number of todoItems", 10, todoList.getTodoItems().size());
        
        for (TodoItemDTO item: todoList.getTodoItems()) {
            assertSuccess("error deleting todoItem", 
                    todosClient.deleteTodoItem(todoList.getName(), item.getName()));
        }
        todoList = getEntity(todosClient.getTodoList(todoList.getName()), TodoListDTO.class);
        assertTrue("todoItems not empty: " + (todoList.getTodoItems()==null?0:todoList.getTodoItems().size()),
                todoList.getTodoItems()==null || todoList.getTodoItems().isEmpty());
    }

 */
}
