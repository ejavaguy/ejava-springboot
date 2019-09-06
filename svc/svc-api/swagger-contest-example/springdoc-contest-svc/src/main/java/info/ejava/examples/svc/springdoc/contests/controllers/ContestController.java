package info.ejava.examples.svc.springdoc.contests.controllers;

import info.ejava.examples.svc.springdoc.contests.api.ContestAPI;
import info.ejava.examples.svc.springdoc.contests.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contests.dto.ContestListDTO;
import info.ejava.examples.svc.springdoc.contests.dto.MessageDTO;
import info.ejava.examples.svc.springdoc.contests.svc.ContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name="contest-controller", description = "manages contests")
@RestController
public class ContestController {
    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @Operation(summary="This endpoint will create a new contest. Home and Away teams are required, " +
            "id is ignored, and most other fields are optional.",
            responses = {
                @ApiResponse(responseCode="201", description="The contest has been successfully created and an ID assigned.",
                    content = @Content(schema = @Schema(implementation=ContestDTO.class))),
                @ApiResponse(responseCode="422", description="An invalid property value was provided in the contest.",
                        content = @Content(schema = @Schema(implementation= MessageDTO.class))),
            })
    @RequestMapping(method = RequestMethod.POST,
        path= ContestAPI.CONTESTS_PATH,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ContestDTO> createContest(
            @Parameter(description="The new contest to be added.",
                    content = @Content(examples = {
                            @ExampleObject("{'id'=0}"),
                            @ExampleObject("<contest id=\"0\"></contest>"),
                    }))
            @RequestBody ContestDTO newContest) {

        ContestDTO createdContest = contestService.createContest(newContest);

        URI url = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(ContestAPI.CONTEST_PATH)
                .build(createdContest.getId());
        ResponseEntity<ContestDTO> response = ResponseEntity.created(url)
                .body(createdContest);
         return response;
    }




    @Operation(summary="${api.controllers.ContestControllers.getContests.description}",
        responses = {
            @ApiResponse(responseCode="200", description="The contests requested are returned with page metadata assigned.")
        })
    @RequestMapping(method = RequestMethod.GET,
        path=ContestAPI.CONTESTS_PATH,
        produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ContestListDTO> getContests(
            @Parameter(description="${api.controllers.ContestControllers.getContests.param.offset}",
                    example = "0",
                    required = false,
                    allowEmptyValue = false)
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @Parameter(description="optional, server will set to no limit if not supplied or 0",
                    example = "0",
                    required = false,
                    allowEmptyValue = false)
            @RequestParam(name = "limit", defaultValue = "0") int limit) {

        ContestListDTO contests = contestService.getContests(offset, limit);

        String url = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        ResponseEntity<ContestListDTO> response = ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_LOCATION, url)
            .body(contests);
        return response;
    }

    @Operation(summary="Returns the specified contest.",
        responses = {
            @ApiResponse(responseCode="200", description="The contest requested is returned."),
            @ApiResponse(responseCode="404", description="The contest requested could not be found.",
                content = @Content(schema=@Schema(implementation = MessageDTO.class)))
        })
    @RequestMapping(method = RequestMethod.GET,
        path=ContestAPI.CONTEST_PATH,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ContestDTO> getContest(@PathVariable("contestId") int id) {
        ContestDTO contest = contestService.getContest(id);

        String url = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        ResponseEntity<ContestDTO> response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, url)
                .body(contest);
        return response;
    }


    @Operation(summary="This method simply checks whether the contest exists.",
        responses = {
            @ApiResponse(responseCode="200", description = "The specified contest exists."),
            @ApiResponse(responseCode="404", description = "The specified contest does not exist.")
        })
    @RequestMapping(method = RequestMethod.HEAD,
            path=ContestAPI.CONTEST_PATH,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> doesContestExist(
            @Parameter(description="id of contest", example = "1", required = true)
            @PathVariable("contestId") int id) {

        boolean exists = contestService.doesContestExist(id);

        ResponseEntity<Void> response = exists ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
        return response;
    }

    @Operation(summary="Update an existing contest -- perhaps to schedule it or report scores.",
        responses = {
                @ApiResponse(responseCode="200", description="The specified contest was successfully updated."),
                @ApiResponse(responseCode="404", description="The specified contest did not exist.",
                        content = @Content(schema = @Schema(implementation = MessageDTO.class))
                ),
                @ApiResponse(responseCode="500", description="There was an error implementing the update.",
                        content = @Content(schema = @Schema(implementation = MessageDTO.class))
                ),
        })
    @RequestMapping(method = RequestMethod.PUT,
        path=ContestAPI.CONTEST_PATH,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> updateContest(
            @Parameter(description="id of contest", example = "1", required = true)
            @PathVariable(name="contestId") int id,
            @RequestBody ContestDTO contestUpdate) {

        contestService.updateContest(id, contestUpdate);

        ResponseEntity<Void> response = ResponseEntity.ok().build();
        return response;
    }

    @Operation(summary="This method will delete the specified contest. There " +
            "is no error if the ID does not exist. There will only be an error when " +
            "we fail to delete it.")
    @ApiResponses({
            @ApiResponse(responseCode="204", description="Specified contest was deleted or it did not exist."),
            @ApiResponse(responseCode="500", description="There was an error implementing the delete.",
                    content = @Content(schema = @Schema(implementation = MessageDTO.class))
            ),
    })
    @RequestMapping(method = RequestMethod.DELETE,
        path=ContestAPI.CONTEST_PATH)
    public ResponseEntity<Void> deleteContest(
            @Parameter(description="id of contest", example = "1", required = true)
            @PathVariable(name="contestId") int id) {
        contestService.deleteContest(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @Operation(summary="This method will delete all contests.")
    @ApiResponses({
            @ApiResponse(responseCode="204", description="All contests were deleted."),
            @ApiResponse(responseCode="500", description="There was an error implementing the delete.",
                    content = @Content(schema = @Schema(implementation = MessageDTO.class))
            ),
    })
    @RequestMapping(method = RequestMethod.DELETE,
            path=ContestAPI.CONTESTS_PATH)
    public ResponseEntity<Void> deleteAllContests() {
        contestService.deleteAllContests();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }
}
