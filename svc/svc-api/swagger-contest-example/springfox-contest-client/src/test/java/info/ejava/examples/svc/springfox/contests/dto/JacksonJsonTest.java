package info.ejava.examples.svc.springfox.contests.dto;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import info.ejava.examples.common.time.ISODateFormat;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.io.StringWriter;

@Slf4j
public class JacksonJsonTest extends MarshallingTestBase {
    private ObjectMapper mapper;

    @Override
    @BeforeEach
    public void init() {
        mapper = new Jackson2ObjectMapperBuilder()
                .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .dateFormat(new ISODateFormat())
                .createXmlMapper(false)
                .build();
    }

    @Override
    public <T> String marshal(T object)
            throws JsonGenerationException, JsonMappingException, IOException {
        StringWriter buffer = new StringWriter();
        mapper.writeValue(buffer, object);
        log.info("{} toJSON: {}", object, buffer);
        return buffer.toString();
    }

    @Override
    public <T> T unmarshal(Class<T> type, String buffer)
            throws JsonParseException, JsonMappingException, IOException {
        T result = mapper.readValue(buffer, type);
        log.info("{} fromJSON: {}", buffer, result);
        return result;
    }
}
