package info.ejava.examples.common.web;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Slf4j
public class RestTemplateLoggingFilter implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        if (log.isDebugEnabled()) {
            HttpMethod method = request.getMethod();
            URI uri = request.getURI();
            HttpStatus status = response.getStatusCode();
            String requestBody = new String(body);
            String responseBody = getResponseBody(response);

            //https://stackoverflow.com/q/1883345
            String message = String.format("%n%s %s, returned %s/%s%nsent: %s%n%s%nrcvd: %s%n%s",
                    method, uri, status.name(), status.value(),
                    request.getHeaders(), requestBody,
                    response.getHeaders(), responseBody);
            log.debug(message);
        }

        return response;
    }

    protected String getResponseBody(ClientHttpResponse response) {
        try {
            return readString(response.getBody());
        } catch (Exception ex){
            //may fail if having trouble reading stream
            return "";
        }
    }

    /**
     * @see https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
     * @param inputStream
     * @return input stream as one string
     */
    @SneakyThrows
    private String readString(InputStream inputStream) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}
