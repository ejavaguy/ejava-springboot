package info.ejava.examples.svc.content.quotes.dto;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Slf4j
public class Jackson2JaxbTest extends InteropTestBase {
    private JacksonXmlTest jacksonXml = new JacksonXmlTest();
    private JaxbTest jaxbXml = new JaxbTest();

    @BeforeEach
    public void init() {
        jaxbXml.init();
        jacksonXml.init();
    }

    @ParameterizedTest
    @MethodSource("dtos")
    public void jackson2jaxb(Object dto) throws IOException, JAXBException {
        String xml = jacksonXml.marshal(dto);
        Object result = jaxbXml.unmarshal(dto.getClass(), xml);
        Assertions.assertThat(result).isEqualTo(dto);

        if (dto instanceof ADate) {
            compareTimes((ADate) dto, (ADate) result);
        }
    }

    @ParameterizedTest
    @MethodSource("dtos")
    public void jaxb2jackson(Object dto) throws IOException, JAXBException {
        String xml = jaxbXml.marshal(dto);
        Object result = jacksonXml.unmarshal(dto.getClass(), xml);
        Assertions.assertThat(result).isEqualTo(dto);

        if (dto instanceof ADate) {
            compareTimes((ADate) dto, (ADate) result);
        }
    }
}
