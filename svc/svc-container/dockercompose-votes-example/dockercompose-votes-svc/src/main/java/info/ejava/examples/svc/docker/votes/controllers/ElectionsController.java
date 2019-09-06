package info.ejava.examples.svc.docker.votes.controllers;

import info.ejava.examples.svc.docker.votes.dto.ElectionResultsDTO;
import info.ejava.examples.svc.docker.votes.services.ElectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionsController {
    private final ElectionsService votesService;

    @GetMapping(value = "counts",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ElectionResultsDTO> getVoteCounts() {
        ElectionResultsDTO result = votesService.getVoteCounts();

        ResponseEntity<ElectionResultsDTO> response = ResponseEntity.ok(result);
        return response;
    }
}
