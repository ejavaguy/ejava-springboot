package info.ejava.examples.svc.content.quotes.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "quotes", namespace = "urn:ejava.svc-controllers.quotes")
@XmlType()
@XmlAccessorType(XmlAccessType.NONE)
@JacksonXmlRootElement(localName = "quotes", namespace = "urn:ejava.svc-controllers.quotes")
public class QuoteListDTO {
    @XmlAttribute(required = false)
    private Integer offset;
    @XmlAttribute(required = false)
    private Integer limit;
    @XmlAttribute(required = false)
    private Integer total;
    @XmlAttribute(required = false)
    private String keywords;

    @XmlElementWrapper(name="quotes")
    @XmlElement(name="quote")
    @JacksonXmlElementWrapper(localName = "quotes")
    @JacksonXmlProperty(localName = "quote")
    private List<QuoteDTO> quotes;


    @XmlAttribute(required = false)
    public int getCount() {
        return quotes==null ? 0 : quotes.size();
    }
    public void setCount(Integer count) {
        //ignored - count is determined from quotes.size
    }
}
