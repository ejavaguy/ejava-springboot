package info.ejava.examples.svc.springmvc.todos.dto;

import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonTest extends MarshallingTest {
    private Logger logger = LoggerFactory.getLogger(JacksonTest.class);
    
    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected <T> String marshal(T object) 
            throws JsonGenerationException, JsonMappingException, IOException {
        StringWriter buffer = new StringWriter();
        mapper.writeValue(buffer, object);
        logger.info("{} toJSON: {}", object, buffer);
        return buffer.toString();
    }

    @Override
    protected <T> T demarshal(Class<T> type, String buffer) 
            throws JsonParseException, JsonMappingException, IOException {
        T result = mapper.readValue(buffer, type);
        logger.info("{} fromJSON: {}", buffer, result);
        return result;
    }
}
