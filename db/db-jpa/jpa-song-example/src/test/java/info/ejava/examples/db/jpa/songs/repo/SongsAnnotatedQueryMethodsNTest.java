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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static info.ejava.examples.db.jpa.songs.dto.SongDTOFactory.nextDate;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={NTestConfiguration.class})
@Tag("springboot")
@ActiveProfiles(resolver = TestProfileResolver.class)
@Slf4j
@DisplayName("Repository Annotated @Query Methods")
public class SongsAnnotatedQueryMethodsNTest {
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;
    @Autowired
    private EntityManager entityManager;

    private List<Song> savedSongs = new ArrayList<>();

    @BeforeEach
    void populate() {
        songsRepository.deleteAll();
        IntStream.range(0,3).forEach(i->{
            Song song = mapper.map(dtoFactory.make(nextDate));
            if (i==2) {
                song.setTitle(null);
            }
            songsRepository.save(song);
            savedSongs.add(song);
            //log.info("{}", song);
        });
    }

    @Test
    void native_query() {
        //given
        int minLength = savedSongs.stream()
                .mapToInt(s-> (s.getTitle()==null ? 0 : s.getTitle().length()) )
                .max()
                .getAsInt();
        Set<String> titles = savedSongs.stream()
                .filter(s-> s.getTitle()!=null && s.getTitle().length() >= minLength)
                .map(s->s.getTitle())
                .collect(Collectors.toSet());

        //when
        List<String> foundTitles = songsRepository.getTitlesGESizeNative(minLength);
        log.info("title size GE '{}' found {}", minLength, foundTitles);

        //then
        then(new HashSet<>(foundTitles)).isEqualTo(titles);
    }

    @Test
    void findBy_size_of_title() {
        //given
        int minLength = savedSongs.stream()
                .mapToInt(s-> (s.getTitle()==null ? 0 : s.getTitle().length()) )
                .max()
                .getAsInt();
        Set<Integer> ids = savedSongs.stream()
                .filter(s-> s.getTitle()!=null && s.getTitle().length() >= minLength)
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Song> foundSongs = songsRepository.findByTitleGESize(minLength);
        //select ...
        // from reposongs_song song0_
        // where length(song0_.title)>=?
        log.info("title size GE '{}' found {}", minLength, foundSongs);

        //then
        Set<Integer> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(ids);
    }

    @Test
    void findBy_size_of_artist() {
        //given
        int minLength = savedSongs.stream()
                .mapToInt(s-> (s.getArtist()==null ? 0 : s.getArtist().length()) )
                .max()
                .getAsInt();
        Set<Integer> ids = savedSongs.stream()
                .filter(s->s.getArtist()!=null && s.getArtist().length() >= minLength)
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when - using JPA
        TypedQuery<Song> query = entityManager
                .createNamedQuery("Song.findByArtistGESize", Song.class)
                .setParameter("length", minLength);
        List<Song> jpaFoundSongs = query.getResultList();
        //then
        Set<Integer> foundIds = jpaFoundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(ids);

        //when - using Spring Data
        List<Song> foundSongs = songsRepository.findByArtistGESize(minLength);
        //select ...
        // from reposongs_song song0_
        // where length(song0_.artist)>=?
        log.info("title size GE '{}' found {}", minLength, foundSongs);

        //then
        foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(ids);
    }
}
