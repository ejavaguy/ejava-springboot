package info.ejava.assignments.race.client.races;

import info.ejava.examples.common.web.ServerConfig;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class RacesAPIClient implements RacesAPI {
    private final URI baseUrl;
    private final RestTemplate restTemplate;
    private final MediaType mediaType;

    public RacesAPIClient(RestTemplate restTemplate, ServerConfig serverConfig, MediaType mediaType) {
        this.restTemplate = restTemplate;
        this.baseUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl()).build().toUri();
        this.mediaType = mediaType;
    }

    @Override
    public ResponseEntity<RaceDTO> createRace(RaceDTO newRace) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACES_PATH).build().toUri();

        RequestEntity<RaceDTO> request = RequestEntity.post(url)
                .accept(mediaType)
                .contentType(mediaType)
                .body(newRace);
        ResponseEntity<RaceDTO> response = restTemplate.exchange(request, RaceDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<RaceListDTO> getRaces(Integer pageSize, Integer pageNumber) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(baseUrl).path(RACES_PATH);
        if (pageSize!=null) {
            uriBuilder = uriBuilder.queryParam("pageSize", pageSize);
        }
        if (pageNumber!=null) {
            uriBuilder = uriBuilder.queryParam("pageNumber", pageNumber);
        }
        URI url = uriBuilder.build().toUri();

        RequestEntity<Void> request = RequestEntity.get(url).accept(mediaType).build();
        ResponseEntity<RaceListDTO> response = restTemplate.exchange(request, RaceListDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<RaceDTO> getRace(String id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACE_PATH).build(id);

        RequestEntity<Void> request = RequestEntity.get(url).accept(mediaType).build();
        ResponseEntity<RaceDTO> response = restTemplate.exchange(request, RaceDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<RaceDTO> updateRace(String id, RaceDTO updatedRace) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACE_PATH).build(id);

        RequestEntity<RaceDTO> request = RequestEntity.put(url)
                .accept(mediaType)
                .body(updatedRace);
        ResponseEntity<RaceDTO> response = restTemplate.exchange(request, RaceDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<RaceDTO> cancelRace(String id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACE_CANCELLATION_PATH).build(id);

        RequestEntity<Void> request = RequestEntity.post(url)
                .accept(mediaType)
                .build();
        ResponseEntity<RaceDTO> response = restTemplate.exchange(request, RaceDTO.class);
        return response;
    }

    @Override
    public ResponseEntity<Void> deleteRace(String id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACE_PATH).build(id);

        RequestEntity<Void> request = RequestEntity.delete(url).build();
        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
        return response;
    }

    @Override
    public ResponseEntity<Void> deleteAllRaces() {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RACES_PATH).build().toUri();

        RequestEntity<Void> request = RequestEntity.delete(url).build();
        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
        return response;
    }
}
