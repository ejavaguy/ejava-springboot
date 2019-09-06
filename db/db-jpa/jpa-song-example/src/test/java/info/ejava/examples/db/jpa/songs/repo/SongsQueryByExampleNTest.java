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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static info.ejava.examples.db.jpa.songs.dto.SongDTOFactory.nextDate;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= NTestConfiguration.class)
@Tag("springboot")
@ActiveProfiles(resolver = TestProfileResolver.class)
@DisplayName("Repository Query By Example")
@Slf4j
public class SongsQueryByExampleNTest {
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;

    private static List<Song> savedSongs = new ArrayList<>();

    @BeforeEach
    void populate() {
        if (savedSongs.isEmpty()) {
            songsRepository.deleteAll();
            IntStream.range(0, 10).forEach(i -> {
                Song song = mapper.map(dtoFactory.make(nextDate));
                savedSongs.add(song);
            });
            songsRepository.saveAll(savedSongs);
        }
    }

    /**
     * This test shows how the default matcher creates a predicate for each
     * non-null value and combines them using AND.
     */
    @Test
    void default_matcher_non_null_and() {
        //given
        Song savedSong = savedSongs.get(0);
        Song probe = Song.builder()
                .title(savedSong.getTitle())
                .artist(savedSong.getArtist())
                .build();

        //when
        List<Song> foundSongs = songsRepository.findAll(
                Example.of(probe),//default matcher is matchingAll() and non-null
                Sort.by("id"));
        //select ...
        //from reposongs_song song0_
        //where song0_.id=0 and song0_.artist=? and song0_.title=?
        //order by song0_.id asc

        //then - not found, default matcher included the primary key
        then(foundSongs).isEmpty();
    }

    /**
     * This test shows how we can combine predicates using an OR.
     */
    @Test
    void matching_any() {
        //given
        Song savedSong = savedSongs.get(0);
        Song probe = Song.builder()
                .title(savedSong.getTitle())
                .artist(savedSong.getArtist())
                .build();

        //when
        List<Song> foundSongs = songsRepository.findAll(
                Example.of(probe, ExampleMatcher.matchingAny()),
                Sort.by("id"));
        //select ...
        // from reposongs_song song0_
        // where song0_.id=0 or song0_.artist=? or song0_.title=?
        // order by song0_.id asc

        //then - not found, default matcher included the primary key
        then(foundSongs).isNotEmpty();
        then(foundSongs.get(0).getId()).isEqualTo(savedSong.getId());
    }

    /**
     * This example shows how we can ignore certain example properties. In this
     * case we are ignoring a built-in type that can never be null.
     */
    @Test
    void ignore_properties() {
        //given
        Song savedSong = savedSongs.get(0);
        Song probe = Song.builder()
                .title(savedSong.getTitle())
                .artist(savedSong.getArtist())
                .build();
        ExampleMatcher ignoreId = ExampleMatcher.matchingAll().withIgnorePaths("id");

        //when
        List<Song> foundSongs = songsRepository.findAll(
                Example.of(probe, ignoreId),
                Sort.by("id"));
        //select ...
        //from reposongs_song song0_
        //where song0_.title=? and song0_.artist=?
        //order by song0_.id asc

        //then
        then(foundSongs).isNotEmpty();
        then(foundSongs.get(0).getId()).isEqualTo(savedSong.getId());
    }

    @Test
    void like_matcher() {
        //given
        Song savedSong = savedSongs.get(0);
        Song probe = Song.builder()
                .title(savedSong.getTitle().substring(2))
                .artist(savedSong.getArtist())
                .build();
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnorePaths("id")
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains());

        //when
        List<Song> foundSongs = songsRepository.findAll(Example.of(probe, matcher), Sort.by("id"));
        //binding parameter [1] as [VARCHAR] - [Earth Wind and Fire]
        //binding parameter [2] as [VARCHAR] - [% a God Unknown%]
        //binding parameter [3] as [CHAR] - [\]
        //select ...
        //from reposongs_song song0_
        //where song0_.artist=? and (song0_.title like ? escape ?)
        //order by song0_.id asc

        //then
        then(foundSongs).isNotEmpty();
        then(foundSongs.get(0).getId()).isEqualTo(savedSong.getId());
    }
}
