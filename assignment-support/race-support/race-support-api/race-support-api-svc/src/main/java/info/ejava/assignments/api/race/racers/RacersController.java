package info.ejava.assignments.api.race.racers;

import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacerListDTO;
import info.ejava.assignments.api.race.client.racers.RacersAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RacersController {
    private final RacersService racersService;

    @PostConstruct
    public void init() {
        log.info("Racers initialized, URI={}", RacersAPI.RACERS_PATH);
    }

    @RequestMapping(path= RacersAPI.RACERS_PATH,
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RacerDTO> createRacer(@RequestBody RacerDTO newRacer) {
        RacerDTO racer = racersService.createRacer(newRacer);

        URI url = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath(RacersAPI.RACER_PATH)
                .build(racer.getId());
        ResponseEntity<RacerDTO> response = ResponseEntity.created(url).body(racer);
        return response;
    }

    @RequestMapping(path=RacersAPI.RACERS_PATH,
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RacerListDTO> getRacers(
            @RequestParam(name="pageSize", required = false) Integer pageSize,
            @RequestParam(name="pageNumber", required = false) Integer pageNumber
    ) {
        RacerListDTO racers = racersService.getRacers(pageSize, pageNumber);

        ResponseEntity<RacerListDTO> response = ResponseEntity.ok(racers);
        return response;
    }

    @RequestMapping(path=RacersAPI.RACER_PATH,
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RacerDTO> getRacer(@PathVariable("id") String id) {
        RacerDTO racer = racersService.getRacer(id);

        ResponseEntity<RacerDTO> response = ResponseEntity.ok(racer);
        return response;
    }

    @RequestMapping(path=RacersAPI.RACER_PATH,
            method = RequestMethod.PUT,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RacerDTO> updateRacer(
            @PathVariable("id") String id,
            @RequestBody RacerDTO racerUpdate) {
        RacerDTO updatedRacer = racersService.updateRacer(id, racerUpdate);

        ResponseEntity<RacerDTO> response = ResponseEntity.ok(updatedRacer);
        return response;
    }

    @RequestMapping(path=RacersAPI.RACER_PATH,
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteRacer(@PathVariable("id") String id) {
        racersService.deleteRacer(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path=RacersAPI.RACERS_PATH,
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllRacers() {
        racersService.deleteAllRacers();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }
}
