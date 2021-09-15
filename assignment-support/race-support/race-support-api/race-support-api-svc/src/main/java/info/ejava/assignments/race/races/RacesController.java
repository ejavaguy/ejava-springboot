package info.ejava.assignments.race.races;

import info.ejava.assignments.race.client.races.RaceDTO;
import info.ejava.assignments.race.client.races.RacesAPI;
import info.ejava.assignments.race.client.races.RaceListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@ConditionalOnBean(RacesService.class)
@RequiredArgsConstructor
public class RacesController {
    private final RacesService racesService;

    @RequestMapping(path= RacesAPI.RACES_PATH,
        method = RequestMethod.POST,
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RaceDTO> createRace(@RequestBody RaceDTO newRace) {
        RaceDTO createdRace = racesService.createRace(newRace);


        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath(RacesAPI.RACE_PATH)
                .build(createdRace.getId());
        ResponseEntity<RaceDTO> response = ResponseEntity.created(uri).body(createdRace);
        return response;
    }

    @RequestMapping(path=RacesAPI.RACES_PATH,
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RaceListDTO> getRaces(
            @RequestParam(name="pageSize", required = false) Integer pageSize,
            @RequestParam(name="pageNumber", required = false) Integer pageNumber
    ) {
        RaceListDTO races = racesService.getRaces(pageSize, pageNumber);

        ResponseEntity<RaceListDTO> response = ResponseEntity.ok(races);
        return response;
    }

    @RequestMapping(path=RacesAPI.RACE_PATH,
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RaceDTO> getRace(@PathVariable("id") String id) {
        RaceDTO race = racesService.getRace(id);

        ResponseEntity<RaceDTO> response = ResponseEntity.ok(race);
        return response;
    }

    @RequestMapping(path=RacesAPI.RACE_PATH,
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RaceDTO> updateRace(
            @PathVariable("id") String id,
            @RequestBody RaceDTO updateRace) {
        RaceDTO race = racesService.updateRace(id, updateRace);

        ResponseEntity<RaceDTO> response = ResponseEntity.ok(race);
        return response;
    }

    @RequestMapping(path=RacesAPI.RACE_CANCELLATION_PATH,
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RaceDTO> cancelRace(@PathVariable("id") String id) {
        RaceDTO race = racesService.cancelRace(id);

        ResponseEntity<RaceDTO> response = ResponseEntity.ok(race);
        return response;
    }

    @RequestMapping(path=RacesAPI.RACE_PATH,
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRace(@PathVariable("id") String id) {
        racesService.deleteRace(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path=RacesAPI.RACES_PATH,
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllRaces() {
        racesService.deleteAllRaces();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }
}
