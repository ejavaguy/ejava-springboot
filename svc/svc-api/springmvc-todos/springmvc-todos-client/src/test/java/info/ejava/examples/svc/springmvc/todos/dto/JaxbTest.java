package info.ejava.examples.svc.springmvc.todos.dto;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

@Slf4j
public class JaxbTest extends MarshallingTest {

    @Override
    protected <T> String marshal(T object) throws JAXBException {
        if (object==null) { return ""; }
        
        JAXBContext jbx = JAXBContext.newInstance(object.getClass());
        
        Marshaller marshaller = jbx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);        
        StringWriter buffer = new StringWriter();
        marshaller.marshal(object, buffer);
        log.info("{} toXML: {}", object, buffer);
        return buffer.toString();        
    }

    @Override
    protected <T> T demarshal(Class<T> type, String buffer) throws JAXBException {
        if (buffer==null) { return null; }
        
        JAXBContext jbx = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = jbx.createUnmarshaller();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(buffer.getBytes());
        @SuppressWarnings("unchecked")
        T result = (T) unmarshaller.unmarshal(bis);
        log.info("{} fromJSON: {}", buffer, result);
        return result;
    }
}
