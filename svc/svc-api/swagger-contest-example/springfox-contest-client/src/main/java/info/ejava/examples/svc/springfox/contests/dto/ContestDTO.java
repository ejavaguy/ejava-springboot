package info.ejava.examples.svc.springfox.contests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@JacksonXmlRootElement(localName = "contest", namespace = ContestDTO.CONTEST_NAMESPACE)
/**
 * This swagger model class example will express all fields within this class file
 * and not through any properties.
 */
@ApiModel(description="This class describes a contest between a home and away team, " +
        "either in the past or future.")
public class ContestDTO {
    public static final String CONTEST_NAMESPACE = "urn:ejava.svc-swagger.contests";
    @JacksonXmlProperty(isAttribute = true)
    /**
     * I noticed that as soon as you add a Swagger annotation to a class model
     * property, it wants to create an instance of that property using the
     * annotation example field -- which is by default an empty String. For
     * int and Integer, that caused a parsing problem because the API spec generation
     * wanted to parse an integer from the example.
     */
    @ApiModelProperty(value = "A read-only ID assigned by the service during create.",
            example = "0", //valid integer example required to avoid parse stack trace at startup
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private int id;

    @JsonProperty(required = false)
    @ApiModelProperty(position = 3,
            value = "Scheduled or completed contests should have a scheduled started time " +
                    "expressed in LocalDateTime with offset from UTC.")
    @EqualsAndHashCode.Exclude
    private OffsetDateTime scheduledStart;

    @JsonProperty(required = false)
    @ApiModelProperty(position = 4,
            example = "PT60M",
            value = "Each scheduled contest should have a period of time specified that " +
                    "identifies the duration of the contest. e.g., PT60M, PT2H")
    private Duration duration;

    @JsonProperty(required = true)
    @ApiModelProperty(position = 5,
            value = "This value will be true when the contest is completed and the scores " +
                    "reported can be considered final.")
    private boolean completed;

    @JsonProperty(required = true)
    @ApiModelProperty(position = 1,
            value = "Name of the home team in the contest.")
    private String homeTeam;

    @JsonProperty(required = true)
    @ApiModelProperty(position = 2,
            value = "Name of the away team in the contest.")
    private String awayTeam;

    @JsonProperty(required = false)
    @ApiModelProperty(position = 5,
            example = "2",
            value = "Home team score. This will not be valid and may not be " +
                    "supplied prior to the scheduled start time and not complete " +
                    "until the completed field is set to true.")
    private Integer homeScore;

    @JsonProperty(required = false)
    @ApiModelProperty(position = 6,
            example = "1",
            value = "Away team score. This will not be valid and may not be " +
                    "supplied prior to the scheduled start time and not complete " +
                    "until the completed field is set to true.")
    private Integer awayScore;

    @EqualsAndHashCode.Include
    private Instant getScheduledStartAsInstant() {
        return scheduledStart == null ? null : scheduledStart.toInstant();
    }
}
