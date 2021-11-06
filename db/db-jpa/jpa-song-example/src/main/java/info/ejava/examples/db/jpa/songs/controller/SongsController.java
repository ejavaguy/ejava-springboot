package info.ejava.examples.db.jpa.songs.controller;


import info.ejava.examples.common.web.paging.PageableDTO;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import info.ejava.examples.db.jpa.songs.dto.SongsPageDTO;
import info.ejava.examples.db.jpa.songs.svc.SongsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SongsController {
    public static final String SONGS_PATH="api/songs";
    public static final String SONG_PATH= SONGS_PATH + "/{id}";
    public static final String RANDOM_SONG_PATH = SONGS_PATH + "/random";

    private final SongsService songsService;

    @RequestMapping(path=SONGS_PATH,
            method= RequestMethod.POST,
            consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongDTO> createSong(@RequestBody SongDTO songDTO) {

        SongDTO result = songsService.createSong(songDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath(SONG_PATH)
                .build(result.getId());
        ResponseEntity<SongDTO> response = ResponseEntity.created(uri).body(result);
        return response;
    }

    @RequestMapping(path=SONGS_PATH,
            method= RequestMethod.GET,
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongsPageDTO> getSongs(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            @RequestParam(value = "sort", required = false) String sortString
            ) {
        Pageable pageable = PageableDTO.of(pageNumber, pageSize, sortString).toPageable();
        Page<SongDTO> result = songsService.getSongs(pageable);
        SongsPageDTO resultDTO = new SongsPageDTO(result);

        ResponseEntity<SongsPageDTO> response = ResponseEntity.ok(resultDTO);
        return response;
    }


    @RequestMapping(path=SONG_PATH,
            method=RequestMethod.GET,
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongDTO> getSong(
            @PathVariable(name="id") int id) {

        SongDTO result = songsService.getSong(id);

        ResponseEntity<SongDTO> response = ResponseEntity.ok(result);
        return response;
    }

    @RequestMapping(path=RANDOM_SONG_PATH,
            method=RequestMethod.GET,
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongDTO> getRandomSong() {

        SongDTO result = songsService.getRandomSong();

        ResponseEntity<SongDTO> response = ResponseEntity.ok(result);
        return response;
    }

    @RequestMapping(path=SONG_PATH,
            method=RequestMethod.PUT,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> updateSong(
            @PathVariable("id") int id,
            @RequestBody SongDTO songDTO) {

        songsService.updateSong(id, songDTO);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path=SONG_PATH,
            method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteSong(
            @PathVariable(name="id") int id) {

        songsService.deleteSong(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path=SONGS_PATH,
            method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllSongs() {

        songsService.deleteAllSongs();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @Operation(summary = "This endpoint will find matching Songs based on a provided " +
            "example. The supplied Song example implements exact match only and ignores " +
            "the `id` field. This endpoint supports sorting and paging through request " +
            "query params. The results are returned in a page construct indicating the " +
            "content, total number of matching songs, and the requested pageable properties. " +
            "This paging is 100% inline with Spring Data Pageable constructs.")
    @RequestMapping(path=SONGS_PATH + "/example",
            method=RequestMethod.POST,
            consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<SongsPageDTO> findSongsByExample(
            @Parameter(description="Which page to return based on pageSize.")
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @Parameter(description="The maximum number of elements to provide in single page.")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Parameter(description="An ordered list of property/direction sets expressed as a string. " +
                    "Example: released:DESC,id:ASC")
            @RequestParam(value = "sort", required = false) String sortString,
            @Parameter(description = "Song properties to perform an exact match. ID property is ignored.")
            @RequestBody SongDTO probe) {

        Pageable pageable = PageableDTO.of(pageNumber, pageSize, sortString).toPageable();

        Page<SongDTO> result=songsService.findSongsMatchingAll(probe, pageable);

        SongsPageDTO resultDTO = new SongsPageDTO(result);
        ResponseEntity<SongsPageDTO> response = ResponseEntity.ok(resultDTO);
        return response;
    }
}
