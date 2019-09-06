package info.ejava.examples.svc.springfox.contests.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "This class represents a page of contests.")
public class ContestListDTO {
    @ApiModelProperty(position = 2,
            example = "0",
            notes = "${api.model.ContestListDTO.offset}")
    @JacksonXmlProperty(isAttribute = true)
    private Integer offset;

    @ApiModelProperty(position = 3,
            example = "0",
            notes = "${api.model.ContestListDTO.limit}")
    @JacksonXmlProperty(isAttribute = true)
    private Integer limit;

    @ApiModelProperty(position = 4,
            example = "0",
            notes = "${api.model.ContestListDTO.total}")
    @JacksonXmlProperty(isAttribute = true)
    private Integer total;

    @ApiModelProperty(position = 5,
            example = "homeTeam=Sharks",
            notes = "${api.model.ContestListDTO.keywords}")
    @JacksonXmlProperty(isAttribute = true)
    private String keywords;

    @ApiModelProperty(position = 6,
            notes = "${api.model.ContestListDTO.contests}")
    @JacksonXmlElementWrapper(localName = "contests")
    @JacksonXmlProperty(localName = "contest")
    private List<ContestDTO> contests;

    @ApiModelProperty(position = 1,
            example = "0",
            notes = "${api.model.ContestListDTO.count}")
    @JacksonXmlProperty(isAttribute = true)
    public int getCount() {
        return contests ==null ? 0 : contests.size();
    }
    public void setCount(Integer count) {
        //ignored - count is determined from quotes.size
    }
}
