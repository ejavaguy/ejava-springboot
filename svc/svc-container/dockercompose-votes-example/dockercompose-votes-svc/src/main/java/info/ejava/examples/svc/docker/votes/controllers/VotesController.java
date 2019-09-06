package info.ejava.examples.svc.docker.votes.controllers;

import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import info.ejava.examples.svc.docker.votes.services.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VotesController {
    private final VoterService voterService;

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VoteDTO> createVotes(@RequestBody VoteDTO newVote) {
        VoteDTO createdVote = voterService.castVote(newVote);

        URI url = ServletUriComponentsBuilder.fromCurrentRequestUri().path("{id}").build(createdVote.getId());
        ResponseEntity<VoteDTO> response = ResponseEntity.created(url)
                .body(createdVote);
        return response;
    }

    @GetMapping(path = "total",
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> getTotalVotes() {
        long total = voterService.getTotalVotes();

        ResponseEntity<String> response = ResponseEntity.ok(""+total);
        return response;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<VoteDTO>> getVotes(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "0") int limit) {
        Page<VoteDTO> votes = voterService.getVotes(offset, limit);

        ResponseEntity<Page<VoteDTO>> response = ResponseEntity.ok(votes);
        return response;
    }
}
