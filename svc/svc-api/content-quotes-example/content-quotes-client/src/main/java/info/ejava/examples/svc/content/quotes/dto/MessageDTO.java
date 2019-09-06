package info.ejava.examples.svc.content.quotes.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "message", namespace = "urn:ejava.svc-controllers.quotes")
@JacksonXmlRootElement(localName = "message", namespace = "urn:ejava.svc-controllers.quotes")
public class MessageDTO {
    private String url;
    private String text;
}
