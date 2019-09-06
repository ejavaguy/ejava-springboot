package info.ejava.examples.svc.content.quotes.dto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class Jackson2JsonbTest extends InteropTestBase {
    private JacksonJsonTest jacksonJson = new JacksonJsonTest();
    private JsonbTest jsonbJson = new JsonbTest();

    @BeforeEach
    public void init() {
        jsonbJson.init();
        jacksonJson.init();
    }

    @ParameterizedTest
    @MethodSource("dtos")
    public void jackson2jsonb(Object dto) throws IOException, JAXBException {
        String json = jacksonJson.marshal(dto);
        Object result = jsonbJson.unmarshal(dto.getClass(), json);
        assertThat(result).isEqualTo(dto);

        if (dto instanceof ADate) {
            compareTimes((ADate) dto, (ADate) result);
        }
    }

    @ParameterizedTest
    @MethodSource("dtos")
    public void jsonb2jackson(Object dto) throws IOException, JAXBException {
        String json = jsonbJson.marshal(dto);
        Object result = jacksonJson.unmarshal(dto.getClass(), json);
        assertThat(result).isEqualTo(dto);

        if (dto instanceof ADate) {
            compareTimes((ADate) dto, (ADate) result);
        }
    }
}
