package info.ejava.examples.svc.springdoc.contests.dto;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import info.ejava.examples.common.time.ISODateFormat;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.io.StringWriter;

@Slf4j
public class JacksonXmlTest extends MarshallingTestBase {
    private XmlMapper mapper;

    @Override
    @BeforeEach
    public void init() {
        mapper = new Jackson2ObjectMapperBuilder()
                .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .dateFormat(new ISODateFormat())
                .createXmlMapper(true)
                .build();
    }

    @Override
    public <T> String marshal(T object) throws IOException {
        StringWriter buffer = new StringWriter();
        mapper.writeValue(buffer, object);
        log.info("{} toXML: {}", object, buffer);
        return buffer.toString();
    }

    @Override
    public <T> T unmarshal(Class<T> type, String buffer) throws IOException {
        T result = mapper.readValue(buffer, type);
        log.info("{} fromXML: {}", buffer, result);
        return result;
    }
}
