package info.ejava.examples.svc.springmvc.todos.client;

import org.junit.jupiter.api.Test;

public class GreetingsNTest {
    @Test
    public void test() {}
/*
    private static final Logger logger = LoggerFactory.getLogger(GreetingsIT.class);
    private static final String baseHttpUrlString = TodoListsIT.getITProperty("url.base.http", "http://localhost:8080"); 
    
    URI baseTodosUrl = UriBuilder.fromPath(baseHttpUrlString).path("ejavaTodos/controllers").build();
    Client client;

    @Before
    public void setUp() throws NamingException {
        client = ClientBuilder.newClient();
    }
    
    @Test
    public void clientBasics() {
            //determine the baseUrl for the application
        String baseHttpUrlString = TodoListsIT.getITProperty("url.base.http", "http://localhost:8080");
            //build a URL to the specific resource  
        URI resourceUrl = javax.ws.rs.core.UriBuilder.fromUri(baseHttpUrlString)
                //.path("") //using root resource - no need for extra path
                .build();
        
            //construct a JAX-RS client 
        javax.ws.rs.client.Client client = javax.ws.rs.client.ClientBuilder.newClient();
        {
            //create the WebTarget to represent intended resource
        javax.ws.rs.client.WebTarget target = client.target(resourceUrl);
            //create an overall request, indicating Acceptable response types
        javax.ws.rs.client.Invocation.Builder request = target.request(MediaType.TEXT_HTML_TYPE);
            //create a method-specific request
        javax.ws.rs.client.Invocation method = request.buildGet();
            //invoke the request and get an actual response from remote resource
        javax.ws.rs.core.Response response = method.invoke();
            //interpret response
        javax.ws.rs.core.Response.StatusType status = response.getStatusInfo();
        int statusCode = response.getStatus();
            //get response entity
        String content = response.readEntity(String.class);
        
        logger.debug("GET {} => {}/{}", target.getUri(), response.getStatus(), response.getStatusInfo());
        
        assertEquals("unexpected statusType", Status.OK, status);
        assertEquals("unexpected statusCode", 200, statusCode);
        assertNotEquals("unexpected size", 0, content.length());
        }
        
            //with a little short-hand
        {
        WebTarget target = client.target(resourceUrl);
        Response response = target.request(MediaType.TEXT_HTML_TYPE)
                .buildGet()
                .invoke();
        logger.debug("GET {} => {}/{}", target.getUri(), response.getStatus(), response.getStatusInfo());
        
        String content = response.readEntity(String.class);
        
        assertEquals("unexpected statusType", Status.Family.SUCCESSFUL, response.getStatusInfo().getFamily());
        assertEquals("unexpected statusType", Status.OK, response.getStatusInfo());
        assertNotEquals("unexpected size", 0, content.length());
        }
             
    }
    
    @Test
    public void sayHi() {
        URI uri = UriBuilder.fromUri(baseTodosUrl)
                            .path("greetings")
                            .path("hi")
                            .build();
        WebTarget target = client.target(uri);
        Response response = target.request(MediaType.TEXT_PLAIN_TYPE)
                                  .get();
        logger.info("GET {} => {}", target.getUri(), response.getStatusInfo());
        String greeting = response.readEntity(String.class);
        logger.info("{}", greeting);
        assertEquals("unexpected status", Status.OK, response.getStatusInfo());
        assertEquals("unexpected greeting", "hi", greeting);
    }
    
    @Test
    public void greetBadRequest() {
        URI uri = UriBuilder.fromUri(baseTodosUrl)
                .path("greetings")
                .path("greet")
                .build();
        WebTarget target = client.target(uri);
        Response response = target.request(MediaType.TEXT_PLAIN_TYPE)
                      .get();
        logger.info("GET {} => {}/{}", target.getUri(), response.getStatus(), response.getStatusInfo());
        String greeting = response.readEntity(String.class);
        logger.info("{}", greeting);
        assertEquals("unexpected status", Status.BAD_REQUEST, response.getStatusInfo());        
    }
    
    @Test
    public void greetOK() {
        URI uri = UriBuilder.fromUri(baseTodosUrl)
                .path("greetings")
                .path("greet")
                .build();
        WebTarget target = client.target(uri)
                .queryParam("name", "ejava");
        Response response = target.request(MediaType.TEXT_PLAIN_TYPE)
                      .get();
        logger.info("GET {} => {}", target.getUri(), response.getStatusInfo());
        String greeting = response.readEntity(String.class);
        logger.info("{}", greeting);
        assertEquals("unexpected status", Status.OK, response.getStatusInfo());        
        assertEquals("unexpected greeting", "hello ejava", greeting);        
    }

 */
}
