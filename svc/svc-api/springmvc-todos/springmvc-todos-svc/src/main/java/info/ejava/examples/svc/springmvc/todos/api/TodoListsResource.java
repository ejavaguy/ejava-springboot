package info.ejava.examples.svc.springmvc.todos.api;

import org.springframework.web.bind.annotation.RestController;

@RestController("todo_lists")
public class TodoListsResource {
/*
    private static final Logger logger = LoggerFactory.getLogger(TodoListsResource.class);
    
    @EJB
    private TodosMgmtRemote todosMgmt;
    @Context
    private UriInfo uriInfo;
    
    @GET @Path("")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getTodoLists(
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("limit") @DefaultValue("10") Integer limit) {
        ResponseBuilder rb = null;
        try {
            TodoListListDTO entity = todosMgmt.getTodoLists(offset, limit);
            rb = Response.ok(entity)
                    .contentLocation(uriInfo.getAbsolutePath());
        } catch (InternalErrorException ex) {
            rb = Response.serverError().entity(ex.getMessage());
        } catch (Exception ex) {
            logger.info("Unexpected exception getting TodoLists", ex);
            String msg = String.format("Unexpected error getting TodoLists: %s", ex.toString());
            rb = Response.serverError()
                    .entity(new MessageDTO(msg));
        }
        return rb.build();
    }
    
    private ResponseBuilder getUndexpectedErrorResponse(String message, Exception ex) {
        logger.info(message, ex);
        String msg = String.format("%s: %s", message, ex.toString());
        return Response.serverError()
                .entity(new MessageDTO(msg));        
    }
    private ResponseBuilder getInternalErrorResponse(Exception ex) {
        logger.info(ex.getMessage());
        return Response.serverError()
                .entity(new MessageDTO(ex.getMessage()));        
    }
    private ResponseBuilder getBadRequestResponse(Exception ex) {
        logger.debug(ex.getMessage());
        return Response.status(Status.BAD_REQUEST)
                .entity(new MessageDTO(ex.getMessage()));        
    }
    private ResponseBuilder getNotFoundResponse(Exception ex) {
        logger.debug(ex.getMessage());
        return Response.status(Status.NOT_FOUND)
                .entity(new MessageDTO(ex.getMessage()));        
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createTodoList(TodoListDTO todoList) {
        ResponseBuilder rb = null;
        try {
            TodoListDTO entity = todosMgmt.createTodoList(todoList);
            URI location = uriInfo.getBaseUriBuilder()
                    .path(TodoListsResource.class)
                    .path(TodoListsResource.class, "getTodoList")
                    .build(entity.getName());
            rb = Response.created(location)                    
                    .contentLocation(location)
                    .entity(entity);
        } catch (InvalidRequestException ex) {
            rb = getBadRequestResponse(ex);
        } catch (InternalErrorException ex) {
            rb = getInternalErrorResponse(ex);
        } catch (Exception ex) {
            rb = getUndexpectedErrorResponse("Unexpected error creating TodoList", ex);
        }
        return rb.build();
    }

    @GET @Path("{listName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getTodoList(@PathParam("listName") String listName) {
        ResponseBuilder rb = null;
        try {
            TodoListDTO entity = todosMgmt.getTodoList(listName);
            rb = Response.ok(entity)
                    .contentLocation(uriInfo.getAbsolutePath());
        } catch (ResourceNotFoundException ex) {
            rb = getNotFoundResponse(ex);
        } catch (InternalErrorException ex) {
            rb = getInternalErrorResponse(ex);
        } catch (Exception ex) {
            rb = getUndexpectedErrorResponse("Unexpected error creating TodoList", ex);
        }
        return rb.build();        
    }

    @POST @Path("{listName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response renameTodoList(
            @PathParam("listName") String oldName,
            @QueryParam("name") String newName) {
        ResponseBuilder rb = null;
        try {
            TodoListDTO entity = todosMgmt.renameTodoList(oldName, newName);
            URI location = uriInfo.getBaseUriBuilder()
                    .path(TodoListsResource.class, "getTodoList")
                    .build(entity.getName());
            rb = Response.ok(entity)
                    .location(location)
                    .contentLocation(location);
        } catch (ResourceNotFoundException ex) {
            rb = getNotFoundResponse(ex);
        } catch (InternalErrorException ex) {
            rb = getInternalErrorResponse(ex);
        } catch (Exception ex) {
            rb = getUndexpectedErrorResponse("Unexpected error creating TodoList", ex);
        }
        return rb.build();        
    }
    
    @DELETE @Path("{listName}")
    public Response deleteTodoList(@PathParam("listName") String listName) {
        ResponseBuilder rb = null;
        try {
            todosMgmt.deleteTodoList(listName);
            rb = Response.noContent();
        } catch (ResourceNotFoundException ex) {
            rb = getNotFoundResponse(ex);
        } catch (InternalErrorException ex) {
            rb = getInternalErrorResponse(ex);
        } catch (Exception ex) {
            rb = getUndexpectedErrorResponse("Unexpected error deleting TodoList", ex);
        }
        return rb.build();                
    }

    
    @DELETE
    public Response deleteAllTodos() {
        ResponseBuilder rb=null;
        try {
            todosMgmt.deleteAll();
            rb=Response.ok();
        } catch (InternalErrorException ex) {
            rb = getInternalErrorResponse(ex);
        } catch (Exception ex) {
            rb = getUndexpectedErrorResponse("Unexpected error deleting all Todos", ex);
        }
        return rb.build();
    }
    
//========================    
    
    @POST @Path("{listName}/todo_items")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addTodoItem(
            @PathParam("listName") String listName, 
            TodoItemDTO item) {
        ResponseBuilder rb = null;
        try {
            todosMgmt.addTodoListItem(listName, item);
            rb = Response.ok();
        } catch (ResourceNotFoundException ex) {
            rb = getNotFoundResponse(ex);
        } catch (InvalidRequestException ex) {
            rb = getBadRequestResponse(ex);
        } catch (InternalErrorException ex) {
            rb = getInternalErrorResponse(ex);
        } catch (Exception ex) {
            rb = getUndexpectedErrorResponse("Unexpected error adding TodoItem", ex);
        }
        return rb.build();                        
    }

    
    @PUT @Path("{listName}/todo_items/{itemName}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateTodoItem(
            @PathParam("listName") String listName,
            @PathParam("itemName") String itemName,
            TodoItemDTO item) {
        ResponseBuilder rb = null;
        try {
            TodoItemDTO entity = todosMgmt.updateTodoListItem(listName, itemName, item);
            rb = Response.ok(entity);
        } catch (ResourceNotFoundException ex) {
            rb = getNotFoundResponse(ex);
        } catch (InvalidRequestException ex) {
            rb = getBadRequestResponse(ex);
        } catch (InternalErrorException ex) {
            rb = getInternalErrorResponse(ex);
        } catch (Exception ex) {
            rb = getUndexpectedErrorResponse("Unexpected error updating TodoItems", ex);
        }
        return rb.build();                                
    }

    
    @DELETE @Path("{listName}/todo_items/{itemName}")
    public Response deleteTodoItem(
            @PathParam("listName") String listName, 
            @PathParam("itemName") String itemName) {
        ResponseBuilder rb = null;
        try {
            todosMgmt.deleteTodoListItem(listName, itemName);
            rb = Response.noContent();
        } catch (ResourceNotFoundException ex) {
            rb = getNotFoundResponse(ex);
        } catch (InternalErrorException ex) {
            rb = getInternalErrorResponse(ex);
        } catch (Exception ex) {
            rb = getUndexpectedErrorResponse("Unexpected error deleting TodoItem", ex);
        }
        return rb.build();                                
    }

 */
}
