package info.ejava.examples.common.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JacksonXmlRootElement(localName = "message", namespace = MessageDTO.NAMESPACE)
public class MessageDTO {
    public static final String NAMESPACE="urn:ejava.util-dto";

    @JacksonXmlProperty(namespace = NAMESPACE)
    private String url;
    @JacksonXmlProperty(namespace = NAMESPACE)
    private int statusCode;
    @JacksonXmlProperty(namespace = NAMESPACE)
    private String statusName;
    @JacksonXmlProperty(namespace = NAMESPACE)
    private String message;
    @JacksonXmlProperty(namespace = NAMESPACE)
    private String description;
    @JacksonXmlProperty(namespace = NAMESPACE)
    private Instant date;
}
