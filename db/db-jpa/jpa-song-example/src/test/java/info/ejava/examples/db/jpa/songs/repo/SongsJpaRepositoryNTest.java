package info.ejava.examples.db.jpa.songs.repo;

import info.ejava.examples.db.jpa.songs.TestProfileResolver;
import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.NTestConfiguration;
import info.ejava.examples.db.jpa.songs.dto.SongDTOFactory;
import info.ejava.examples.db.jpa.songs.svc.SongsMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= NTestConfiguration.class)
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
public class SongsJpaRepositoryNTest {
    @Autowired
    private SongsRepository songsRepository;
    @Autowired
    private SongDTOFactory dtoFactory;
    @Autowired
    private SongsMapper mapper;

    @Test
    @Transactional
    void flush() {
        //given
        List<Song> songs = dtoFactory.listBuilder().songs(5,5).stream().map(s->mapper.map(s)).collect(Collectors.toList());
        songsRepository.saveAll(songs);

        //when
        songsRepository.flush();
    }

    @Test
    @Transactional
    void ref_session() {
        //given
        Song song = mapper.map(dtoFactory.make());
        songsRepository.save(song);

        //when - obtaining a reference with a session
        Song dbSongRef = songsRepository.getOne(song.getId());

        //then
        then(dbSongRef).isNotNull();
        then(dbSongRef.getId()).isEqualTo(song.getId());
        then(dbSongRef.getTitle()).isEqualTo(song.getTitle());
    }

    @Test
    void ref_no_session() {
        //given
        Song song = mapper.map(dtoFactory.make());
        songsRepository.save(song);

        //when - obtaining a reference without a session
        Song dbSongRef = songsRepository.getOne(song.getId());

        //then - get a reference with basics
        then(dbSongRef).isNotNull();
        then(dbSongRef.getId()).isEqualTo(song.getId());
        //cannot get entity because session ref was obatined already completed
        assertThatThrownBy(
                () -> dbSongRef.getTitle())
                .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @Transactional
    void ref_not_exist() {
        //given
        int doesNotExist=1234;

        //when -
        Song dbSongRef = songsRepository.getOne(doesNotExist);

        //then - get a reference with basics
        then(dbSongRef).isNotNull();
        then(dbSongRef.getId()).isEqualTo(doesNotExist);
        //entity will actually be tried at this point
        //dbSongRef.getTitle();
        assertThatThrownBy(
                () -> dbSongRef.getTitle())
                .isInstanceOf(EntityNotFoundException.class);
    }


    @Test
    @Transactional
    void session() {
        //given
        Song song = mapper.map(dtoFactory.make());
        songsRepository.save(song);

        //when - obtaining a reference with session active
        Song dbSongRef = songsRepository.getOne(song.getId());

        //then - complete entity is available
        then(dbSongRef.getTitle()).isEqualTo(song.getTitle());
    }

    @Nested
    class Batch {
        private List<Song> savedSongs = new ArrayList<>();

        @BeforeEach
        void populate() {
            IntStream.range(0, 3).forEach(i->savedSongs.add(mapper.map(dtoFactory.make())));
            songsRepository.saveAll(savedSongs);
        }

        @Test
        void deleteAll() {
            songsRepository.deleteAll(savedSongs);
            //delete from reposongs_song where id=?
            //delete from reposongs_song where id=?
            //delete from reposongs_song where id=?
        }

        @Test
        void deleteAllInBatch() {
            songsRepository.deleteInBatch(savedSongs);
            //delete from reposongs_song where id=? or id=? or id=?
        }
    }
}
