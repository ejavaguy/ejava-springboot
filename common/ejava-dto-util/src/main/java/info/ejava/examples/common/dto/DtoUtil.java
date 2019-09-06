package info.ejava.examples.common.dto;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public abstract class DtoUtil {
    public static final String DATE_FORMAT="yyyy-MM-dd";
    public static final String DATETIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss.SSSX";


    //String
    public <T> String marshal(T object) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        marshal(object, os);
        return os.toString();
    }
    public <T> T unmarshal(String text, Class<T> type) {
        InputStream is = new ByteArrayInputStream(text.getBytes());
        T object = unmarshal(is, type);
        return object;
    }

    public <T> String marshalThrows(T object) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        marshalThrows(object, os);
        return os.toString();
    }
    public <T> T unmarshalThrows(String text, Class<T> type) throws IOException {
        InputStream is = new ByteArrayInputStream(text.getBytes());
        T object = unmarshalThrows(is, type);
        return object;
    }

    //byte[]
    public <T> byte[] marshalAsBytes(T object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshal(object, bos);
        return bos.toByteArray();
    }
    public <T> T unmarshal(byte[] bytes, Class<T> type) {
        if (log.isDebugEnabled()) {
            log.debug("{}", new String(bytes));
        }
        return unmarshal(new ByteArrayInputStream(bytes), type);
    }


    //core implementation wrappers that do not throw exceptions
    public <T> void marshal(T object, OutputStream os) {
        try {
            marshalThrows(object, os);
        } catch (Exception ex) {
            log.info("{}",ex);
        }
    }
    public <T> T unmarshal(InputStream is, Class<T> type) {
        try {
            return unmarshalThrows(is, type);
        } catch (Exception ex) {
            log.info("{}", ex);
            return null;
        }
    }

    //core implementations
    public abstract <T> void marshalThrows(T object, OutputStream os) throws IOException;
    public abstract <T> T unmarshalThrows(InputStream is, Class<T> type) throws IOException;

    protected void init() {}
}
