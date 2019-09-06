package info.ejava.examples.svc.springfox.contests.controllers;

import info.ejava.examples.svc.springfox.contests.svc.ContestService;
import info.ejava.examples.svc.springfox.contests.api.ContestAPI;
import info.ejava.examples.svc.springfox.contests.dto.ContestDTO;
import info.ejava.examples.svc.springfox.contests.dto.ContestListDTO;
import info.ejava.examples.svc.springfox.contests.dto.MessageDTO;
import io.swagger.annotations.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Api(value="contest-controller", description = "manages contests")
@RestController
public class ContestController {
    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @ApiOperation(value="This endpoint will create a new contest. Home and Away teams are required, " +
            "id is ignored, and most other fields are optional.",
            response=ContestDTO.class)
    @ApiResponses({
        @ApiResponse(code=201, message="The contest has been successfully created and an ID assigned.",
            response = ContestDTO.class),
        @ApiResponse(code= 422, message="An invalid property value was provided in the contest.",
            response = MessageDTO.class),
    })
    @RequestMapping(method = RequestMethod.POST,
        path= ContestAPI.CONTESTS_PATH,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(code = HttpStatus.CREATED) //needed by swagger to eliminate 200 response
    public ResponseEntity<ContestDTO> createContest(
            @ApiParam(value = "The new contest to be added.",

                examples = @Example(value={
                        @ExampleProperty(mediaType=MediaType.APPLICATION_JSON_VALUE,
                            value="{'id'=0}"),
                        @ExampleProperty(mediaType=MediaType.APPLICATION_XML_VALUE,
                                value="<contest id=\"0\"></contest>"),

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




    @ApiOperation(value = "${api.controllers.ContestControllers.getContests.description}")
    @ApiResponses({
            @ApiResponse(code=200, message="The contests requested are returned with page metadata assigned.",
                    response = ContestListDTO.class),
    })
    @RequestMapping(method = RequestMethod.GET,
        path=ContestAPI.CONTESTS_PATH,
        produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ContestListDTO> getContests(
            @ApiParam(value="${api.controllers.ContestControllers.getContests.param.offset}",
                    example = "0",
                    required = false,
                    allowEmptyValue = false)
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @ApiParam(value="optional, server will set to no limit if not supplied or 0",
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

    @ApiOperation("Returns the specified contest.")
    @ApiResponses({
            @ApiResponse(code=200, message="The contest requested is returned.",
                    response = ContestDTO.class),
            @ApiResponse(code= 404, message="The contest requested could not be found.",
                    response = MessageDTO.class),
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


    @ApiOperation("This method simply checks whether the contest exists.")
    @ApiResponses({
            @ApiResponse(code = 200,message = "The specified contest exists."),
            @ApiResponse(code = 404,message = "The specified contest does not exist."),
    })
    @RequestMapping(method = RequestMethod.HEAD,
            path=ContestAPI.CONTEST_PATH,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> doesContestExist(
            @ApiParam(value = "id of contest", example = "1", required = true)
            @PathVariable("contestId") int id) {

        boolean exists = contestService.doesContestExist(id);

        ResponseEntity<Void> response = exists ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
        return response;
    }

    @ApiOperation(value = "Update an existing contest -- perhaps to schedule it or " +
            "report scores.")
    @ApiResponses({
            @ApiResponse(code=200, message="The specified contest was successfully updated."),
            @ApiResponse(code=404, message="The specified contest did not exist."),
            @ApiResponse(code=500, message="There was an error implementing the update.",
                    response = MessageDTO.class),
    })
    @ResponseStatus(code=HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT,
        path=ContestAPI.CONTEST_PATH,
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> updateContest(
            @ApiParam(value = "id of contest", example = "1", required = true)
            @PathVariable(name="contestId") int id,
            @RequestBody ContestDTO contestUpdate) {

        contestService.updateContest(id, contestUpdate);

        ResponseEntity<Void> response = ResponseEntity.ok().build();
        return response;
    }

    @ApiOperation(value = "This method will delete the specified contest. There " +
            "is no error if the ID does not exist. There will only be an error when " +
            "we fail to delete it.")
    @ApiResponses({
            @ApiResponse(code=204, message="Specified contest was deleted or it did not exist."),
            @ApiResponse(code=500, message="There was an error implementing the delete.",
                    response = MessageDTO.class),
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT) //needed by swagger to eliminate 200 status
    @RequestMapping(method = RequestMethod.DELETE,
        path=ContestAPI.CONTEST_PATH)
    public ResponseEntity<Void> deleteContest(
            @ApiParam(value = "id of contest", example = "1", required = true)
            @PathVariable(name="contestId") int id) {
        contestService.deleteContest(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @ApiOperation(value = "This method will delete all contests.")
    @ApiResponses({
            @ApiResponse(code=204, message="All contests were deleted."),
            @ApiResponse(code=500, message="There was an error implementing the delete.",
                response = MessageDTO.class),
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT) //needed by swagger to eliminate 200 status
    @RequestMapping(method = RequestMethod.DELETE,
            path=ContestAPI.CONTESTS_PATH)
    public ResponseEntity<Void> deleteAllContests() {
        contestService.deleteAllContests();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }
}
