package info.ejava.examples.db.jpa.songs.repo;

import info.ejava.examples.db.jpa.songs.TestProfileResolver;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static info.ejava.examples.db.jpa.songs.dto.SongDTOFactory.nextDate;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={NTestConfiguration.class})
@Tag("springboot")
@ActiveProfiles(resolver = TestProfileResolver.class)
@Slf4j
@DisplayName("Repository Custom Methods")
public class SongsCustomRepositoryNTest {
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;

    private List<Song> savedSongs = new ArrayList<>();

    @BeforeEach
    void populate() {
        songsRepository.deleteAll();
        IntStream.range(0,5).forEach(i->{
            Song song = mapper.map(dtoFactory.make(nextDate));
            savedSongs.add(song);
        });
        songsRepository.saveAll(savedSongs);
    }

    @Test
    void custom_extension(){
        //when
        Optional<Song> randomSong = songsRepository.random();

        //then
        then(randomSong.isPresent()).isTrue();
        then(randomSong.get()).isNotNull();
    }
}
