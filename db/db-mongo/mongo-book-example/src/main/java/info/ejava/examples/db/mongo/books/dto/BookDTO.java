package info.ejava.examples.db.mongo.books.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JacksonXmlRootElement(localName = "book", namespace = "urn:ejava.db-repo.books")
@XmlRootElement(name = "book", namespace = "urn:ejava.db-repo.books")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    @JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private String id;
    private String title;
    private String author;
    @XmlJavaTypeAdapter(LocalDateJaxbAdapter.class)
    private LocalDate published;

    public static class LocalDateJaxbAdapter extends XmlAdapter<String, LocalDate> {
        @Override
        public LocalDate unmarshal(String text) {
            return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        @Override
        public String marshal(LocalDate timestamp) {
            return DateTimeFormatter.ISO_LOCAL_DATE.format(timestamp);
        }
    }
}


