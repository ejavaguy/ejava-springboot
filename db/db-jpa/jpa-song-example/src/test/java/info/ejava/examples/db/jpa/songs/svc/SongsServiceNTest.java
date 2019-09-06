package info.ejava.examples.db.jpa.songs.svc;

import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.db.jpa.songs.TestProfileResolver;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.repo.SongsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static info.ejava.examples.common.exceptions.ClientErrorException.NotFoundException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = {NTestConfiguration.class})
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboottest")
@Slf4j
@DisplayName("Songs Service NTest")
public class SongsServiceNTest {
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private SongsService songsService;
    @Autowired
    private SongDTOFactory songDtoFactory;
    @Autowired
    private SongsMapper songsMapper;

    @BeforeEach
    void init() {
        songsRepository.deleteAll();
    }

    void thenSongsEqual(SongDTO result, SongDTO expected) {
        if (expected.getId()==0) { //we don't know what the value will be
            then(result.getId()).isNotZero(); //but it will be assigned
        } else {
            then(result.getId()).isEqualTo(expected.getId());
        }
        then(result.getArtist()).isEqualTo(expected.getArtist());
        then(result.getTitle()).isEqualTo(expected.getTitle());
        then(result.getReleased()).isEqualTo(expected.getReleased());
    }

    @Test
    void create_song() {
        //given
        SongDTO song = songDtoFactory.make();
        //when
        SongDTO result = songsService.createSong(song);
        //then
        then(result.getId()).isNotZero();
        log.info("{}", JsonUtil.instance().marshal(result));
        thenSongsEqual(result, song);
    }

    @Test
    void get_song_exists() {
        //given
        SongDTO song = songDtoFactory.make();
        SongDTO existingSong = songsService.createSong(song);
        //when
        SongDTO returnedSong = songsService.getSong(existingSong.getId());
        //then
        then(returnedSong).isNotNull();
        thenSongsEqual(returnedSong, existingSong);
    }

    @Test
    void get_song_not_exist() {
        //given
        int doesNotExist=1234;
        //when
        NotFoundException ex = catchThrowableOfType(() -> songsService.getSong(doesNotExist),
                NotFoundException.class);
        //then
        log.info("{}", ex);
        then(ex).hasMessage("Song id[%s] not found", doesNotExist);
    }

    @Test
    void get_songs() {
        //given
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastWeek = today.minusDays(7);
        List<Song> songs = songsMapper.map(songDtoFactory.listBuilder().songs(10,10));
        long modified=IntStream.range(0,songs.size())
                .peek(i->songs.set(i, songs.get(i).withReleased(lastWeek)))
                .filter(i->i%2==0)
                .peek(i->songs.set(i, songs.get(i).withReleased(today)))
                .count();
        songsRepository.saveAll(songs);
        //when
        Page<SongDTO> page = songsService.findReleasedAfter(yesterday,
                PageRequest.of(0, 3, Sort.by("id")));
        //then
        then(page.getNumberOfElements()).isEqualTo(3);
        then(page.getTotalElements()).isEqualTo(modified);
    }

    @Test
    void delete_song() {
        //given
        Song song = songsMapper.map(songDtoFactory.make());
        songsRepository.save(song);
        then(songsRepository.existsById(song.getId())).isTrue();
        //when
        songsService.deleteSong(song.getId());
        //then
        then(songsRepository.existsById(song.getId())).isFalse();
    }

}
