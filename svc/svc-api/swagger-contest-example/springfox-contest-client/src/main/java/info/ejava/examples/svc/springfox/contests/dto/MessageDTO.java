package info.ejava.examples.svc.springfox.contests.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description="This class is used to convey a generic message -- usually the result " +
        "of an error.")
public class MessageDTO {
    @ApiModelProperty(position=0,
        notes="The URL that generated the message.")
    private String url;
    @ApiModelProperty(position=0,
            notes="The text of the message.")
    private String text;
    @ApiModelProperty(position=0,
            notes="The date (UTC) of the generated message.")
    private Instant date;
}
