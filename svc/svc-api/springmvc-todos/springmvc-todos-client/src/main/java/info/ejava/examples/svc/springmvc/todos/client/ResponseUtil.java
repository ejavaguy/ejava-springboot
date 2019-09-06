package info.ejava.examples.svc.springmvc.todos.client;

public class ResponseUtil {
    /*
    static <T> T getEntity(Response response, Class<T> type) {
        if (Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            return response.readEntity(type, type.getAnnotations());
        } else {
            throw new IllegalStateException(String.format("error response[%d %s]: %s",
                    response.getStatus(),
                    response.getStatusInfo(),
                    response.readEntity(String.class))
                    );
        }
    }

    static <T> void assertSuccess(String message, Response response) {
        if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            throw new IllegalStateException(String.format(message + ", error response[%d %s]: %s",
                    response.getStatus(),
                    response.getStatusInfo(),
                    response.readEntity(String.class))
                    );            
        } else {
            response.close();
        }
    }
    */
}
