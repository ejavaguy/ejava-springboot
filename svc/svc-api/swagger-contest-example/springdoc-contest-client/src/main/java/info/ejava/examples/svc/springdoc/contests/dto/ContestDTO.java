package info.ejava.examples.svc.springdoc.contests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

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
@Schema(description="This class describes a contest between a home and away team, " +
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
    @Schema(description = "A read-only ID assigned by the service during create.",
            example = "0", //valid integer example required to avoid parse stack trace at startup
            accessMode = READ_ONLY)
    private int id;

    @JsonProperty(required = false)
    @Schema(//position = 3,
            description = "Scheduled or completed contests should have a scheduled started time " +
                    "expressed in LocalDateTime with offset from UTC.")
    @EqualsAndHashCode.Exclude
    private OffsetDateTime scheduledStart;

    @JsonProperty(required = false)
    @Schema(//position = 4,
            example = "PT60M",
            description = "Each scheduled contest should have a period of time specified that " +
                    "identifies the duration of the contest. e.g., PT60M, PT2H")
    private Duration duration;

    @JsonProperty(required = true)
    @Schema(//position = 5,
            description = "This value will be true when the contest is completed and the scores " +
                    "reported can be considered final.")
    private boolean completed;

    @JsonProperty(required = true)
    @Schema(//position = 1,
            description = "Name of the home team in the contest.")
    private String homeTeam;

    @JsonProperty(required = true)
    @Schema(//position = 2,
            description = "Name of the away team in the contest.")
    private String awayTeam;

    @JsonProperty(required = false)
    @Schema(//position = 5,
            example = "2",
            description = "Home team score. This will not be valid and may not be " +
                    "supplied prior to the scheduled start time and not complete " +
                    "until the completed field is set to true.")
    private Integer homeScore;

    @JsonProperty(required = false)
    @Schema(//position = 6,
            example = "1",
            description = "Away team score. This will not be valid and may not be " +
                    "supplied prior to the scheduled start time and not complete " +
                    "until the completed field is set to true.")
    private Integer awayScore;

    @EqualsAndHashCode.Include
    private Instant getScheduledStartAsInstant() {
        return scheduledStart == null ? null : scheduledStart.toInstant();
    }
}
