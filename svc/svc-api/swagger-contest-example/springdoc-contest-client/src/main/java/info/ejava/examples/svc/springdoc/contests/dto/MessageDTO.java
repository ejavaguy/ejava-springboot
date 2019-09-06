package info.ejava.examples.svc.springdoc.contests.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JacksonXmlRootElement(localName = "message", namespace = ContestDTO.CONTEST_NAMESPACE)
@Schema(description="This class is used to convey a generic message -- usually the result " +
        "of an error.")
public class MessageDTO {
    @Schema(//position=0,
            description="The URL that generated the message.")
    private String url;
    @Schema(//position=0,
            description="The text of the message.")
    private String text;
    @Schema(//position=0,
            description="The date (UTC) of the generated message.")
    private Instant date;
}
