package info.ejava.examples.svc.content.quotes.dto;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JaxbTest extends MarshallingTestBase {
    @Override
    public <T> String marshal(T object) throws JAXBException {
        if (object==null) { return ""; }
        
        JAXBContext jbx = JAXBContext.newInstance(object.getClass());
        
        Marshaller marshaller = jbx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter buffer = new StringWriter();
        marshaller.marshal(object, buffer);
        log.info("{} toXml: {}", object, buffer);
        return buffer.toString();        
    }

    @Override
    public <T> T unmarshal(Class<T> type, String buffer) throws JAXBException {
        if (buffer==null) { return null; }
        
        JAXBContext jbx = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = jbx.createUnmarshaller();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(buffer.getBytes(StandardCharsets.UTF_8));
        T result = (T) unmarshaller.unmarshal(bis);
        log.info("{} fromXml: {}", buffer, result);
        return result;
    }

    @Override
    protected String get_marshalled_adate(String dateText) {
        return String.format(DATES_XML,dateText);
    }

    @Override
    public String get_date(String marshalledQuote) {
        Pattern pattern = Pattern.compile(".*<date>(.+)<\\/date>.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(marshalledQuote);

        if (matcher.matches()) {
            String date = matcher.group(1);
            return date;
        }
        return null;
    }

    @Override
    protected boolean canParseFormat(String format, ZoneOffset tzo) {
        //can parse Z and +05:00 but cannot parse +05
//        if (format.equals(ISO_8601_DATETIME5_FORMAT) ||
//                (tz!=null && tz.getID().equals(TimeZone.getTimeZone("UTC").getID()))) {
//            return true;
//        }
//        return false;
        return true;
    }

}
