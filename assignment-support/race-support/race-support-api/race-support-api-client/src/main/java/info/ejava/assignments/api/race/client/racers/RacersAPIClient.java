package info.ejava.assignments.api.race.client.racers;

import info.ejava.examples.common.web.ServerConfig;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class RacersAPIClient implements RacersAPI {
    private final URI baseUrl;
    private final RestTemplate restTemplate;
    private final MediaType mediaType;

    public RacersAPIClient(RestTemplate restTemplate, ServerConfig serverConfig, MediaType mediaType) {
        this.restTemplate = restTemplate;
        this.baseUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl()).build().toUri();
        this.mediaType = mediaType;
    }

    @Override
    public ResponseEntity<RacerDTO> createRacer(RacerDTO newRace) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACERS_PATH).build().toUri();

        RequestEntity<RacerDTO> request = RequestEntity.post(url)
                .accept(mediaType)
                .contentType(mediaType)
                .body(newRace);
        ResponseEntity<RacerDTO> response = restTemplate.exchange(request, RacerDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<RacerListDTO> getRacers(Integer pageSize, Integer pageNumber) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(baseUrl).path(RACERS_PATH);
        if (pageSize!=null) {
            uriBuilder = uriBuilder.queryParam("pageSize", pageSize);
        }
        if (pageNumber!=null) {
            uriBuilder = uriBuilder.queryParam("pageNumber", pageNumber);
        }
        URI url = uriBuilder.build().toUri();

        RequestEntity<Void> request = RequestEntity.get(url).accept(mediaType).build();
        ResponseEntity<RacerListDTO> response = restTemplate.exchange(request, RacerListDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<RacerDTO> getRacer(String id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACER_PATH).build(id);

        RequestEntity<Void> request = RequestEntity.get(url).accept(mediaType).build();
        ResponseEntity<RacerDTO> response = restTemplate.exchange(request, RacerDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<RacerDTO> updateRacer(String id, RacerDTO updatedRace) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACER_PATH).build(id);

        RequestEntity<RacerDTO> request = RequestEntity.put(url)
                .accept(mediaType)
                .body(updatedRace);
        ResponseEntity<RacerDTO> response = restTemplate.exchange(request, RacerDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<Void> deleteRacer(String id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACER_PATH).build(id);

        RequestEntity<Void> request = RequestEntity.delete(url).build();
        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
        return response;
    }

    @Override
    public ResponseEntity<Void> deleteAllRacers() {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACERS_PATH).build().toUri();

        RequestEntity<Void> request = RequestEntity.delete(url).build();
        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
        return response;
    }
}
