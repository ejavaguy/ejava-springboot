package info.ejava.examples.svc.springmvc.todos.client;

public class TodosJaxRsClientImpl implements TodosJaxRsClient {
/*

    private static final Logger logger = LoggerFactory.getLogger(TodosJaxRsClientImpl.class);
    private URI baseUrl;
    private Client client;
    private MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;

    public TodosJaxRsClientImpl(Client client, URI baseUrl, String mediaType) {
        this.client = client;
        this.baseUrl = baseUrl;
        setMediaType(mediaType);
    }
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
    public void setMediaType(String mediaType) {
        setMediaType(MediaType.valueOf(mediaType));
    }

    private UriBuilder getBaseUrl(String...path) {
        UriBuilder builder = UriBuilder.fromUri(baseUrl);
        if (path!=null) {
            for (String p:path) {
                builder = builder.path(p);
            }
        }
        return builder;
    }

    private String marshal(Object object) {
        try {
            if (object==null) {
                return null;
            } else if (MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
                return JsonbUtil.marshal(object);
            } else if (MediaType.APPLICATION_SVG_XML_TYPE.equals(mediaType)) {
                return JAXBUtil.marshal(object);
            } else {
                return object.toString();
            }
        } catch (Exception ex) {
            return ""; //don't barf if missing any supporting libs
        }
    }

    @Override
    public Response deleteAll() {
        URI uri = getBaseUrl(TODO_LISTS_PATH).build();
        WebTarget target = client.target(uri);
        logger.debug("DELETE {}", target.getUri());

        return target.request(mediaType)
                .buildDelete()
                .invoke();
    }

    @Override
    public Response getTodoLists(Integer offset, Integer limit) {
        URI uri = getBaseUrl(TODO_LISTS_PATH).build();

        WebTarget target = client.target(uri);
        if (offset!=null) {
            target=target.queryParam(OFFSET, offset);
        }
        if (limit!=null) {
            target=target.queryParam(LIMIT, limit);
        }
        logger.debug("GET {}", target.getUri());

        return target.request(mediaType)
              .buildGet()
              .invoke();
    }

    @Override
    public Response createTodoList(TodoListDTO todoList) {
        URI uri = getBaseUrl(TODO_LISTS_PATH).build();
        WebTarget target = client.target(uri);

        Response response = target.request(mediaType)
                .buildPost(Entity.entity(todoList, mediaType, todoList.getClass().getAnnotations()))
                .invoke();
        logger.debug("POST {}\n{}\n=>{}/{}\n"
                + "Location: {}\n"
                + "Content-Location: {}", target.getUri(),
                marshal(todoList),
                response.getStatusInfo(), response.getStatus(),
                response.getHeaderString("Location"),
                response.getHeaderString("Content-Location")
                );
        return response;
    }

    @Override
    public Response getTodoList(String listName) {
        URI uri = getBaseUrl(TODO_LIST_PATH).build(listName);
        WebTarget target = client.target(uri);
        logger.debug("GET {}", target.getUri());

        return target.request(mediaType)
                .buildGet()
                .invoke();
    }

    @Override
    public Response deleteTodoList(String listName) {
        URI uri = getBaseUrl(TODO_LIST_PATH).build(listName);
        WebTarget target = client.target(uri);
        logger.debug("DELETE {}", target.getUri());

        return target.request(mediaType)
                .buildDelete()
                .invoke();
    }

    @Override
    public Response renameTodoList(String oldName, String newName) {
        URI uri = getBaseUrl(TODO_LIST_PATH).build(oldName);
        WebTarget target = client.target(uri)
                .queryParam(NAME_PARAM, newName);
        logger.debug("POST {}", target.getUri());

        return target.request(mediaType)
                .buildPost(null)
                .invoke();
    }

    @Override
    public Response addTodoItem(String listName, TodoItemDTO item) {
        URI uri = getBaseUrl(TODO_LIST_PATH, TODO_ITEMS_PATH).build(listName);
        WebTarget target = client.target(uri);
        logger.debug("POST {}", target.getUri());

        Entity<TodoItemDTO> entity = item==null ?
                null :
                Entity.entity(item, mediaType, item.getClass().getAnnotations());

        return target.request(mediaType)
                .buildPost(entity)
                .invoke();
    }

    @Override
    public Response updateTodoItem(String listName, TodoItemDTO item) {
        URI uri = getBaseUrl(TODO_LIST_PATH, TODO_ITEM_PATH).build(listName, item.getName());
        WebTarget target = client.target(uri);
        logger.debug("PUT {}", target.getUri());

        return target.request(mediaType)
                .buildPut(Entity.entity(item, mediaType, item.getClass().getAnnotations()))
                .invoke();
    }

    @Override
    public Response deleteTodoItem(String listName, String itemName) {
        URI uri = getBaseUrl(TODO_LIST_PATH, TODO_ITEM_PATH).build(listName, itemName);
        WebTarget target = client.target(uri);
        logger.debug("DELETE {}", target.getUri());

        return target.request(mediaType)
                .buildDelete()
                .invoke();
    }
*/
}
