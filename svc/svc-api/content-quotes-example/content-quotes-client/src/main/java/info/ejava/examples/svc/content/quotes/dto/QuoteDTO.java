package info.ejava.examples.svc.content.quotes.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import javax.json.bind.annotation.JsonbTransient;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With

@XmlRootElement(name = "quote", namespace = "urn:ejava.svc-controllers.quotes")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "quote", namespace = "urn:ejava.svc-controllers.quotes")
public class QuoteDTO {
    @XmlAttribute
    @JacksonXmlProperty(isAttribute = true)
    private int id;
    private String author;
    private String text;
    @XmlJavaTypeAdapter(JaxbTimeAdapters.LocalDateJaxbAdapter.class)
    private LocalDate date;
    @JsonIgnore
    @JsonbTransient
    @XmlTransient
    private String ignored;
}
