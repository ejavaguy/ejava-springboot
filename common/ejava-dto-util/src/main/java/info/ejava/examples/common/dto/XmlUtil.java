package info.ejava.examples.common.dto;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class XmlUtil extends DtoUtil {
    protected static XmlUtil instance = new XmlUtil();
    protected XmlMapper mapper = new XmlMapper();

    public XmlUtil() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleModule dateSerialization = new SimpleModule();
        dateSerialization.addDeserializer(Date.class, DateDeserializers.DateDeserializer.instance);
        dateSerialization.addSerializer(Date.class, new DateSerializer(false, sdf));

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());

        mapper.registerModule(dateSerialization);
        init();
    }

    @Override
    public <T> void marshalThrows(T object, OutputStream os) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(os, object);
    }

    @Override
    public <T> T unmarshalThrows(InputStream is, Class<T> type) throws IOException {
        return mapper.readValue(is, type);
    }

    public static XmlUtil instance() { return instance; }
}
