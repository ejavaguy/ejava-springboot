package info.ejava.examples.svc.springdoc.contests.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JacksonXmlRootElement(localName = "contests", namespace = ContestDTO.CONTEST_NAMESPACE)
/**
 * This swagger model class example is using property files to supply the
 * individual property descriptions. Swagger, for some unknown reason, does
 * not yet support defining the class description using properties and must
 * be defined here.
 */
@Schema(description = "This class represents a page of contests.")
public class ContestListDTO {
    @Schema(//position = 2,
            example = "0",
            description = "Offset from the beginning of the collection of contests on the server.")
    @JacksonXmlProperty(isAttribute = true)
    private Integer offset;

    @Schema(//position = 3,
            example = "0",
            description = "Maximum number of elements the page was originally requested to have.")
    @JacksonXmlProperty(isAttribute = true)
    private Integer limit;

    @Schema(//position = 4,
            example = "0",
            description = "Total number of contests that are available, that matched the original " +
                    "query but may not have been returned because of paging limits set.")
    @JacksonXmlProperty(isAttribute = true)
    private Integer total;

    @Schema(//position = 5,
            example = "homeTeam=Sharks",
            description = "Used as a description of the query used to obtain the contests.")
    @JacksonXmlProperty(isAttribute = true)
    private String keywords;

    @Schema(//position = 6,
            description = "${api.model.ContestListDTO.contests}")
    @JacksonXmlElementWrapper(localName = "contests")
    @JacksonXmlProperty(localName = "contest")
    private List<ContestDTO> contests;

    @Schema(//position = 1,
            example = "0",
            description = "The number of elements in the contest field.")
    @JacksonXmlProperty(isAttribute = true)
    public int getCount() {
        return contests ==null ? 0 : contests.size();
    }
    public void setCount(Integer count) {
        //ignored - count is determined from quotes.size
    }
}
