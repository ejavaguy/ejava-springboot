package info.ejava.examples.svc.content.quotes.dto;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JacksonXmlTest extends MarshallingTestBase {
    private XmlMapper mapper;

    @Override
    @BeforeEach
    public void init() {
        mapper = new Jackson2ObjectMapperBuilder()
                .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
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

    @Override
    protected String get_marshalled_adate(String dateText) {
        return String.format(DATES_XML,dateText);
    }


    @Override
    public String get_date(String marshalledQuote) {
        Pattern pattern = Pattern.compile(".*<date xmlns=\"\">(.+)<\\/date>.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(marshalledQuote);

        if (matcher.matches()) {
            String date = matcher.group(1);
            return date;
        }
        return null;
    }


    @Override
    protected boolean canParseFormat(String format, ZoneOffset tzo) {
        return tzo==ZoneOffset.UTC;
    }
}
